package com.fun.crawl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.evernote.edam.type.LinkedNotebook;
import com.fun.crawl.model.NoteSysbook;
import com.fun.crawl.model.NoteUser;
import com.fun.crawl.service.FileExtendService;
import com.fun.crawl.service.NoteSysbookService;
import com.fun.crawl.service.NoteUserService;
import com.fun.crawl.service.YingXiangService;
import com.fun.crawl.base.utils.PanApiService;
import com.fun.crawl.base.utils.PanCoreUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.fun.crawl.base.utils.PanApiService.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CrawlTest {

    private final static ArrayBlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<>(9);

    private final static RejectedExecutionHandler HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

    private static ThreadPoolExecutor executorService = new ThreadPoolExecutor(16, 16, 1000, TimeUnit.MILLISECONDS, WORK_QUEUE, HANDLER);


    public static final String AUTH_TOKEN_OLD = "S=s6:U=f56ff9:E=16b6ea0c9e3:C=16b4a944358:P=1cd:A=en-devtoken:V=2:H=e0989d4623855d826f0b446c3c1b795f";

    public static final String AUTH_TOKEN = "S=s53:U=150a708:E=16b633b594a:C=16b3f2ed410:P=1cd:A=en-devtoken:V=2:H=ae47b61694e4892fa86b5d14692f2d1e";



    @Autowired
    private NoteUserService noteUserService;


    @Autowired
    private NoteSysbookService noteSysbookService;
    @Autowired
    private FileExtendService fileExtendService;


    @Autowired
    private YingXiangService YingXiangService;


    @Test
    public void contextLoads() {
    }


    @Test
    public void test1() {
//        YingXiangService.sysNoteBookName("大师课");
//        YingXiangService.sysNoteBookName("樊登读书会");
//        YingXiangService.sysNoteBookName("每天听本书2019");


    }




}
