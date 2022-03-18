package com.yun.server.servlet;

import com.yun.server.catalina.Context;
import com.yun.server.http.Request;
import com.yun.server.http.Response;
import com.yun.server.utils.FileUtils;
import com.yun.server.utils.WebXmlUtils;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.File;
import java.io.IOException;

public class DefaultServlet extends GenericServlet {

    public static final DefaultServlet INSTANCE = new DefaultServlet();

    private DefaultServlet(){}

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        Request request = (Request) servletRequest;
        Response response = (Response) servletResponse;
        File file = null;
        Context context = request.getContext();
        String servletPath = request.getServletPath();
        if("/".equals(servletPath)){//no request source :use welcome file
            String welcomeFile = WebXmlUtils.getWelcomeFile(context);
            file = new File(context.getDocBase() + "/" +welcomeFile);
        } else {
            //mimeMapping
            if(servletPath.contains(".")){
                String mimeType = WebXmlUtils.getMimeType(servletPath);
                response.setContentType(mimeType);
            }
            file = new File(context.getDocBase(), servletPath);
        }
        if(file.exists() && !file.isDirectory()){
            byte[] bytes = FileUtils.getBytes(file);
            response.setResponseBody(bytes);
            return;
        }
        response.setStatus(404);
        response.getWriter().println("404<br>");
        response.getWriter().println("File Not Found......");
    }
}
