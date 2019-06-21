package com.fun.crawl.service;


import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.evernote.auth.EvernoteAuth;
import com.evernote.auth.EvernoteService;
import com.evernote.clients.ClientFactory;
import com.evernote.clients.NoteStoreClient;
import com.evernote.clients.UserStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteList;
import com.evernote.edam.notestore.NoteStore;
import com.evernote.edam.type.*;
import com.evernote.edam.userstore.AuthenticationResult;
import com.evernote.thrift.TException;
import com.evernote.thrift.protocol.TBinaryProtocol;
import com.evernote.thrift.transport.THttpClient;
import com.evernote.thrift.transport.TTransportException;
import com.fun.crawl.model.NoteBook;
import com.fun.crawl.model.NoteUser;
import com.fun.crawl.model.dto.yinxiang.LinkNoteListDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

@Service
@Slf4j
public class YingXiangService {


    public static final String AUTH_TOKEN = "S=s53:U=150a708:E=16b9316930b:C=16b6f0a0c90:P=1cd:A=en-devtoken:V=2:H=9a8c3e34070a6df2a592abaa2e307118";
    private final static ArrayBlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<>(100);

    private final static RejectedExecutionHandler HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

    private static ThreadPoolExecutor executorService = new ThreadPoolExecutor(10, 15, 200000, TimeUnit.MILLISECONDS, WORK_QUEUE, HANDLER);

    private static ExecutorService pool = Executors.newCachedThreadPool();// 启用多线程

    BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(200); //固定为200的线程队列

    ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 6, 1, TimeUnit.DAYS, queue);

    private static Map<String, Object> CacheMap = new HashMap<>();

    @Autowired
    private RedisService redisService;


    @Autowired
    private NoteUserService noteUserService;
    @Autowired
    private NoteSysbookService noteSysbookService;
    @Autowired
    private NoteBookService noteBookService;


    public static void main(String[] args) throws EDAMUserException, EDAMSystemException, TException, EDAMNotFoundException {

//        List<LinkedNotebook> linkedNotebooks = YingXiangService.listLinkNotebook(AUTH_TOKEN);
//
//        System.out.println(linkedNotebooks);

    }


    /**
     * 同步单个笔记本 把账号1的笔记本同步到账号2
     *
     * @return
     */
    public synchronized int sysNoteBookName(final String name) {
        List<NoteUser> list = noteUserService.list();

        Optional<NoteUser> mainUserOpt = list.stream().filter(noteUser -> noteUser.getMainAccount().intValue() == 1).findFirst();
        Optional<NoteUser> flowUserOpt = list.stream().filter(noteUser -> noteUser.getMainAccount().intValue() == 0).findFirst();
        final NoteUser mainUser = mainUserOpt.get();
        final NoteUser flowUser = flowUserOpt.get();

        try {
            log.info("用户：main" + YingXiangService.getUserStore(mainUser.getToken()).getUser().toString());
            log.info("用户：flow" + YingXiangService.getUserStore(flowUser.getToken()).getUser().toString());
        } catch (EDAMUserException e) {
            e.printStackTrace();
        } catch (EDAMSystemException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }


        List<LinkedNotebook> linkedNotebooks = listLinkNotebook(mainUser.getToken());
        for (LinkedNotebook linkedNotebook : linkedNotebooks) {
            String shareName = linkedNotebook.getShareName();

            if (StringUtils.isNotBlank(name)) {
                if (shareName.contains(name)) {
                    log.info("linkedNotebook 名称：" + shareName);

                    copyLinkNoteBooksToNewByUser(linkedNotebook, mainUser, flowUser);

                }
            } else {
//                    copyLinkNoteBooksToNewByUser(linkedNotebook, mainUser, flowUser);
            }


        }
        return 0;
    }

    /**
     * 同步所有的笔记本 把账号1的笔记本同步到账号2
     *
     * @return
     */
    public int sysAllNoteBook() {
        List<NoteUser> list = noteUserService.list();
        Stream<NoteUser> stream = list.stream();
        Optional<NoteUser> mainUserOpt = stream.filter(noteUser -> noteUser.getMainAccount().intValue() == 1).findFirst();
        Optional<NoteUser> flowUserOpt = stream.filter(noteUser -> noteUser.getMainAccount().intValue() == 0).findFirst();
        NoteUser mainUser = mainUserOpt.get();
        NoteUser flowUser = flowUserOpt.get();
        List<LinkedNotebook> linkedNotebooks = listLinkNotebook(mainUser.getToken());
        for (LinkedNotebook linkedNotebook : linkedNotebooks) {
            String shareName = linkedNotebook.getShareName();
            log.info("linkedNotebook 名称：" + shareName);
            copyLinkNoteBooksToNewByUser(linkedNotebook, mainUser, flowUser);
        }
        return 0;
    }


    /**
     * 带有数据-------笔记
     * 复制连接Link笔记本中的某一条数据...
     *
     * @param token
     * @param note
     * @return
     */
    public int shareNoteBook(String token, List<String> notebookGuids, String email) {
        NoteStoreClient noteStore = getNoteStore(token);
        List<Notebook> notebooks = null;
        try {
            notebooks = noteStore.listNotebooks();
        } catch (EDAMUserException e) {
            e.printStackTrace();
        } catch (EDAMSystemException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
        int i = 0;
        for (String notebookGuid : notebookGuids) {
            SharedNotebook sharedNotebook = new SharedNotebook();
            sharedNotebook.setEmail(email);
            sharedNotebook.setNotebookGuid(notebookGuid);
            sharedNotebook.setNotebookModifiable(false);
            sharedNotebook.setRequireLogin(true);
            sharedNotebook.setServiceCreated(new Date().getTime());
            sharedNotebook.setPrivilege(SharedNotebookPrivilegeLevel.READ_NOTEBOOK_PLUS_ACTIVITY);
            try {

                log.info("***分享笔记本***GUID** " + notebookGuid + "***收件人邮箱*** " + email);
                SharedNotebook sbsqook = noteStore.createSharedNotebook(sharedNotebook);
                String notebootkGuis = sbsqook.getNotebookGuid();

                log.info("***发送笔记本提醒***GUID** " + notebootkGuis + "***收件人邮箱*** " + email);
                List<String> arrlist = new ArrayList<>();
                arrlist.add(email);
//                Notebook notebook = noteStore.getNotebook(notebookGuid);

//                noteStore.sendMessageToSharedNotebookMembers(notebookGuid, "笔记已发送,点击链接获取", arrlist);


                i = ++i;
            } catch (EDAMUserException e) {
                e.printStackTrace();
            } catch (EDAMNotFoundException e) {
                e.printStackTrace();
            } catch (EDAMSystemException e) {
                e.printStackTrace();
            } catch (TException e) {
                e.printStackTrace();
            }
        }
        log.info("***此次分享总共笔记本数：" + notebookGuids.size() + "***成功分享数：" + i);
        return i;
    }


    /**
     * @param linkedNotebook
     * @param linkTokenUser
     * @param newTokenUser
     * @return
     */
    public Boolean copyLinkNoteBooksToNewByUser(LinkedNotebook linkedNotebook, NoteUser linkTokenUser, NoteUser newTokenUser) {
        String bookName = linkedNotebook.getShareName();
        Notebook notebook = new Notebook();
        notebook.setName(bookName);
        NoteStoreClient noteStore = getNoteStore(newTokenUser.getToken());
        NoteBook dbNoteBook = noteBookService.slectByNameLikeAndUserId(newTokenUser.getId(), bookName);
        try {
            try {
                log.info("开始创建笔记本：" + bookName);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                notebook = noteStore.createNotebook(notebook);

                if (dbNoteBook == null) {
                    NoteBook noteBook = new NoteBook();
                    noteBook.setCreateTime(new Date());
                    noteBook.setIsDeleted(0L);
                    noteBook.setNotebookGuid(notebook.getGuid());
                    noteBook.setNotebookName(notebook.getName());
                    noteBook.setNotebookType(1L);
                    noteBook.setNoteUserId(newTokenUser.getId());
                    noteBookService.save(noteBook);
                }



            } catch (EDAMUserException e) {
                System.err.println("Error: " + e.getErrorCode().toString()
                        + " parameter: " + e.getParameter());
                String string = e.getErrorCode().toString();
                log.info("笔记本已经存在：" + bookName + "所有笔记本中查找匹配.......");
                List<Notebook> notebooks = null;
                if (dbNoteBook != null) {//如果
                    String notebookGuid = dbNoteBook.getNotebookGuid();
                    notebook = noteStore.getNotebook(notebookGuid);
                    Date date = new Date(notebook.getServiceUpdated());
                    dbNoteBook.setModifyTime(date);
                    noteBookService.updateById(dbNoteBook);
                } else {
                    try {

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException aa) {
                            aa.printStackTrace();
                        }
                        notebooks = noteStore.listNotebooks();
                        Optional<Notebook> first = notebooks.stream().filter(book -> book.getName().equals(bookName)).findFirst();
                        boolean present = first.isPresent();
                        if (present) {
                            notebook = first.get();
                            log.info("笔记本已经存在：" + bookName + "上次更新时间..." + DateUtil.format(new Date(notebook.getServiceUpdated()), DatePattern.NORM_DATETIME_PATTERN));
                            if (dbNoteBook == null) {
                                NoteBook noteBook = new NoteBook();
                                noteBook.setCreateTime(new Date());
                                noteBook.setIsDeleted(0L);
                                noteBook.setNotebookGuid(notebook.getGuid());
                                noteBook.setNotebookName(notebook.getName());
                                noteBook.setNotebookType(1L);
                                noteBook.setNoteUserId(newTokenUser.getId());
                                noteBookService.save(noteBook);
                            }

                        }
                    } catch (EDAMUserException e1) {
                        e1.printStackTrace();
                    } catch (EDAMSystemException e1) {
                        e1.printStackTrace();
                    } catch (TException e1) {
                        e1.printStackTrace();
                    }
                }
            } catch (EDAMSystemException e) {
                e.printStackTrace();
            } catch (TException e) {
                e.printStackTrace();
            }

            //如果笔记本中的资料 10天没有更新过，就不需要另外处理
            String tonoteBookgUid = notebook.getGuid();
            NoteFilter noteFilter = new NoteFilter();
            noteFilter.setOrder(NoteSortOrder.CREATED.getValue());//根据创建时间排序
            noteFilter.setNotebookGuid(tonoteBookgUid);
            NoteList newNotes = null;
            /**
             * 获取本子中所有的笔记哦
             */
            newNotes = ListAllNotes(noteStore, noteFilter, null);

            List<Note> notesNew = newNotes.getNotes();
            LinkNoteListDto dto = listLinkNote(linkedNotebook, linkTokenUser.getToken());
            if (dto == null) {
                return false;
            }
            NoteStore.Client client = dto.getClient();
            String authenticationToken = dto.getAuthenticationToken();
            NoteList noteList = dto.getNoteList();
            List<Note> notes = noteList.getNotes();
            NoteAttributes noteAttributes = new NoteAttributes();
            noteAttributes.setAuthor("知识汇聚团队(微信：konw-who888");
//            noteAttributes.setSourceURL("kanlem.com");

            Boolean isBookUpdate = false;
            for (final Note note : notes) {

                if (notesNew != null && notesNew.size() > 0) {
                    Optional<Note> first = notesNew.stream().filter(thisNote -> thisNote.getTitle().equals(note.getTitle())).findFirst();
                    boolean present = first.isPresent();
                    if (present) {
                        Note sysnote = first.get();
                        //获取数据更新了，需要重新同步数据的笔记
                        log.info("笔记本：" + linkedNotebook.getShareName() + "标题：" + sysnote.getTitle() + "------sysnote更新时间：" + sysnote.getUpdated() + "------note更新时间：" + note.getUpdated());
                        if (sysnote.getUpdated() < note.getUpdated()) {
                            Note copy = client.getNote(authenticationToken, note.getGuid(), true, true, true, true);
                            sysnote.setResources(copy.getResources());
                            sysnote.setTitle(copy.getTitle());
                            String content = replaceContent(copy);
                            sysnote.setContent(content);
                            sysnote.setUpdated(note.getUpdated());
                            noteStore.updateNote(sysnote);
                            isBookUpdate = true;
                        }
                    } else {
                        log.info("笔记本：" + linkedNotebook.getShareName() + "******创建笔记*********标题：" + note.getTitle());
                        Note copy = client.getNote(authenticationToken, note.getGuid(), true, true, true, true);
                        copy.setAttributes(noteAttributes);
                        copy.setNotebookGuid(tonoteBookgUid);
                        String content = replaceContent(copy);
                        copy.setContent(content);
                        noteStore.createNote(copy);
                    }
                } else {
                    log.info("笔记本：" + linkedNotebook.getShareName() + "******创建笔记*********标题：" + note.getTitle());
                    Note copy = client.getNote(authenticationToken, note.getGuid(), true, true, true, true);
                    copy.setAttributes(noteAttributes);
                    copy.setNotebookGuid(tonoteBookgUid);
                    String content = replaceContent(copy);
                    copy.setContent(content);
                    noteStore.createNote(copy);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


                //java8 方式
//                executorService.execute(() -> {

//                });
            }

            if (isBookUpdate) {
                dbNoteBook.setModifyTime(new Date());
                noteBookService.updateById(dbNoteBook);
            }

        } catch (EDAMUserException e) {
            e.printStackTrace();
        } catch (EDAMSystemException e) {
            e.printStackTrace();
        } catch (EDAMNotFoundException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 添加广告宣传......
     *
     * @param copy
     * @return
     */
    private static String replaceContent(Note copy) {
        String content = copy.getContent();

        /**
         * <span style="box-sizing: border-box; -webkit-tap-highlight-color: transparent; -webkit-font-smoothing: antialiased; word-break: break-word; word-wrap: break-word;">
         * <span style="color: rgb(214, 214, 214); font-family: font-text; font-weight: 400; line-height: 30px; font-size: 10pt;">杨波团队 微信：dedao777</span></span>
         * </span>
         * <br>
         */
        if (content.contains("杨波团队") || content.contains("dedao777")) {
            content = content.replace("杨波团队", "知识汇聚团队");
            content = content.replace("dedao777", "konw-who888 ");
        } else {
            content = content.replace("<en-note>", "<en-note><p> <span style=\"color: rgb(214, 214, 214); font-family: font-text; font-weight: 400; line-height: 30px; font-size: 10pt;\">知识汇聚团队 微信：konw-who888 </span></p>");
        }


        return content;
    }



    /**
     * 带有数据-------笔记
     * 复制连接Link笔记本中的某一条数据...
     *
     * @param token
     * @param note
     * @return
     */
    public Note copyLinkBook(Note note, NoteStore.Client client, String authenticationToken) {
        try {
            Note clientNote = client.getNote(authenticationToken, note.getGuid(), true, true, true, true);
            return clientNote;
        } catch (EDAMUserException e) {
            e.printStackTrace();
        } catch (EDAMSystemException e) {
            e.printStackTrace();
        } catch (EDAMNotFoundException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取所有分享的链接笔记本中的笔记
     *
     * @return
     * @throws Exception
     */
    public LinkNoteListDto listLinkNote(LinkedNotebook linkedNotebook, String token) {
        log.info("*****linkedNotebook******笔记本名称：" + linkedNotebook.getShareName());
        String shareKey = linkedNotebook.getShareKey();
        THttpClient tHttpClient = null;
        try {
            tHttpClient = new THttpClient(linkedNotebook.getNoteStoreUrl());
            TBinaryProtocol tBinaryProtocol = new TBinaryProtocol(tHttpClient);
            NoteStore.Client client = new NoteStore.Client(tBinaryProtocol);
            AuthenticationResult authenticationResult = client.authenticateToSharedNotebook(shareKey, token);
            String authenticationToken = authenticationResult.getAuthenticationToken();
            SharedNotebook sharedNotebookByAuth = client.getSharedNotebookByAuth(authenticationToken);
            NoteFilter noteFilter = new NoteFilter();
            noteFilter.setOrder(NoteSortOrder.CREATED.getValue());//根据创建时间排序
            noteFilter.setNotebookGuid(sharedNotebookByAuth.getNotebookGuid());

            NoteList notes = ListAllLinkNotes(client, authenticationToken, noteFilter, null);

            LinkNoteListDto dto = new LinkNoteListDto();
            dto.setClient(client);
            dto.setAuthenticationToken(authenticationToken);
            dto.setShareKey(shareKey);
            dto.setNoteList(notes);
            return dto;
        } catch (EDAMUserException | EDAMNotFoundException | EDAMSystemException | TException e) {
            log.error("*****linkedNotebook******笔记本名称：" + linkedNotebook.getShareName(), e);
            NoteStoreClient noteStore = getNoteStore(token);
            int i = 0;
            try {
                i = noteStore.expungeLinkedNotebook(linkedNotebook.getGuid());
            } catch (EDAMUserException e1) {
                e1.printStackTrace();
            } catch (EDAMNotFoundException e1) {
                e1.printStackTrace();
            } catch (EDAMSystemException e1) {
                e1.printStackTrace();
            } catch (TException e1) {
                e1.printStackTrace();
            }
            log.error("****删除*****linkedNotebook******笔记本名称：" + linkedNotebook.getShareName() + "状态：" + i);
        }
        return null;
    }

    //分页逻辑

    /**
     * 获取所有链接笔记本中的 笔记本数
     *
     * @param client
     * @param authenticationToken
     * @param noteFilter
     * @param notes
     * @return
     * @throws EDAMUserException
     * @throws EDAMSystemException
     * @throws TException
     * @throws EDAMNotFoundException
     */
    public static NoteList ListAllLinkNotes(NoteStore.Client client, String authenticationToken, NoteFilter noteFilter, List<Note> notes) throws EDAMUserException, EDAMSystemException, TException, EDAMNotFoundException {
        try {
            Thread.currentThread().sleep(1000);
        } catch (InterruptedException e2) {
            e2.printStackTrace();
        }

        if (notes == null || notes.size() <= 0) {
            notes = new ArrayList<>();
            NoteList noteList = client.findNotes(authenticationToken, noteFilter, 0, 50);
            List<Note> add = noteList.getNotes();
            notes.addAll(add);
            if (noteList.getTotalNotes() > notes.size()) {//还没有取全...
                noteList = ListAllLinkNotes(client, authenticationToken, noteFilter, notes);
            }
            return noteList;
        } else {//没有取全的逻辑
            NoteList noteList = client.findNotes(authenticationToken, noteFilter, notes.size(), 50);
            List<Note> add = noteList.getNotes();
            notes.addAll(add);
            if (noteList.getTotalNotes() > notes.size()) {
                noteList = ListAllLinkNotes(client, authenticationToken, noteFilter, notes);
            }
            noteList.setNotes(notes);
            return noteList;
        }

    }


    /**
     * 获取所有笔记本中的 笔记本数
     *
     * @param noteStore
     * @param noteFilter
     * @param notes
     * @return
     * @throws EDAMUserException
     * @throws EDAMSystemException
     * @throws TException
     * @throws EDAMNotFoundException
     */
    public static NoteList ListAllNotes(NoteStoreClient noteStore, NoteFilter noteFilter, List<Note> notes) throws EDAMUserException, EDAMSystemException, TException, EDAMNotFoundException {

        try {
            Thread.currentThread().sleep(1000);
        } catch (InterruptedException e2) {
            e2.printStackTrace();
        }

        if (notes == null || notes.size() <= 0) {
            notes = new ArrayList<>();
            NoteList noteList = noteStore.findNotes(noteFilter, 0, 50);
            List<Note> add = noteList.getNotes();
            notes.addAll(add);
            if (noteList.getTotalNotes() > notes.size()) {//还没有取全...
                noteList = ListAllNotes(noteStore, noteFilter, notes);
            }
            return noteList;
        } else {//没有取全的逻辑
            NoteList noteList = noteStore.findNotes(noteFilter, notes.size(), 50);
            List<Note> add = noteList.getNotes();
            notes.addAll(add);
            if (noteList.getTotalNotes() > notes.size()) {
                noteList = ListAllNotes(noteStore, noteFilter, notes);
            }
            noteList.setNotes(notes);
            return noteList;
        }

    }


    /**
     * 获取所有分享的链接笔记本
     *
     * @return
     * @throws Exception
     */
    public List<LinkedNotebook> listLinkNotebook(String token) {
        try {
            List<LinkedNotebook> linkedNotebooks = getNoteStore(token).listLinkedNotebooks();
            return linkedNotebooks;
        } catch (EDAMUserException e) {//这里失败，需要发送邮箱告知管理人员
            e.printStackTrace();
        } catch (EDAMNotFoundException e) {
            e.printStackTrace();
        } catch (EDAMSystemException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取所有的笔记本
     *
     * @return
     * @throws Exception
     */
    public List<Notebook> listNotebook(String token) {
        List<Notebook> notebooks = null;
        try {
            notebooks = getNoteStore(token).listNotebooks();
            return notebooks;
        } catch (EDAMUserException e) {
            e.printStackTrace();
        } catch (EDAMSystemException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取操作笔记Client
     *
     * @param auth_token
     * @return
     */
    public NoteStoreClient getNoteStore(String auth_token) {
        Object o = CacheMap.get(auth_token);
        if (o != null) {
            return (NoteStoreClient) o;
        }

        EvernoteAuth evernoteAuth = new EvernoteAuth(EvernoteService.YINXIANG, auth_token);
        ClientFactory factory = new ClientFactory(evernoteAuth);
        try {
            NoteStoreClient noteStore = factory.createNoteStoreClient();
            CacheMap.put(auth_token, noteStore);
            return noteStore;
        } catch (EDAMUserException e) {
            e.printStackTrace();
            if (e.getErrorCode().name().equals("AUTH_EXPIRED")) {//发送邮箱哦.................


            }


        } catch (EDAMSystemException e) {
            e.printStackTrace();
            if (e.getErrorCode().name().equals("RATE_LIMIT_REACHED")) {//发送邮箱哦.................
                int rateLimitDuration = e.getRateLimitDuration();
                log.error("连接限制...........请" + rateLimitDuration + "s后再试");
            }

        } catch (TException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取用户Client
     *
     * @param auth_token
     * @return
     */
    public static UserStoreClient getUserStore(String auth_token) {
        EvernoteAuth evernoteAuth = new EvernoteAuth(EvernoteService.YINXIANG, auth_token);
        ClientFactory factory = new ClientFactory(evernoteAuth);
        UserStoreClient userStoreClient = null;
        try {
            userStoreClient = factory.createUserStoreClient();
            return userStoreClient;
        } catch (TTransportException e) {
            e.printStackTrace();

        }
        return null;
    }


}

