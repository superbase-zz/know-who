package com.fun.crawl.auth.handler;

import com.fun.crawl.auth.exception.CustomOauth2Exception;
import com.fun.crawl.base.constants.FisherServiceNameConstants;
import com.fun.crawl.base.enums.OperationStatusEnum;
import com.fun.crawl.base.enums.SysLogTypeEnum;
import com.fun.crawl.base.utils.UrlUtil;
import com.fun.crawl.model.dto.SysLogDTO;
import com.fun.crawl.service.SysLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @description: oauth2异常处理器
 */

@Component("customWebResponseExceptionTranslator")
@Slf4j
public class CustomWebResponseExceptionTranslator implements WebResponseExceptionTranslator {

    @Autowired
    private SysLogService sysLogService;

    @Override
    public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String username = request.getParameter("username");
        SysLogDTO sysLogDTO = new SysLogDTO();
        sysLogDTO
                .setCreateBy(username)
                .setRequestUri(request.getRequestURI())
                .setUserAgent(UrlUtil.getRemoteHost(request))
                .setType(SysLogTypeEnum.LOGIN.getCode())
                .setStatus(OperationStatusEnum.FAIL.getCode())
                .setModuleName("auth认证模块")
                .setActionName("登录")
                .setServiceId(FisherServiceNameConstants.FISHER_AUTH)
                .setRemoteAddr(UrlUtil.getRemoteHost(request))
                .setMethod(request.getMethod())
                .setException(UrlUtil.getTrace(e));
//        rabbitTemplate.convertAndSend(MqQueueNameConstant.SYS_LOG_QUEUE, sysLogDTO);


        com.fun.crawl.model.SysLog sysLog = new com.fun.crawl.model.SysLog();
        BeanUtils.copyProperties(sysLogDTO, sysLog);
        sysLogService.save(sysLog);

        log.error(e.getStackTrace().toString());
        if (!(e instanceof OAuth2Exception)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new CustomOauth2Exception(e.getMessage()));
        }
        OAuth2Exception oAuth2Exception = (OAuth2Exception) e;
        return ResponseEntity
                .status(HttpStatus.NOT_ACCEPTABLE)
                .body(new CustomOauth2Exception(oAuth2Exception.getMessage()));
    }
}
