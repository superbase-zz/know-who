package com.fun.crawl.controller.admin;


import com.fun.crawl.base.constants.FisherServiceNameConstants;
import com.fun.crawl.base.utils.ApiResult;
import com.fun.crawl.base.utils.UserUtil;
import com.fun.crawl.config.annotation.SysLog;
import com.fun.crawl.model.NoteUser;
import com.fun.crawl.model.dto.SysRoleDTO;
import com.fun.crawl.model.dto.SysUserInfoDTO;
import com.fun.crawl.model.query.NoteBookVoQuery;
import com.fun.crawl.model.query.NoteUserVoQuery;
import com.fun.crawl.service.NoteBookService;
import com.fun.crawl.service.NoteUserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author jobob
 * @since 2019-06-11
 */
@RestController
@RequestMapping("/evernote/noteUser")
public class NoteUserController {



    private static final String MODULE_NAME = "印象笔记本模块";

    @Autowired
    private NoteUserService noteUserService;

    @Autowired
    private HttpServletRequest request;


    @SysLog(serviceId = FisherServiceNameConstants.FISHER_USER_SERVICE, moduleName = MODULE_NAME, actionName = "印象笔记本分页查询")
    @ApiOperation(value = "印象笔记账号 分页查询", notes = "印象笔记账号分页查询", httpMethod = "GET")
    @ApiImplicitParam(name = "query", value = "印象笔记本查询条件", required = false, dataType = "NoteBookVoQuery")
    @GetMapping("/page")
    public ApiResult<NoteUserVoQuery> pageByQuery(NoteUserVoQuery query) {
        return new ApiResult<>(noteUserService.pageNoteUserVoByQuery(query));
    }



    @SysLog(serviceId = FisherServiceNameConstants.FISHER_USER_SERVICE, moduleName = MODULE_NAME, actionName = "查询印象笔记账号")
    @ApiOperation(value = "查询印象笔记账号信息", notes = "查询印象笔记账号", httpMethod = "GET")
    @ApiImplicitParam(name = "id", value = "映像账号ID", required = true, dataType = "integer")
    @GetMapping("/{id}")
    public ApiResult<NoteUser> getInfo(@PathVariable("id") Integer id){
        return new ApiResult<>(noteUserService.getById(id));
    }



    @SysLog(serviceId = FisherServiceNameConstants.FISHER_USER_SERVICE, moduleName = MODULE_NAME, actionName = "修改印象账号")
    @ApiOperation(value = "修改印象账号", notes = "修改印象账号", httpMethod = "PUT")
    @ApiImplicitParam(name = "NoteUser", value = "角色信息", required = true, dataType = "NoteUser")
    @PutMapping
    public ApiResult<Boolean> update(@RequestBody   NoteUser noteUser){
        return new ApiResult<>(noteUserService.updateById(noteUser));
    }



    @SysLog(serviceId = FisherServiceNameConstants.FISHER_USER_SERVICE, moduleName = MODULE_NAME, actionName = "修改印象账号")
    @ApiOperation(value = "添加印象账号", notes = "添加印象账号", httpMethod = "POST")
    @ApiImplicitParam(name = "NoteUser", value = "角色信息", required = true, dataType = "NoteUser")
    @PostMapping
    public ApiResult<Boolean> add(@RequestBody   NoteUser noteUser){
        return new ApiResult<>(noteUserService.save(noteUser));
    }


}

