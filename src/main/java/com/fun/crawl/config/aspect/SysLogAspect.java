package com.fun.crawl.config.aspect;


import com.fun.crawl.base.enums.OperationStatusEnum;
import com.fun.crawl.base.utils.UrlUtil;
import com.fun.crawl.base.utils.UserUtil;
import com.fun.crawl.config.annotation.SysLog;
import com.fun.crawl.model.dto.SysLogDTO;
import com.fun.crawl.service.SysLogService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Aspect
@Slf4j
public class SysLogAspect {

//    @Autowired
//    private RabbitTemplate rabbitTemplate;
//

    @Autowired
    private SysLogService sysLogService;


    @Around("execution(public com.fun.crawl.base.utils.ApiResult *(..))")
//    @Around(value = "@annotation(com.fisher.common.annotation.SysLog)")
    public Object handlerControllerMethod(ProceedingJoinPoint pjp) {
        Object result = null;
        Signature signature = pjp.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method targetMethod = methodSignature.getMethod();

        long startTime = System.currentTimeMillis();
        SysLogDTO sysLogDTO = new SysLogDTO();
        // 需要记录日志存库
        if (targetMethod.isAnnotationPresent(SysLog.class)) {
            Gson gson = new Gson();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

            // 获取注解
            SysLog sysLog = targetMethod.getAnnotation(SysLog.class);
            sysLogDTO
                    .setServiceId(sysLog.serviceId())
                    .setModuleName(sysLog.moduleName())
                    .setActionName(sysLog.actionName())
                    .setParams(gson.toJson(request.getParameterMap()))
                    .setRemoteAddr(UrlUtil.getRemoteHost(request))
                    .setMethod(request.getMethod())
                    .setRequestUri(request.getRequestURI())
                    .setUserAgent(request.getHeader("user-agent"));

            // 获取当前用户名
            try {
                String username = UserUtil.getUserName(request);
                sysLogDTO.setCreateBy(username);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        try {
            result = pjp.proceed();
            sysLogDTO.setStatus(OperationStatusEnum.SUCCESS.getCode());
        } catch (Throwable e) {
            sysLogDTO.setException(UrlUtil.getTrace(e));
            sysLogDTO.setStatus(OperationStatusEnum.FAIL.getCode());
        }
        // 本次操作用时（毫秒）
        long elapsedTime = System.currentTimeMillis() - startTime;
        log.info("[{}]use time: {}", pjp.getSignature(), elapsedTime);
        sysLogDTO.setTime(String.valueOf(elapsedTime));


        // 发送消息到 系统日志队列
        if (targetMethod.isAnnotationPresent(SysLog.class)) {
//            rabbitTemplate.convertAndSend(MqQueueNameConstant.SYS_LOG_QUEUE, sysLogDTO);

            com.fun.crawl.model.SysLog sysLog = new com.fun.crawl.model.SysLog();
            BeanUtils.copyProperties(sysLogDTO, sysLog);
            sysLogService.save(sysLog);

        }
        return result;
    }


}
