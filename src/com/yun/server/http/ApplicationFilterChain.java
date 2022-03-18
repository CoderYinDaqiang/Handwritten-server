package com.yun.server.http;

import javax.servlet.*;
import java.io.IOException;
import java.util.List;

public class ApplicationFilterChain implements FilterChain {

    private List<Filter> filters;

    private Servlet servlet;

    private int flag = 0;

    public ApplicationFilterChain(List<Filter> filters, Servlet servlet) {
        this.filters = filters;
        this.servlet = servlet;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {
        if(flag < this.filters.size()){
            Filter filter = filters.get(flag++);
            filter.doFilter(servletRequest, servletResponse, this);
        } else {
            this.servlet.service(servletRequest, servletResponse);
        }
    }
}
