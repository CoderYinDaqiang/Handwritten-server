package com.yun.server.classLoader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class WebappClassLoader extends URLClassLoader {

    public WebappClassLoader(String docBase, ClassLoader parent) {
        super(new URL[]{}, parent);
        File webinfDirectory = new File(docBase, "WEB-INF");
        if(!webinfDirectory.exists()){
            return;
        }
        File libDirectory = new File(webinfDirectory, "lib");
        File classesDirectory = new File(webinfDirectory, "classes");
        if(libDirectory.exists()){
            File[] jars = libDirectory.listFiles();
            for (File jar : jars) {
                if(jar.getName().endsWith(".jar")){
                    try {
                        addURL(new URL("file:" + jar.getAbsolutePath()));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if(classesDirectory.exists()){
                try {
                    addURL(new URL("file:" + classesDirectory.getAbsolutePath() + "/"));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

        }

    }
}
