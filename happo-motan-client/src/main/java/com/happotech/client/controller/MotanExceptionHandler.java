package com.happotech.client.controller;

import com.happotech.actmgr.motan.vo.ResultVO;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Optional;

/**
 * 页面统一异常
 * @author songxulin
 * @date 2021/4/15
 */
@ControllerAdvice
public class MotanExceptionHandler {
    @ExceptionHandler(value =Exception.class)
    public ResultVO exceptionHandler(Exception e){
        ResultVO<Object> vo = new ResultVO<>();
        vo.code="000000";
        vo.message = Optional.ofNullable(e.getMessage()).orElse("调用失败");
        return vo;
    }
}
