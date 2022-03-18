package com.yun.server;

import com.yun.server.classLoader.CommonClassLoader;
import com.yun.server.utils.ThreadUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class BootStrap {

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        CommonClassLoader commonClassLoader = new CommonClassLoader();
        Class<?> aClass = commonClassLoader.loadClass("com.yun.server.catalina.Server");
        //需要将CommonCLassloader设置为当前整个服务器的父加载器，每个应用的webapp classloader子类加载器
        //如果tomcat的commonsClassloader加载了servlet的jar包，那么每个应用的webapp 类加载器就不会再去加载servlet的jar包
        Thread.currentThread().setContextClassLoader(commonClassLoader);
        Object o = aClass.newInstance();
        Method serverStart = aClass.getDeclaredMethod("start");
        serverStart.invoke(o);

    }
}
