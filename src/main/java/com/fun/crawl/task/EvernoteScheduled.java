package com.fun.crawl.task;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.evernote.edam.type.LinkedNotebook;
import com.fun.crawl.model.NoteSysbook;
import com.fun.crawl.model.NoteUser;
import com.fun.crawl.service.NoteSysbookService;
import com.fun.crawl.service.NoteUserService;
import com.fun.crawl.service.YingXiangService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class EvernoteScheduled {


    @Autowired
    private NoteUserService noteUserService;


    @Autowired
    private NoteSysbookService noteSysbookService;


    @Autowired
    private YingXiangService yingXiangService;

    /**
     * 同步，需要同步的笔记本，先记录下来，后续再分配同步的账号
     */
//    @Scheduled(fixedDelay = 120 * 60 * 1000) // 固定每5个小时执行一次
    @Scheduled(fixedDelay = 120 * 60 * 1000) // 固定每20分钟执行一次 （执行完后再算时间）
    public void sysAllSysNote() {
        try {
            QueryWrapper<NoteUser> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(NoteUser::getMainAccount, 1);
            NoteUser mainUser = noteUserService.getOne(queryWrapper);
            List<LinkedNotebook> linkedNotebooks = yingXiangService.listLinkNotebook(mainUser.getToken());
            List<NoteSysbook> list = noteSysbookService.list();
            for (LinkedNotebook linkedNotebook : linkedNotebooks) {
                if (list != null && list.size() > 0) {
                    Optional<NoteSysbook> sysbook = list.stream().filter(noteSysbook -> noteSysbook.getNotebookGuid().equals(linkedNotebook.getGuid())).findFirst();
                    if (!sysbook.isPresent()) {
                        NoteSysbook noteSysbook = new NoteSysbook();
                        noteSysbook.setCreateTime(new Date());
                        noteSysbook.setIsDeleted(0l);
                        noteSysbook.setNotebookName(linkedNotebook.getShareName());
                        noteSysbook.setNotebookGuid(linkedNotebook.getGuid());
                        noteSysbookService.save(noteSysbook);
                    }
                } else {
                    NoteSysbook noteSysbook = new NoteSysbook();
                    noteSysbook.setCreateTime(new Date());
                    noteSysbook.setIsDeleted(0l);
                    noteSysbook.setNotebookName(linkedNotebook.getShareName());
                    noteSysbook.setNotebookGuid(linkedNotebook.getGuid());
                    noteSysbookService.save(noteSysbook);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }


    /**
     * 同步分配的笔记本
     */
    @Scheduled(fixedDelay = 60 * 60 * 1000) // 每一个小时执行一次 （执行完后再算时间）
    public void sysNote() {

        log.info("同步开始.............................................");

        try {
            QueryWrapper<NoteUser> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(NoteUser::getMainAccount, 1);
            NoteUser mainUser = noteUserService.getOne(queryWrapper);
            List<LinkedNotebook> linkedNotebooks = yingXiangService.listLinkNotebook(mainUser.getToken());
            List<NoteSysbook> list = noteSysbookService.list();
            for (LinkedNotebook linkedNotebook : linkedNotebooks) {
                if (list != null && list.size() > 0) {
                    Optional<NoteSysbook> sysbook = list.stream().filter(noteSysbook -> noteSysbook.getNotebookGuid().equals(linkedNotebook.getGuid()) && noteSysbook.getUid() != null).findFirst();
                    if (sysbook.isPresent()) {
                        NoteSysbook noteSysbook = sysbook.get();
                        log.info("linkedNotebook 名称：" + linkedNotebook.getShareName());
                        NoteUser flowUser = noteUserService.getById(noteSysbook.getUid());
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        yingXiangService.copyLinkNoteBooksToNewByUser(linkedNotebook, mainUser, flowUser);
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();

        }


        log.info("同步结束.............................................");
    }


}

