/*
 * Created 2018-6-29 11:47:12
 */
package cn.com.yting.kxy.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.yting.kxy.web.KxyWebException;
import cn.com.yting.kxy.web.KxyWebException.NotFoundException;
import cn.com.yting.kxy.web.account.AccountException;
import cn.com.yting.kxy.web.message.WebMessageWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

/**
 *
 * @author Azige
 */
@Controller
@ControllerAdvice
@ResponseBody
public class ControllerExceptionHandler implements ErrorController {

    private static final Logger LOG = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @Autowired
    private ErrorAttributes errorAttributes;

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public WebMessageWrapper handleNotFoundException(NotFoundException ex) {
        return handleKxyWebException(ex);
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(KxyWebException.class)
    public WebMessageWrapper handleKxyWebException(KxyWebException ex) {
        return WebMessageWrapper.error(ex.getErrorCode(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(ConcurrencyFailureException.class)
    public WebMessageWrapper handleConcurrencyFailure(ConcurrencyFailureException ex, HttpServletRequest request, HttpServletResponse response) {
        LOG.error("在处理请求 {} 中出现并发冲突异常", request.getRequestURI());
        response.addHeader(HttpHeaders.RETRY_AFTER, "10");
        return WebMessageWrapper.error(KxyWebException.EC_CONCURRENCY_ERROR, ex.getMessage());
    }

    @RequestMapping("/accountLocked")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public WebMessageWrapper handleAccountLocked(HttpServletRequest request) {
        Map<String, Object> error = new HashMap<>();
        error.put("message", "账号已被锁定");
        error.put("accountId", request.getAttribute("accountId"));
        return WebMessageWrapper.error(AccountException.EC_ACCOUNT_LOCKED, error);
    }

    @RequestMapping("/error")
    public WebMessageWrapper handleOtherException(WebRequest request) {
        if (request.getAttribute("javax.servlet.error.status_code", RequestAttributes.SCOPE_REQUEST) == null) {
            Throwable error = errorAttributes.getError(request);
            LOG.error(error.getMessage(), error);
        }
        Map<String, Object> attributes = errorAttributes.getErrorAttributes(request, ErrorAttributeOptions.defaults());
        return WebMessageWrapper.error(KxyWebException.EC_UNKNOW, attributes);
    }
}
