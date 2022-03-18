package com.yun.server.catalina;

import com.yun.server.classLoader.WebappClassLoader;
import com.yun.server.http.ApplicationContext;
import com.yun.server.utils.ContextUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.servlet.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Context {

    private String path;

    private String docBase;

    private Map<String, String> urlPatternServletClass;

    //设计这个map的目的是为了能够进行校验url-pattern是否合法
    //多个servlet同时映射到一个相同的url-pattern，the servlet named xxx and xxx are both mapped to the url pattern which is not permitted
    private Map<String, String> servletClassUrlPattern;

    private Map<String, String> servletNameServletClass;

    private Map<String, String> servletNameUrlPattern;

    private Map<String, List<String>> urlPatternFilterClasses;

    private Map<String, List<String>> urlPatternFilterNames;

    private Map<String, String> filterNameFilterClass;

    private Map<String, String> filterClassFilterName;

    private Map<String, Filter> filterPool;

    private File webXml;

    private WebappClassLoader webappClassLoader;

    private ApplicationContext servletContext;

    private Map<Class, Servlet> servletPool;

    private List<ServletContextListener> listeners;

    public Context(String path, String docBase) {
        this.path = path;
        this.docBase = docBase;
        //servlet
        this.urlPatternServletClass = new HashMap<>();
        this.servletClassUrlPattern = new HashMap<>();
        this.servletNameServletClass = new HashMap<>();
        this.servletNameUrlPattern = new HashMap<>();
        //filer
        this.urlPatternFilterClasses = new HashMap<>();
        this.urlPatternFilterNames = new HashMap<>();
        this.filterNameFilterClass = new HashMap<>();
        this.filterClassFilterName = new HashMap<>();
        this.filterPool = new HashMap<>();
        //listener
        this.listeners = new ArrayList<>();
        // Process servlet and classLoader
        ClassLoader commonClassLoader = Thread.currentThread().getContextClassLoader();
        this.webappClassLoader = new WebappClassLoader(this.docBase, commonClassLoader);
        this.webXml = new File(this.docBase, ContextUtils.getWatchedResource());
        //  obtain servletContext
        this.servletContext = new ApplicationContext(this);
        this.servletPool = new HashMap<>();
        deploy();
    }

    //部署的方法：servlet、filter、listener
    private void deploy() {
        if(this.webXml.exists() && !this.webXml.isDirectory()){
            deployServlet();
            deployFilter();
            deployListener();
            initListener();
        }
    }

    private void initListener() {
        ServletContextEvent servletContextEvent = new ServletContextEvent(this.servletContext);
        for (ServletContextListener listener : this.listeners) {
            listener.contextInitialized(servletContextEvent);
        }
    }

    private void deployListener() {
        try {
            Document document = Jsoup.parse(this.webXml, "utf-8");
            Elements elements = document.select("listener listener-class");
            for (Element element : elements) {
                String listenerClass = element.text();
                Class<?> aClass = this.webappClassLoader.loadClass(listenerClass);
                ServletContextListener o = (ServletContextListener) aClass.newInstance();
                this.listeners.add(o);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    private void deployFilter() {
        try {
            Document document = Jsoup.parse(this.webXml, "utf-8");
            parseFilterMapping(document);
            Set<String> filterClassNames = filterClassFilterName.keySet();
            for (String filterClassName : filterClassNames) {
                Class<?> aClass = null;
                aClass = webappClassLoader.loadClass(filterClassName);
                //全部实例化，需要把filter加入到pool中
                getFilter(aClass);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void getFilter(Class<?> aClass) {
        Filter filter = filterPool.get(aClass.getName());
        if(filter == null){
            String filterClassName = aClass.getName();
            try {
                filter = (Filter) aClass.newInstance();
                //没有实现FilterConfig
                filter.init(null);
                filterPool.put(filterClassName, filter);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ServletException e) {
                e.printStackTrace();
            }
        }
    }

    //去解析filter的映射关系，形成一个map 键值对
    //有效的部分：url-pattern----filter-class集合 映射关系
    private void parseFilterMapping(Document document) {
        Elements elements = document.select("filter-mapping url-pattern");
        // url-pattern-----filter-names
        for (Element element : elements) {
            String urlPattern = element.text();
            String filterName = element.parent().select("filter-name").first().text();
            List<String> filterNames = urlPatternFilterNames.get(urlPattern);
            if(filterNames == null){
                filterNames = new ArrayList<>();
                urlPatternFilterNames.put(urlPattern, filterNames);
            }
            filterNames.add(filterName);
        }
        //filter-class-name-----filter-name
        Elements elements1 = document.select("filter filter-name");
        for (Element element : elements1) {
            String filterName = element.text();
            String filterClassName = element.parent().select("filter-class").first().text();
            filterNameFilterClass.put(filterName, filterClassName);
            filterClassFilterName.put(filterClassName, filterName);
        }
        //url-pattern------filter-class-name
        Set<String> urls = urlPatternFilterNames.keySet();
        for (String url : urls) {
            List<String> filterNames = urlPatternFilterNames.get(url);
            for (String filterName : filterNames) {
                String filterClassName = filterNameFilterClass.get(filterName);
                List<String> filterClassNames = urlPatternFilterClasses.get(url);
                if(filterClassNames == null){
                    filterClassNames = new ArrayList<>();
                    urlPatternFilterClasses.put(url, filterClassNames);
                }
                filterClassNames.add(filterClassName);
            }
        }
    }



    private void deployServlet() {
        parseServletMapping1();
        checkUrlPatternValid();
        parseServletMapping2();
        parseLoadOnStartup();
    }

    private void parseLoadOnStartup() {
        try {
            Document document = Jsoup.parse(this.webXml, "utf-8");
            Elements element = document.select("servlet load-on-startup");
            if(element.size() > 0){
                String text = element.text();
                if(Integer.parseInt(text) > 0){
                    String servletClass = element.parents().select("servlet-class").text();
                    Class<?> aClass = this.webappClassLoader.loadClass(servletClass);
                    getServlet(aClass);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    //形成url-pattern------servlet-class的映射
    private void parseServletMapping2() {
        Set<String> servletNameSet = this.servletNameServletClass.keySet();
        Set<String> servletMappingNameSet = this.servletNameUrlPattern.keySet();
        for (String name1 : servletNameSet) {
            for (String name2 : servletMappingNameSet) {
                if(name1.equals(name2)){
                    this.urlPatternServletClass.put(this.servletNameUrlPattern.get(name2), this.servletNameServletClass.get(name1));
                }
            }
        }
    }

    //不同的servlet-class是否映射到同一个url-pattern，如果映射到同一个，那么报错
    private void checkUrlPatternValid() {
        Set<String> keyset1 = this.servletClassUrlPattern.keySet();
        Set<String> keySet2 = this.servletClassUrlPattern.keySet();
        for (String key1 : keyset1) {
            //只有servlet不相同的时候才进行比较
            for (String key2 : keySet2) {
                if(!key1.equals(key2)){
                    String value1 = this.servletClassUrlPattern.get(key1);
                    String value2 = this.servletClassUrlPattern.get(key2);
                    if(value1.equals(value2)){
                        throw new IllegalArgumentException("The servlets named [" + key1 + "] and [" + key2 + "] " +
                                "are both mapped to the url-pattern [" + value1 + "] which is not permitted" );
                    }
                }
            }
        }
    }

    //servlet-name---servlet-class   servlet-name----url-pattern
    // servlet-class-----url-pattern
    private void parseServletMapping1() {
        try {
            Document document = Jsoup.parse(this.webXml, "utf-8");
            Elements servletElments = document.select("servlet");
            for (Element servletElment : servletElments) {
                Elements servletNameElement = servletElment.select("servlet-name");
                String servletName = servletNameElement.text();
                Elements servletClassElment = servletElment.select("servlet-class");
                String serveletClass = servletClassElment.text();
                this.servletNameServletClass.put(servletName, serveletClass);
            }
            Elements servletMappingElements = document.select("servlet-mapping");
            for (Element servletMappingElement : servletMappingElements) {
                Elements servletNameElement = servletMappingElement.select("servlet-name");
                String servletName = servletNameElement.text();
                Elements urlpatternElement = servletMappingElement.select("url-pattern");
                String urlpattern = urlpatternElement.text();
                this.servletNameUrlPattern.put(servletName, urlpattern);
            }
            Set<String> servletNameSet = this.servletNameServletClass.keySet();
            Set<String> servletMappingNameSet = this.servletNameUrlPattern.keySet();
            for (String name1 : servletNameSet) {
                for (String name2 : servletMappingNameSet) {
                    if(name1.equals(name2)){
                        this.servletClassUrlPattern.put(this.servletNameServletClass.get(name1), this.servletNameUrlPattern.get(name2));
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getServletClass(String servletPath){
        return urlPatternServletClass.get(servletPath);
    }

    public String getPath() {
        return path;
    }

    public String getDocBase() {
        return docBase;
    }

    public ClassLoader getClassLoader(){
        return this.webappClassLoader;
    }

    public ApplicationContext getServletContext() {
        return servletContext;
    }

    public Servlet getServlet(Class<?> aClass) {
        Servlet servlet = this.servletPool.get(aClass);
        if(servlet == null){
            try {
                servlet = (Servlet) aClass.newInstance();
                servlet.init(null);
                this.servletPool.put(aClass, servlet);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ServletException e) {
                e.printStackTrace();
            }

        }
        return servlet;
    }

    public List<Filter> getFilters(String servletPath){
        List<Filter> filterList = new ArrayList<>();
        List<String> filterClassList = this.urlPatternFilterClasses.get(servletPath);
        if(filterClassList != null){
            for (String filterClass : filterClassList) {
                Filter filter = this.filterPool.get(filterClass);
                filterList.add(filter);
            }
            return filterList;
        }
        return filterList;
    }
}
