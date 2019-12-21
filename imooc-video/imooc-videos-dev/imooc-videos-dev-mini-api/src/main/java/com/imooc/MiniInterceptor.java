package com.imooc;

import com.alibaba.druid.support.json.JSONUtils;
import com.google.gson.Gson;
import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

public class MiniInterceptor implements HandlerInterceptor {

    @Autowired
    public RedisOperator redis;

    public static final String USER_REDIS_SESSION = "user-redis-session";

    Gson gson = new Gson();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        System.out.println("请求url=" + request.getRequestURI());
        String userId = request.getHeader("userId");
        String userToken = request.getHeader("userToken");
        System.out.println("userId=" + userId);
        System.out.println("userToken=" + userToken);
        if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userToken)) {
            String redisUserToken = redis.get(USER_REDIS_SESSION + ":" + userId);
            if (StringUtils.isNotBlank(redisUserToken)) {
                if (!redisUserToken.equals(userToken)) {
                    System.out.println("用户被挤出...");
                    returnErrorResponse(response,new IMoocJSONResult().errorTokenMsg("用户被挤出..."));
                    return false;
                }
            } else {
                System.out.println("失效，请重新登录");
                returnErrorResponse(response,new IMoocJSONResult().errorTokenMsg("失效，请重新登录"));
                return false;
            }
//            if ()
        } else {
            System.out.println("请登录");
            returnErrorResponse(response,new IMoocJSONResult().errorTokenMsg("请登录..."));
            return false;
        }
        return true;
    }

    public void returnErrorResponse(HttpServletResponse response, IMoocJSONResult result) throws Exception{
        OutputStream out = null;

        try {
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/json");
            out = response.getOutputStream();
            out.write(gson.toJson(result).getBytes("utf-8"));
            out.flush();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
