package com.song.common.idempotent;

import com.alibaba.fastjson.JSONObject;
import com.song.common.cache.RedisCacheManager;
import com.song.common.utils.MD5Utils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Idempotent interceptor
 */
public class IdempotentInterceptor implements MethodInterceptor{

    private RedisCacheManager redisCacheManager;


    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // scan method annotation and set datasource
        if (invocation.getMethod().isAnnotationPresent(Idempotent.class)) {
            Idempotent idempotent = invocation.getMethod().getAnnotation(Idempotent.class);
            long expire = idempotent.timeout();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

            String md5Str = MD5Utils.getMD5(JSONObject.toJSONString(request));
            if (redisCacheManager.hasKey(md5Str)) {
                return redisCacheManager.get(md5Str);
            } else {
                Object result = invocation.proceed();
                redisCacheManager.set(md5Str, request, expire);
                return result;
            }
        }

        return invocation.proceed();
    }
}
