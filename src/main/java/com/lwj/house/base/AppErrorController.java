package com.lwj.house.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 *  web错误，全局处理
 * @author lwj
 */
@Controller
public class AppErrorController implements ErrorController {
    private static final String ERROR_PATH = "/error";

    private ErrorAttributes errorAttributes;

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }

    @Autowired
    public AppErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    /**
     * web页面错误处理
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = ERROR_PATH, produces = "text/html")
    public String errorPageHandler(HttpServletRequest request, HttpServletResponse response) {
        int status = response.getStatus();
        switch (status) {
            case 403:
                //  权限限制提示页面
                return "403";
            case 404:
                //  Not Found提示页面
                return "404";
            case 500:
                //  服务异常提示页面
                return "500";
                default:
                    return "index";
        }
    }

    /**
     *  除web页面外的错误处理，比如Json/XML等
     * @param request
     * @return
     */
    @RequestMapping(value = ERROR_PATH)
    @ResponseBody
    public ApiResponse errorApiHandler(HttpServletRequest request){
        WebRequest webRequest = new ServletWebRequest(request);

        Map<String, Object> attr = this.errorAttributes.getErrorAttributes(webRequest, false);
        int statusCode = getStatusCode(request);

        return ApiResponse.ofMessage(statusCode, String.valueOf(attr.getOrDefault("message", "error")));
    }

    /**
     * 从request中获取错误码
     * @param request
     * @return
     */
    private int getStatusCode(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode != null) {
            return statusCode;
        }
        return 500;
    }
}
