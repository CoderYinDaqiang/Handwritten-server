package com.yun.server.http;

import javax.servlet.ServletContext;
import java.util.*;

public class ContextSession extends BaseHttpSession {

    private int defaultTimeOut;

    private ServletContext servletContext;

    private String id;

    private Map<String, Object> sessionMap;

    private long lastAccessedTime;

    public ContextSession(String id, ServletContext servletContext) {
        this.id = id;
        this.servletContext = servletContext;
        this.sessionMap = new HashMap<>();
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public ServletContext getServletContext() {
        return this.servletContext;
    }

    @Override
    public void setAttribute(String s, Object o) {
        this.sessionMap.put(s, o);
    }

    @Override
    public Object getAttribute(String s) {
        return this.sessionMap.get(s);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        Set<String> keySet = this.sessionMap.keySet();
        Enumeration<String> enumeration = Collections.enumeration(keySet);
        return enumeration;
    }

    @Override
    public void removeAttribute(String s) {
        this.sessionMap.remove(s);
    }

    @Override
    public void setMaxInactiveInterval(int i) {
        this.defaultTimeOut = defaultTimeOut;
    }

    @Override
    public int getMaxInactiveInterval() {
        return this.defaultTimeOut;
    }

    @Override
    public long getLastAccessedTime() {
        return this.lastAccessedTime;
    }

    public void setLastAccessedTime(long lastAccessedTime) {
        this.lastAccessedTime = lastAccessedTime;
    }
}
