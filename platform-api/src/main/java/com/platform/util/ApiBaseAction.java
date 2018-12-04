package com.platform.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.platform.entity.TokenEntity;
import com.platform.interceptor.AuthorizationInterceptor;
import com.platform.service.TokenService;
import com.platform.util.request.JsonRequestWrapper;
import org.apache.log4j.Logger;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * @author harmon
 * @ClassName: ApiBaseAction
 * @Description: 基础控制类
 * @date 2016年9月2日
 */
public class ApiBaseAction {
    protected Logger logger = Logger.getLogger(getClass());
    /**
     * 得到request对象
     */
    @Autowired
    protected HttpServletRequest request;
    /**
     * 得到response对象
     */
    @Autowired
    protected HttpServletResponse response;

    @Autowired
    protected TokenService tokenService;

    /**
     * 参数绑定异常
     */
    @ExceptionHandler({BindException.class, MissingServletRequestParameterException.class, UnauthorizedException.class, TypeMismatchException.class})
    @ResponseBody
    public Map<String, Object> bindException(Exception e) {
        if (e instanceof BindException) {
            return toResponsObject(1, "参数绑定异常", e.getMessage());
        } else if (e instanceof UnauthorizedException) {
            return toResponsObject(1, "无访问权限", e.getMessage());
        }
        return toResponsObject(1, "处理异常", e.getMessage());
    }

    /**
     * @param requestCode
     * @param msg
     * @param data
     * @return Map<String,Object>
     * @throws
     * @Description:构建统一格式返回对象
     * @date 2016年9月2日
     * @author zhuliyun
     */
    public Map<String, Object> toResponsObject(int requestCode, String msg, Object data) {
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("errno", requestCode);
        obj.put("errmsg", msg);
        if (data != null)
            obj.put("data", data);
        return obj;
    }

    public Map<String, Object> toResponsSuccess(Object data) {
        Map<String, Object> rp = toResponsObject(0, "执行成功", data);
        logger.info("response:" + rp);
        return rp;
    }

    public Map<String, Object> toResponsMsgSuccess(String msg) {
        return toResponsObject(0, msg, "");
    }

    public Map<String, Object> toResponsSuccessForSelect(Object data) {
        Map<String, Object> result = new HashMap<>(2);
        result.put("list", data);
        return toResponsObject(0, "执行成功", result);
    }

    public Map<String, Object> toResponsFail(String msg) {
        return toResponsObject(1, msg, null);
    }

    /**
     * initBinder 初始化绑定 <br>
     * 这里处理了3种类型<br>
     * 1、字符串自动 trim 去掉前后空格 <br>
     * 2、java.util.Date 转换为 "yyyy-MM-dd HH:mm:ss" 格式<br>
     * 3、java.sql.Date 转换为 "yyyy-MM-dd" 格式<br>
     * 4、java.util.Timestamps 时间转换
     *
     * @param binder  WebDataBinder 要注册的binder
     * @param request 前端请求
     */
    @InitBinder
    public void initBinder(WebDataBinder binder, WebRequest request) {

        // 字符串自动Trim
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
    }

    /**
     * 获取请求方IP
     *
     * @return 客户端Ip
     */
    public String getClientIp() {
//        String xff = request.getHeader("x-forwarded-for");
        String xff = request.getRemoteAddr();
        if (xff == null) {
            return "8.8.8.8";
        }
        if(xff.contains(",")){
            xff = xff.split(",")[0];
        }
        return xff;
    }

    public JSONObject getJsonRequest() {
        JSONObject result = null;
        /*
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader();) {
            char[] buff = new char[1024];
            int len;
            while ((len = reader.read(buff)) != -1) {
                sb.append(buff, 0, len);
            }
            result = JSONObject.parseObject(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        try {
            JsonRequestWrapper jsonRequestWrapper = new JsonRequestWrapper((HttpServletRequest) request);
            String body = jsonRequestWrapper.getBody();
            result = JSONObject.parseObject(body);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }


    //若通过getJsonRequest 获取不到参数  通过下面方法
    public JSONObject getJsonRequestByParameterMap() {
        JSONObject result = null;
        Map map = new HashMap();
        Enumeration enumeration =  request.getParameterNames();
        while (enumeration.hasMoreElements()){
            String name = enumeration.nextElement().toString();
            map.put(name,request.getParameter(name));
        }
        result = new JSONObject(map);
        return result;
    }
    /**
     * 获取请求的用户Id
     *
     * @return 客户端Ip
     */
    public Long getUserId() {
        String token = request.getHeader(AuthorizationInterceptor.LOGIN_TOKEN_KEY);
        //查询token信息
        TokenEntity tokenEntity = tokenService.queryByToken(token);
        if (tokenEntity == null || tokenEntity.getExpireTime().getTime() < System.currentTimeMillis()) {
            return null;
        }
        return tokenEntity.getUserId();
    }
}
