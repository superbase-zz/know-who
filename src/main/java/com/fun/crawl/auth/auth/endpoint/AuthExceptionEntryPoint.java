package com.fun.crawl.auth.auth.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fun.crawl.base.enums.ResponseCodeEnum;
import com.fun.crawl.base.utils.ApiResult;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @description: 自定义AuthExceptionEntryPoint用于token校验失败返回信息
 */
public class AuthExceptionEntryPoint implements AuthenticationEntryPoint {


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException)
            throws ServletException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setContentType("application/json;charset=UTF-8");
        ApiResult<String> result = new ApiResult<>(authException, ResponseCodeEnum.PERMISSION_DEFINED);
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
