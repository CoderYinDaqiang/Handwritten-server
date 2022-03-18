package com.yun.server.classLoader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

public class CommonClassLoader extends URLClassLoader {


    public CommonClassLoader() {
        super(new URL[]{});
        File file = new File(System.getProperty("user.dir"), "lib");
        File[] files = file.listFiles();
        for (File jar : files) {
            if(jar.getName().endsWith(".jar")){
                try {
                    addURL(new URL("file:" + jar.getAbsolutePath()));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
