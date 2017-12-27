package com.song.common.idempotent;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.qunar.mobile.car.idempotent.api.annotation.IdempotentIgnoreParam;
import com.song.common.cache.RedisCacheManager;
import com.song.common.utils.MD5Utils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.core.MethodParameter;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * Idempotent interceptor
 */
public class IdempotentInterceptor implements MethodInterceptor {

    private RedisCacheManager redisCacheManager;


    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        // scan method annotation and set datasource
        if (invocation.getMethod().isAnnotationPresent(Idempotent.class)) {
            Idempotent idempotent = invocation.getMethod().getAnnotation(Idempotent.class);
            long expire = idempotent.timeout();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

            String md5Str = "idempotent: " + getURL(request) + getRequestParams(invocation);
            if (redisCacheManager.hasKey(md5Str)) {
                // 已请求过，直接返回上次请求结果
                return redisCacheManager.get(md5Str);
            } else {
                // 首次请求，将请求参数，请求处理结果写入redis
                Object result = invocation.proceed();
                redisCacheManager.set(md5Str, request, expire);
                return result;
            }
        }

        return invocation.proceed();
    }

    /**
     * 获取请求链接
     *
     * @param request
     * @return
     */
    private String getURL(HttpServletRequest request) {
        return request.getRequestURL() + request.getRequestURI();
    }

    /**
     * 获取请求参数json str
     * 忽略带IdempotentIgnore注解的参数
     *
     * @param invocation
     * @return
     */
    private String getRequestParams(MethodInvocation invocation) {
        final Map<String, Object> requestParams = Maps.newHashMap();
        Method method = invocation.getMethod();
        Object[] args = invocation.getArguments();

        for (int i = 0; i < args.length; ++i) {
            final Object curArg = args[i];
            MethodParameter param = new MethodParameter(method, i);
            //
            RequestParam requestParam = param.getParameterAnnotation(RequestParam.class);
            if (requestParam != null) {
                String paraName = requestParam.value();
                IdempotentIgnoreParam idempotentIgnoreParam = param.getParameterAnnotation(IdempotentIgnoreParam.class);
                if (idempotentIgnoreParam == null) {
                    // 添加未标注@IdempotentIgnoreParam的参数
                    requestParams.put(paraName, curArg);
                    continue;
                }
            }

            // 只处理业务入参
            if (!(curArg instanceof HttpServletRequest || curArg instanceof BindingResult||curArg instanceof HttpSession)) {
                // 添加未标注@IdempotentIgnoreParam的参数域
                ReflectionUtils.doWithFields(param.getParameterType(), new ReflectionUtils.FieldCallback() {
                    public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                        String name = field.getName();
                        field.setAccessible(true);
                        Object value = field.get(curArg);
                        if (value != null) {
                            requestParams.put(name, value);
                        }

                    }
                }, new ReflectionUtils.FieldFilter() {
                    public boolean matches(Field field) {
                        int modifiers = field.getModifiers();
                        if (Modifier.isStatic(modifiers)) {
                            return false;
                        } else {
                            return !field.isAnnotationPresent(IdempotentIgnoreParam.class);
                        }
                    }
                });
            }
        }

        if (requestParams.isEmpty()) {
            return null;
        } else {
            return MD5Utils.getMD5(JSONObject.toJSONString(requestParams));
        }
    }

    public RedisCacheManager getRedisCacheManager() {
        return redisCacheManager;
    }

    public void setRedisCacheManager(RedisCacheManager redisCacheManager) {
        this.redisCacheManager = redisCacheManager;
    }
}
