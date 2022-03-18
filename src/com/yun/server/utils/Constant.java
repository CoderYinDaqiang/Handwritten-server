package com.yun.server.utils;

import java.io.File;

public class Constant {

    public static final File webapps = new File(System.getProperty("user.dir"),"webapps");

    public static final File ROOT = new File(webapps, "ROOT");

    public static final File conf = new File(System.getProperty("user.dir"), "conf");

    public static final File webXml = new File(conf, "web.xml");

    public static final File serverXml = new File(conf, "server.xml");

    public static final File contextXml = new File(conf, "context.xml");

}
