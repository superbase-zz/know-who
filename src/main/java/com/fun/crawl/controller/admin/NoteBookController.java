package com.fun.crawl.controller.admin;


import com.fun.crawl.base.constants.FisherServiceNameConstants;
import com.fun.crawl.base.utils.ApiResult;
import com.fun.crawl.config.annotation.SysLog;
import com.fun.crawl.model.SysUser;
import com.fun.crawl.model.query.NoteBookVoQuery;
import com.fun.crawl.service.NoteBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author jobob
 * @since 2019-06-11
 */
@Slf4j
@RestController
@RequestMapping("/notebook")
@Api(value = "印象笔记Controller", tags = {"印象笔记本操作"})
public class NoteBookController {

    private static final String MODULE_NAME = "印象笔记本模块";

    @Autowired
    private NoteBookService noteBookService;

    @Autowired
    private HttpServletRequest request;


    @SysLog(serviceId = FisherServiceNameConstants.FISHER_USER_SERVICE, moduleName = MODULE_NAME, actionName = "印象笔记本分页查询")
    @ApiOperation(value = "获取用户信息 分页查询", notes = "印象笔记本分页查询", httpMethod = "GET")
    @ApiImplicitParam(name = "query", value = "印象笔记本查询条件", required = false, dataType = "NoteBookVoQuery")
    @GetMapping("/page")
    public ApiResult<NoteBookVoQuery> pageByQuery(NoteBookVoQuery query) {
        return new ApiResult<>(noteBookService.pageNoteBookVoByQuery(query));
    }

//
//    @SysLog(serviceId = FisherServiceNameConstants.FISHER_USER_SERVICE, moduleName = MODULE_NAME, actionName = "主键查询用户信息")
//    @ApiOperation(value = "发送笔记本给邮箱", notes = "发送笔记本给邮箱", httpMethod = "GET")
//    @ApiImplicitParam(name = "id", value = "用户id", required = true, dataType = "integer")
//    @GetMapping("/sendEmail/{id}")
//    public ApiResult<SysUser> get(@PathVariable("id") Integer id) {
//        return new ApiResult<>(sysUserService.getById(id));
//    }


}

