package com.yun.server.http;

import com.yun.server.catalina.Context;

import java.util.*;

// Every context needs a unique ServletContext
public class ApplicationContext extends BaseServletContext{

    Context context;

    Map<String , Object> map;

    public ApplicationContext(Context context) {
        this.context = context;
        this.map = new HashMap<>();
    }

    @Override
    public String getRealPath(String s) {
        if(!s.startsWith("/") && !s.startsWith("\\")){
            s = "/" + s;
        }
        return this.context.getDocBase() + s;
    }

    @Override
    public void setAttribute(String s, Object o) {
        this.map.put(s, o);
    }

    @Override
    public Object getAttribute(String s) {
        return this.map.get(s);
    }

    @Override
    public void removeAttribute(String s) {
        this.map.remove(s);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        Set<String> keySet = this.map.keySet();
        Enumeration<String> enumeration = Collections.enumeration(keySet);
        return enumeration;
    }
}
