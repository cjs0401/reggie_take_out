package com.itheima.reggie.common;

/**
 * @author jsc
 * @version 1.0
 */
public class BaseContext {

    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setThreadLocal(Long id) {
        threadLocal.set(id);
    }

    public static Long getId(){
        return threadLocal.get();
    }
}
