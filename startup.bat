del /q bootstrap.jar
jar cvf bootstrap.jar -C out\production\Tomcat com\yun\server/BootStrap.class -C out\production\Tomcat com\yun\server/classLoader/CommonClassLoader.class
del /q tomcat.jar
cd out
cd production
cd Tomcat
jar cvf ../../../lib/tomcat.jar *
cd ..
cd ..
cd ..
java -cp bootstrap.jar com.yun.server.BootStrap
pause