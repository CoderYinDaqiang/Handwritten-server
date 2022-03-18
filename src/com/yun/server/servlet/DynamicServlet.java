package com.yun.server.servlet;

import com.yun.server.catalina.Context;
import com.yun.server.http.Request;
import com.yun.server.http.Response;

import javax.servlet.*;
import java.io.IOException;

public class DynamicServlet extends GenericServlet {

    public static final DynamicServlet INSTANCE = new DynamicServlet();

    private DynamicServlet(){}

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        Request request = (Request) servletRequest;
        Response response = (Response) servletResponse;
        Context context = request.getContext();
        String servletPath = request.getServletPath();
        String servletClass = context.getServletClass(servletPath);
        if(servletClass != null){
            Class<?> aClass = null;
            try {
                aClass = context.getClassLoader().loadClass(servletClass);
                //之前的逻辑是每一个Servlet请求到来都会实例化一个对象出来，那么根据JavaEE规范，每个Servlet对象是单例
                //同时也与实现init()方法有关
                Servlet servlet = context.getServlet(aClass);
                servlet.service(request, response);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
