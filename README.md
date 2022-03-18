Basic knowledge:JAVASE,JAVAEE,COLLECTION,HTTP传输协议(报文格式，协议)
类Tomcat服务器
1.实现一个简易版的服务器，随后将请求报文封装到request对象中（通过inputStream），其次生成一个response对象，
  封装一些响应信息，如响应体，响应头。最后通过response.responde()（通过outputStream）生成响应报文发送给
  游览器。
  
2.首先实现单应用，随后要实现多应用问题（webapps目录下的应用还有conf下server.xml文件中的应用），通过
  contextMap将所有context应用进行封装。注：获取当前应用和请求资源为重点.....关键思想需要也contextMap进
  行效验。

3.欢迎文件, 欢迎界面只能是当servletPath，即请求资源为"/"时，需要加载欢迎界面。具体做法是需要读取web.xml
  文件中的welcome-file，然后与自己的应用目录下的所有文件进行比对，有的话就返回当前文件路径，没有的话默认
  "index.html"并通过stream写入response。

4.响应二进制文件，解析请求资料，当请求资源为1.jpg或者为1.mp4时，就要当作二进制文件进行处理。读取web.xml
  文件中的mime-mapping，返回与自己请求资源匹配的content-type，最后通过response.setContentType响应给
  游览器

5.目前所有的逻辑都堆积在BootStrap中，随着后续功能的展开，额外功能的扩充，代码会变得越来越庞杂，越来越难以
  维护。所以可以设置多个组件对象，可以降低代码之间的耦合，同时后续功能扩展时，也更加方便。
  Server代表了当前服务器，有且只有一个
  Service代表了一组提供服务、处理请求、做出响应的组件
  Connector代表了客户端和服务器进行交互的组件，给Engine提供统一的服务，将engine的功能与各种协议分隔开，
  比如HTTP协议、HTTPS协议、AJP协议等。
  Engine代表了运行Servlet的环境，主要职责是将Connector发送过来的请求进一步交给虚拟主机Host来
  处理
  Host代表了虚拟主机，负责管理应用Context
  Context代表了一个应用，提供Servlet运行的具体环境    

6.配置Servlet新建一个EE项目，编写一个servlet，编译，利用我们编写的服务器虚拟映射部署该应用。首先需要读取
  web.xml配置文件（不同servlet的url-pattern不能冲突），实例化一个servlet对象，根据url-pattern，调用
  对应的servlet.service(request,response)方法，与此同时，将请求报文的封装对象request、以及response
  对象作为参数传递进去，执行完之后，返回结果，读取response，做出响应.
   
   
7.ClassNotFoundException原因：在运行时内存里面是没有对应的class的
  在本地硬盘上面是有这个类的，但是在运行时，内存里面是没有的
  类加载器将本地硬盘上面的文件加载到内存中
  
  用户自己定义了一个Hashmap的实现，那么会不会对整体的java程序运行产生于影响
  
  处于应用目录下WEB-INF/lib还是classes下面的class以及jar没有类加载器来加载，同时呢
  不同的应用还可能需要不同的类的版本，需要去设计一个类加载器，但是这个类加载器不能使用一个类加载器
  去加载所有应用下面的类，因为一个累加载器只可以加载一个对应的类对象，如果多个应用同时使用fileupload，
  并且版本不同，那么这个是不可能实现的
  需要给每个应用单独配置一个类加载器
  此外还有一个地方也需要类加载器。tomcat的lib目录依赖的这些jar包也需要设计一个类加载器来加载。
  不是可以正常运行吗？
  IDEA ----IDE 集成开发环境，我们这边可以正常运行的原因在于我们使用的是集成开发环境
  IDEA会帮助我们将lib目录下面的jar包加载到内存中
  综上所述：
  1.tomcat的类加载器去加载lib目录下面的jar包
  2.给每个应用分别设置一个应用类加载器，分别取加载每个应用下面的WEB-INF目录下的classes、lib等clas以及jar
  
  如何去定义一个类加载器呢？
  1.根据类加载器的定义说明，一个是重写findClass方法，需要文件的数组，利用文件的数组然后定义一个class
  
  我们要去实现的类加载器如果采用上述方式，其实是非常繁琐的，主要在于需要自己去解析jar包里面的所有的class文件
  可以去继承URLClassloader
  
  最终实现的效果就是main方法启动的时候，实例化一个类加载器，类加载器会去加载lib目录下面的jar包
  同时该类加载器还设置成为了一个context classloader, 每个应用单独的类加载器它的父类加载器全部都是CommonClassloader
  
  
8.实现ServletContext,在此之前，要首先实现一个BaseServeltContext类（基类继承了ServletContext）, 然后
  再继承这个基类，避免了ApplicationServlet中有太多我们不使用的代码，减少冗余。这个ServletContext要每一
  个应用都要有，所以要在每一个Context中可以取出。

  
9.servlet单例：一个servlet在JVM内存里面只有一个对应的对象FirstServlet,并且是在Context对象中存在
  思路：从池子里面去取，如果能够取出来，就用；如果取不出来，那么就实例化一个，同时放回池子中. 似Session中
  购物车案例:session.getAttribute()如何没有，就需要为这个用户创建一个新的，并存入session中，如果有则
  直接取出，类似单例设计模式。
  
  生命周期、自启动：
  servlet有三个生命周期函数init（实例化servlet的时候）、service、destroy
  
  自启动：
  servlet的随着应用的加载而启动、开机自启动
  load-on-startup参数：设置一个非负数，可以让servlet随着应用的加载而执行servlet的init方法
  而不是在第一次访问servlet之前才执行,让init的时机提前了一些
  
  1.需要去扫描load-on-startup节点，将这些servlet-class加入到一个集合中
  2.直接去实例化该servlet，调用init
  
  
10.会话技术cookie、session 
   cookie是客户端技术，cookie生成以后，会通过set-Cookie：key=value响应头发送给客户端
   客户端下次访问服务器Cookie：key=value请求头发送给服务器
   
   实现Session先画流程图
   session依赖于cookie   request.getSession();
   
   缺省servlet：
   找不到任何servlet可以处理该请求的时候，会交给缺省servlet来处理
   我们将servlet的处理逻辑和静态资源的处理逻辑做了一层封装
   servlet的逻辑封装到DynamicServlet中
   静态资源的逻辑封装到DefaultServlet中
   同时，还是为了后面的filter引入做一个铺垫
   
11.关于filter的特性：
   url-pattern没有特殊的要求，多个filter可以设置相同的url-pattern，通过url-pattern去获取
   filter的话，那么有可能拿到的是一个list
   
   filter  设置web.xml节点，也需要去写逻辑去解析节点
   
   filter不像servlet一样，在第一个访问之前实例化
   直接实例化
   需要将filter-class集合全部实例化放入到一个pool里面（map）
   
   当一个请求到来时，首先判断当前请求对应的filter有哪些 list
   拿到filter、servlet----形成一个责任链
   
   
12.listener:比较常用到的就是ServletContextListener
   Context里面需要存List<ServletContextListener>引用
   解析web.xml里面的节点，然后解析成list形式，注入到list引用中
   然后在特定的时间点执行listenere.contextInitialized(event);
   
13.线程池：注意到客户端的连接到来之后，会每次都创建一个新的线程来处理后续的请求但是当请求处理完毕之后，
   这个线程就没有后续的使命了，也就是相当于一次性使用线程的创建和销毁其实是比较消耗性能的，并且线程数过
   多了之后，操作系统进行线程调度开销也比较大，所以能不能不这么做呢？线程池