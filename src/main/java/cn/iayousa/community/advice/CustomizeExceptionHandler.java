package cn.iayousa.community.advice;

import cn.iayousa.community.exception.CustomizeException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

@ControllerAdvice
public class CustomizeExceptionHandler {
    // 专门处理你的业务异常
    @ExceptionHandler(CustomizeException.class)
    public ModelAndView handleCustomizeException(HttpServletRequest request,
                                                 CustomizeException ex) {
        ModelAndView mav = new ModelAndView("error");

        // 从异常中获取错误码和信息
        mav.addObject("status", 500);
        mav.addObject("error", "业务错误");
        mav.addObject("message", ex.getMessage()); // 使用异常中的消息
        mav.addObject("path", request.getRequestURI());
        mav.addObject("timestamp", new Date());

        return mav;
    }

    // 处理其他未捕获的异常（作为兜底）
    @ExceptionHandler(Exception.class)
    public ModelAndView handleOtherException(HttpServletRequest request,
                                             Exception ex) throws Exception {
        // 这些异常由 ErrorController 处理
        // 抛出异常，让 ErrorController 接管
        throw ex;
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer code = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (code == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        HttpStatus status = HttpStatus.resolve(code);
        return (status != null) ? status : HttpStatus.INTERNAL_SERVER_ERROR;
    }

}
