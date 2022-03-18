package com.yun.server.catalina;

import com.yun.server.http.ApplicationFilterChain;
import com.yun.server.http.Request;
import com.yun.server.http.Response;
import com.yun.server.servlet.DefaultServlet;
import com.yun.server.servlet.DynamicServlet;
import com.yun.server.utils.SessionUtils;
import com.yun.server.utils.ThreadUtils;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Connector implements Runnable{

    private int port;

    private Service service;

    public Connector(int port, Service service) {
        this.port = port;
        this.service = service;
    }

    @Override
    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(this.port);
            System.out.println("Starting ProtocolHandler [HTTP/1.1-bio-" + port + "]");
            //read all context
            //scanContext();
            while (true){
                // TCP Connection ,and accept() is a blocking method.
                Socket socket = serverSocket.accept();
                Runnable runnable = new Runnable(){
                    @Override
                    public void run() {
                        Request request = null;
                        Response response = null;
                        InputStream inputStream = null;
                        OutputStream outputStream = null;
                        try {
                            inputStream = socket.getInputStream();
                            outputStream = socket.getOutputStream();
                            //there is a blocking method,which is inputStream.read().
                            Engine engine = service.getEngine();
                            request = new Request(inputStream, engine);
                            response = new Response(outputStream);
//--------------------------------------------------------------------------------------------------------------
                            Servlet servlet = null;
                            List<Filter> filters = request.getContext().getFilters(request.getServletPath());
                            if ("/".equals(request.getServletPath())) {//no request source :use welcome file
                                servlet = DefaultServlet.INSTANCE;
                            } else {// process existing request source
                                // when it is servlet, use servlet by reflection  ....
                                String servletClass = request.getContext().getServletClass(request.getServletPath());
                                if (servletClass != null) {
                                    //当请求到来时，为每一个游览器创建一个session，并设置生命周期。
                                    SessionUtils.getSession(request, response);
                                    servlet = DynamicServlet.INSTANCE;
                                } else {
                                    servlet = DefaultServlet.INSTANCE;
                                }
                            }
                            ApplicationFilterChain applicationFilterChain = new ApplicationFilterChain(filters, servlet);
                            applicationFilterChain.doFilter(request, response);
//--------------------------------------------------------------------------------------------------------------
                        } catch (Exception e) {
                            e.printStackTrace();
                            response.setStatus(500);
                            response.getWriter().println("500");
                            response.getWriter().println(e.toString());
                        } finally {
                            response.responde();
                        }

                    }
                };
                ThreadUtils.add(runnable);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
