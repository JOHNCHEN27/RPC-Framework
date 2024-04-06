# 项目笔记 -- 

### 简易版RPC框架初识： 

RPC（Remote Procedure Call）远程过程调用，允许计算机在不同的计算机之间进行通信交互，像本地调用一样
RPC的利用可以让我们使用已经开发过的功能模块，提高开发效率

一个最简单的RPC框架包含服务提供者、服务消费者；消费者通过代理对象来发送请求，在网络传输过程中
Java对象的传输需要进行序列化 通过将对象转化为字节流的方式传输，去请求RPC服务器，
服务提供者需要把提供的服务注册到服务注册器中，提供相应的服务方法


### 全局配置加载：

由于RPC框架是需要被其他项目作为服务提供者或者服务消费者引入的，RPC框架中设计到很多配置
不应该是硬编码而是可以自定义，允许框架通过编写配置文件来自定义配置，一般情况下服务提供者和消费者
需要编写相同的配置

配置一套全局配置加载功能，能够使RPC框架轻松从配置文件中读取配置，并且维护一个全局配置对象，便于
框架快速获取到一致的配置。

首先需要创建一个配置对象类，里面包含RPC框架所对应的属性，比如：名称、版本号、服务器主机、端口等等
创建一个工具类ConfigUtils用来读取对应的配置文件并且返回配置文件对应的java对象（返回一个配置对象）
此工具类应该设计为容易扩展，其方法的返回值应当为一个泛型，根据传进来的类型返回。

在ConfigUtils工具类中设计了一个方法，接受的参数为Class<T>泛型字节码文件，还有一个配置文件属性前缀
参数1泛型字节码文件用来指定根据配置文件转化为相应类的java配置对象
参数2是用来读取配置文件中前缀对应的属性
利用hutool工具包的Props类型，根据配置文件名生成props对象，利用toBean方法指定转化类型以及配置前缀来
返回一个配置对象

#### RPC框架应用初始化
提供一个方法init用户初始化RPC框架应用，需要维护一个volatile修饰的RpcConfig类型的变量
作用是初始化来返回一个配置对象（所以它使用的类型是配置类）

volatile关键字表示当前变量是一个可以共享的变量，在多线程的环境下可以让其他线程访问到，它的原理是
每次从主存中读取该变量的值并拷贝一份给每个线程。
volatile有一个缺点就是不具备原子性 所以我们需要借助双重检验锁来实现这个RPC框架的单例模式

提供一个getRpcConfig方法，首先进行判断是否为空，判断完毕之后对其进行加锁，加锁对象为当前类字节码，
加锁完毕再给对象赋值初始化
为什么要在加锁之后再进行是否为空判断？ -- 由于JVM重排序特点，对一个变量进行赋值时，大概可以分为三步
1、给这个变量分配内存空间   2、变量初始化     3、将这个变量指向分配的内存空间
这三步在单线程（单环境）下没问题，但是在多线程下，有可能顺序为132,此时如果有一个线程过来执行此方法
会导致返回一个未初始化的变量，从而导致线程安全问题。