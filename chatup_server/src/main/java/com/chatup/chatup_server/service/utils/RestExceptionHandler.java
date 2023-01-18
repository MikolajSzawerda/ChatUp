package com.chatup.chatup_server.service.utils;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ExceptionResponse handleIllegalArgumentException(
            IllegalArgumentException ex, ServletWebRequest request) {
        String msg = "Invalid arguments were provided: "
                + ex.getMessage();
        logger.info(msg);
        return new ExceptionResponse(msg, request.getRequest().getRequestURI());
    }
    @ExceptionHandler({IllegalStateException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody ExceptionResponse handleIllegalStateException(
            Exception ex, ServletWebRequest request) {
        String msg = "Data are in wrong state!: "
                + ex.getMessage();
        logger.error(msg);
        return new ExceptionResponse(msg, request.getRequest().getRequestURI());
    }

    @ExceptionHandler({EntityNotFoundException.class, UsernameNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody ExceptionResponse handleEntityNotFound(
            IllegalArgumentException ex, ServletWebRequest request) {
        String msg = "Can't find entity of given data: "
                + ex.getMessage();
        logger.info(msg);
        return new ExceptionResponse(msg, request.getRequest().getRequestURI());
    }
}
