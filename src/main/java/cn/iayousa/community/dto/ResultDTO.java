package cn.iayousa.community.dto;

import cn.iayousa.community.exception.CustomizeErrorCode;
import cn.iayousa.community.exception.CustomizeException;
import lombok.Data;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Data
public class ResultDTO<T> {
    private Integer code;
    private String message;
    private T data;

    public static ResultDTO errorOf(Integer code, String message) {
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(code);
        resultDTO.setMessage(message);
        return resultDTO;
    }

    public static ResultDTO errorOf(CustomizeErrorCode customizeErrorCode) {
        ResultDTO resultDTO = errorOf(customizeErrorCode.getCode(), customizeErrorCode.getMessage());
        return resultDTO;
    }

    public static ResultDTO successOf() {
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(200);
        resultDTO.setMessage("请求成功");
        return resultDTO;
    }

    public static ResultDTO errorOf(CustomizeException ex) {
        ResultDTO resultDTO = errorOf(ex.getCode(), ex.getMessage());
        return resultDTO;
    }

    public static <T> ResultDTO successOf(T t) {
        ResultDTO resultDTO = ResultDTO.successOf();
        resultDTO.setData(t);
        return resultDTO;
    }
}
