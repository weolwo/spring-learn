package com.poplar.interceptor;

import com.poplar.annotation.Right;
import com.poplar.utils.CacheTool;
import com.poplar.utils.ResultEnvelope;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.invoke.MethodHandle;
import java.util.UUID;

public class CustomInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        MethodHandle methodHandle = (MethodHandle) handler;
        //这儿可能会是登录校验或者是权限校验，我们公司是在这儿解析方法上的注解，然后实现相关功能
        Right right = methodHandle.getClass().getAnnotation(Right.class);
        if (right == null) {
            ResultEnvelope resultEnvelope = new ResultEnvelope();
            resultEnvelope.setCode(401);
            resultEnvelope.success("暂无权限");
            response.getWriter().print(resultEnvelope);
        }
        //在这儿我们可以使用分布式锁实现对请求的控制
        //当然实际项目中的key一般为全限定类名加方法名再加用户id
        boolean result = CacheTool.setNx(methodHandle.getClass().getDeclaringClass().getName() + methodHandle.getClass().getName() + request.getRemoteUser(), UUID.randomUUID().toString(), 7);
        if (!result) {
            //直接返回
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //如果到这儿说明请求已经成功，可以从新设置redis中key的过期时间
        MethodHandle methodHandle = (MethodHandle) handler;
        CacheTool.pexpire(methodHandle.getClass().getDeclaringClass().getName() + methodHandle.getClass().getName() + request.getRemoteUser(), 500);
    }
}
