package com.ethansolutions.morpheus.core;

public class RequestContextHolder {
    private static final ThreadLocal<RequestContext> contextHolder = new ThreadLocal<>();

    public static void set(RequestContext context) {
        contextHolder.set(context);
    }

    public static RequestContext get() {
        return contextHolder.get();
    }

    public static void clear() {
        contextHolder.remove();
    }

    public static String getExternalSource() {
        RequestContext ctx = get();
        return ctx != null ? ctx.getExternalSource() : null;
    }

    public static String getExternalId() {
        RequestContext ctx = get();
        return ctx != null ? ctx.getExternalId() : null;
    }
}
