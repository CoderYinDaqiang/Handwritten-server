package com.yun.server.utils;

import com.yun.server.http.ContextSession;
import com.yun.server.http.Request;
import com.yun.server.http.Response;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.util.*;

public class SessionUtils {

    private static Map<String, ContextSession> sessionPool = new HashMap<>();

    static {
        validateSession();
    }

    //设置了Session的生命周期
    private static void validateSession() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    Set<String> jsessionids = sessionPool.keySet();
                    List<String> expiredSessionIds = new ArrayList<>();
                    for (String jsessionid : jsessionids) {
                        ContextSession session = sessionPool.get(jsessionid);
                        long interval = System.currentTimeMillis() - session.getLastAccessedTime();
                        if(interval > session.getMaxInactiveInterval() * 1000){
                            expiredSessionIds.add(jsessionid);
                        }
                    }
                    for (String expiredSessionId : expiredSessionIds) {
                        sessionPool.remove(expiredSessionId);
                    }
                    //每隔20s运行一次
                    try {
                        Thread.sleep(20 * 1000);
                        System.out.println("----session life cycle----");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }



    public static void getSession(Request request, Response response) {
        Cookie[] cookies = request.getCookies();
        String cookieValue = request.getHeader("Cookie");
        ContextSession session = null;
        if(cookieValue.contains("JSESSIONID")){
            for (Cookie cookie : cookies) {
                if("JSESSIONID".equals(cookie.getName())){
                    session = sessionPool.get(cookie.getValue());
                    if(session == null){
                        session = createNewSession(request, response);
                        break;
                    }
                    session.setLastAccessedTime(System.currentTimeMillis());
                }
            }
        } else {
            session = createNewSession(request, response);
        }
        request.setSession(session);
    }

    private static ContextSession createNewSession(Request request, Response response) {
        String id = generateJSESSIONID();
        ContextSession session = new ContextSession(id, request.getServletContext());
        session.setMaxInactiveInterval(WebXmlUtils.getSessionTimeOut());
        sessionPool.put(id, session);
        Cookie cookie = new Cookie("JSESSIONID", id);
        response.addCookie(cookie);
        return session;
    }


    private static String generateJSESSIONID() {
        return SecurityUtils.toMD5(UUID.randomUUID().toString());
    }
}
