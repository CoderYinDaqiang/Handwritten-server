package com.yun.server.http;

import com.yun.server.catalina.Context;
import com.yun.server.catalina.Engine;
import com.yun.server.catalina.Host;
import com.yun.server.utils.SessionUtils;
import com.yun.server.utils.StringUtils;


/**
 *
 * GET /http/demo1/2.html HTTP/1.1
 * Host: localhost:63342
 * Connection: keep-alive
 * Upgrade-Insecure-Requests: 1
 * User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.66 Safari/537.36
 * Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,;q=0.8,application/signed-exchange;v=b3;q=0.9
 * Sec-Fetch-Site:none
 * Sec-Fetch-Mode:navigate
 * Sec-Fetch-Dest:document
 * Accept-Encoding:gzip,deflate,br
 * Accept-Language:zh-CN,zh;q=0.9
 * Cookie:Idea-31d5f4eb=af750744-2158-4f3c-bba9-e7eae63a38fd
 *
 */
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request extends BaseRequest{

    private Map<String, String> requestHeaders;

    private String protocol;

    private String method;

    private String requestURI;

    private String requestURL;

    private InputStream inputStream;

    private String requestStr;

    private Map<String, Context> contextMap;

    private String servletPath;

    private Engine engine;

    private Host host;

    private Context context;

    private Cookie[] cookies;

    private HttpSession session;

    public Request(InputStream inputStream, Engine engine) throws IOException {
        this.contextMap = new HashMap<>();
        this.inputStream = inputStream;
        this.engine = engine;
        parseRequest(this.inputStream);
        if(!StringUtils.isEmpty(this.requestStr)){
            parseRequestLine(this.requestStr);
            parseRequestHeaders(this.requestStr);
            parseURL();
            parseCookie();
        }
        System.out.println("contextPath:" + this.getContextPath() + "\r\nservletPath:" +this.getServletPath());

    }
    private void parseURL() {
        //192.168.1.110:80/application/1.html
        parseHost();
        parseRequestURL();
        parseContextPath();
        parseServletPath();

    }

    public void parseCookie() {
        List<Cookie> cookieList = new ArrayList<>();
        String cookie = this.requestHeaders.get("Cookie");
        if(cookie != null){
            String[] parts = cookie.split(";");
            for (String part : parts) {
                int index = part.indexOf("=");
                Cookie cookiePart = new Cookie(part.substring(0, index).trim(), part.substring(index+1).trim());
                cookieList.add(cookiePart);
            }
            this.cookies = new Cookie[cookieList.size()];
            for (int i = 0; i < cookies.length; i++) {
                cookies[i] = cookieList.get(i);
            }
        }
    }

    private void parseHost() {
        String hostName = this.requestHeaders.get("Host");
        if(hostName.contains(":")){
            int i = hostName.indexOf(":");
            hostName = hostName.substring(0, i);
        }
        List<Host> hostList = this.engine.getHostList();
        for (Host host : hostList) {
            if(hostName.equals(host.getName())){
                this.host = host;
                return;
            }
        }

        String defaultHost = this.engine.getDefaultHost();
        for (Host host : hostList) {
            if(defaultHost.equals(host.getName())){
                this.host = host;
                return;
            }
        }
    }

    //  URI may be :/first/index.html  ; /first ; /index.html
    private void parseContextPath() {
        this.contextMap = this.host.getContextMap();
        String contextPath = StringUtils.subString(this.requestURI, "/", "/");
        if(!StringUtils.isEmpty(contextPath)){
            contextPath = "/" + contextPath;
        } else {
            // if can't intercept, contextPath = /first;/index.html
            contextPath = this.requestURI;
        }
        // there must be verified, because when contextPath=/index.html, contextPath may not be path which server exist.
        Context context = this.contextMap.get(contextPath);
        if(context != null){
            this.context = context;
            return;
        }
        // get current context：analyse application of URL and corresponding docBase
        this.context = this.contextMap.get("/");
    }

    private void parseServletPath() {
        String contextPath = this.context.getPath();
        if("/".equals(contextPath)){
            this.servletPath = this.requestURI;
            return;
        }
        // removePrefix result might be null or ""
        this.servletPath = StringUtils.removePrefix(this.requestURI, contextPath);
        if(StringUtils.isEmpty(this.servletPath)){
            this.servletPath = "/";
        }

    }

    private void parseRequestURL() {
        String host = this.getHeader("Host");
        this.requestURL = host + this.requestURI;
    }

    private void parseRequestHeaders(String requestStr) {
        this.requestHeaders = new HashMap<String,String>();
        int formIndex = this.requestStr.indexOf("\r\n");
        int toIndex = this.requestStr.indexOf("\r\n\r\n");
        String requestHeaders = this.requestStr.substring(formIndex + 2, toIndex);
        String[] parts = requestHeaders.split("\r\n");
        for (String part : parts) {
            String[] split = part.split(":");
            this.requestHeaders.put(split[0].trim(),split[1].trim());
        }
    }

    private void parseRequestLine(String requestStr) {
        int index = this.requestStr.indexOf("\r\n");
        String requestLine = requestStr.substring(0, index);
        String[] parts = requestLine.split(" ");
        this.method = parts[0];
        this.requestURI = parts[1];
        this.protocol = parts[2];
        int i = this.requestURI.indexOf("?");
        if(i != -1){
            this.requestURI = this.requestURI.substring(0, i);
        }
    }

    private void parseRequest(InputStream inputStream) throws IOException {
        int count = 0;
        //表示当前inputStream中可以读的大小
        count = inputStream.available();
        while (count == 0){
            count = inputStream.available();
        }
        byte[] bytes = new byte[count];
        inputStream.read(bytes);
        //没有去处理上传文件情形
        this.requestStr = new String(bytes, 0, count);
    }

    public BaseServletContext getServletContext(){
        return this.context.getServletContext();
    }


    public String getHeader(String key) {
        return requestHeaders.get(key);
    }

    public String getProtocol() {
        return protocol;
    }

    public String getMethod() {
        return method;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public String getServletPath() {
        return this.servletPath;
    }

    public String getContextPath() {
        return this.context.getPath();
    }

    public Context getContext() {
        return context;
    }

    @Override
    public Cookie[] getCookies() {
        return cookies;
    }

    @Override
    public HttpSession getSession() {
        return session;
    }

    public void setSession(HttpSession session) {
        this.session = session;
    }
}
