package com.fun.crawl.controller.admin;


import com.fun.crawl.base.constants.FisherServiceNameConstants;
import com.fun.crawl.base.utils.ApiResult;
import com.fun.crawl.config.annotation.SysLog;
import com.fun.crawl.model.NoteSysbook;
import com.fun.crawl.model.query.NoteSysBookVoQuery;
import com.fun.crawl.model.query.NoteUserVoQuery;
import com.fun.crawl.service.NoteSysbookService;
import com.fun.crawl.service.NoteUserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author jobob
 * @since 2019-06-20
 */
@RestController
@RequestMapping("/evernote/noteSysbook")
public class NoteSysbookController {




    private static final String MODULE_NAME = "印象笔记本模块";

    @Autowired
    private NoteSysbookService noteSysbookService;

    @Autowired
    private HttpServletRequest request;


    @SysLog(serviceId = FisherServiceNameConstants.FISHER_USER_SERVICE, moduleName = MODULE_NAME, actionName = "印象笔记本分页查询")
    @ApiOperation(value = "印象笔记账号 分页查询", notes = "印象笔记账号分页查询", httpMethod = "GET")
    @ApiImplicitParam(name = "query", value = "印象笔记本查询条件", required = false, dataType = "NoteBookVoQuery")
    @GetMapping("/page")
    public ApiResult<NoteSysBookVoQuery> pageByQuery(NoteSysBookVoQuery query) {
        return new ApiResult<>(noteSysbookService.pageVoByQuery(query));
    }






}

