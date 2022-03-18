package com.yun.server.JunitTest;

import com.yun.server.utils.FileUtils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ClassLoaderTest extends ClassLoader {


    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] b = loadClassData(name);
        return defineClass(name, b, 0, b.length);
    }

    private byte[] loadClassData(String name) {
        // name is 全限定类名
        String fileName = name.replaceAll("\\.", "/") + ".class";
        File file = new File("F:\\JAVA\\idea_project\\DemoTest\\EE\\out\\production\\SE", fileName);
        return FileUtils.getBytes(file);
    }

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        ClassLoaderTest classLoaderTest = new ClassLoaderTest();
        Class<?> aClass = classLoaderTest.loadClass("com.yun.demo2.Test");
        Object o = aClass.newInstance();
        Method main = aClass.getDeclaredMethod("service");
        main.invoke(o);
    }
}
