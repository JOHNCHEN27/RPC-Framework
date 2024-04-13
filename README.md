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

### Mock--模拟对象
mock是指模拟对象，用来模拟远程调用行为，因为实际开发中因为一系列缘故可能无法真实访问远程服务
在这种情况下，我们采用mock来测试业务，跑通业务流程。

我们需要在RpcConfig配置类中添加一个属性为 mock 用来判断是否开启mock。
新生成一个代理类，MockServiceProxy，利用反射的原理根据调用方法的返回值来进行生成指定类型的对象测试
利用设计模式中的工厂模式，来生成Mock的代理对象，在ServiceProxyFactory提供一个方法用来获取
MockServiceProxy代理对象，由于反射的机制，在调用工厂模式生成代理对象的时候，会返回一个代理对象
调用其方法，会进入到代理对象中的方法，进行业务逻辑判断，

### 序列化器与SPI机制
对于一个RPC框架，序列化器的选择应该要多样，可供挑选，所以不应该硬编码
除了Jdk原生的序列化器之外，我们还提供三种不同的序列化器可供选择：JSON、Hessian、Kryo
SPI（Service Provider Interface）服务接口提供 --是Java的机制，用于模块化开发和插件化开发
SPI机制允许服务提供者通过特定的配置文件将自己的实现注入到系统中，然后系统通过反射机制动态加载，不需要修改
框架原有的代码，提高扩展性

在resource目录下创建 META-INF目录，这个目录用来存放项目的配置文件、扩展程序等，相当于一个信息包
自定义SPI实现的话需要利用到 序列化器的名称 => 序列化器实现类对象的映射关系

以此创建JSON、Kryo、Hessian三种主流的序列化器类，参考代码即可
可以定义一个类为序列化器常量类，使用接口实现

利用工厂模式，创建一个序列化器工厂，每次执行序列化操作都不需要重新创建一个对象，避免资源的消耗
RpcConfig配置类中需要添加一个属性为默认的Serializer序列化方式

自定义序列化器SPI机制实现 参考Dubbo的实现进行设计，编写SpiLoader加载器类
关键点：
1、用map存储已加载的配置信息， 键名=>实现类
2、扫描指定路径，读取指定路径下的配置文件，获取键名=>实现类信息并存储到map中
3、定义获取序列化器类加载器的方法，根据用户传入的接口和键名，从map中找到对应的实现类，
通过反射机制获取到实现类对象，维护每一个对象实例缓存，创建过的对象直接从缓存中读取

最后在序列化器工厂中使用，SPI机制来指定序列化器对象，静态代码块调用load方法初始化Spi类加载器

### 注册中心基本实现
注册中心的作用是需要服务提供者将服务注册到注册中心，服务消费者从服务注册中心中读取信息
类似nacos一样，一个注册中心需要具备数据分布式存储，服务注册，服务发现、心跳检测、服务注销等功能
对此我们需要一个中间件来完成注册中心的这些核心功能，考虑到高可用、高性能、可靠、稳定，有三种技术选型
主流的Zookeeper、Redis，更适合存储元信息（注册信息）的元原生中间件Etcd

选取Etcd实现注册中心功能
Etcd --Go语言实现、开源、分布式的键值存储系统，主要用于分布式系统中服务发现，配置管理，分布式锁等场景
底层采用Raft一致性算法来保证数据的一致性、可靠性。具有高可用、强一致、分布式特性，简单易用
Etcd使用层次化的键值来存储数据，类似于文件系统路径的层次结构，能够灵活地单key查询，前缀查询、按范围查询
核心数据结构就是key-value，一般将序列化之后的值写入value
核心特性为Lease（租约） -- 对键值对进行TTL超市设置，设置过期时间，当租约到期，键值对被自动删除
Watch(监听) -- 监听特定的键变化，当发送改变时，会发送相应的通知

Etcd安装：官方下载页：https://github.com/etcd-io/etcd/releases
安装完成后会得到三个脚本，etcd -- etcd服务本身； etcdctl -- 客户端，用于操作etcd读写数据; etcd -- 备份恢复工具
执行etcd脚本之后，启动etcd服务，默认占用2379、2380端口
2379： 提供HttpAPI服务，和etcdctl交互
2380:  集群中节点间通讯

Etcd可视化工具： 像Redis的Redis Desktop Manager一样
etcdkeeper: https://github.com/evildecay/etcdkeeper/
kstone: https://github.com/kstone-io/kstone/tree/master/charts
推荐etcdkeeper，安装成本更低，学习使用更方便
默认占用8080端口，可使用-p参数启动时设置端口号, 访问地址:   http://localhost:8080/etcdkeeper

Etcd Java客户端 -- 操作Etcd工具 注意：Java版本必须大于11
首先在项目中导入jetcd依赖：
<dependency>
<groupId>io.etcd</groupId>
<artifactId>jetcd-core</artifactId>
<version>0.7.7</version>
</dependency>
即可开始使用

Etcd Java常用的客户端API为 kvClient、leaseClient、watchClient
kvClient: 用于对键值对的操作，通过kvclient获取对象，进行键值的设置、列出目录等
leaseClient：用于管理etcd的租约机制，理解为设置键值对的过期时间，
watchClient:用于监视etcd中键的变化，并在键的值发生变化时接受通知

etcd中的每个键都有一个与之对应的版本号，用于追踪键的修改历史，当键的值发生变化，其版本号也会增加，通过
watchAPI操作，可以监视键的变化，在发生变化时接受通知，使得etcd在分布式系统中能够实现乐观并发控制、一致性、可靠性的数据访问

注册信息定义： 新建一个ServiceMetainfo类，封装服务的注册信息，包括服务名称， 服务版本号、服务地址、服务分组等
给其添加几个方法，用来获取服务键名，服务注册节点键名等
当服务提供者注册信息时使用该类来封装注册信息

注册中心配置类：定义一个RegistryConfig类用来封装注册中心初始化的一些信息，包括注册中心类别，注册中心地址
用户名，密码，超时时间

注册中心接口：利用接口写注册中心功能，方便扩展其他的注册中心，和序列化器一样采用SPI机制动态加载
接口中包含注册中心初始化方法、注册服务、注销服务、服务发现、服务销毁等方法

创建EtcdRegistry类，用来实现Etcd注册中心的功能，实现注册中心接口方法，完善功能

定义一个注册中心常量类RegistryKeys 用来举例所有支持注册中心的键名
最重要的一点:使用工厂模式，利用SPI机制从资源文件中获取注册中心对象实例
在RpcApplication加载的时候初始化注册中心，在ServiceProxy代理对象的时候从注册中心获取服务提供者地址

### 注册中心优化
需求分析：
1、数据一致性： 服务提供者下线，注册中心需要及时更新，剔除下线节点，否证消费者可能会调用到已下线的节点
2、性能优化： 服务消费者每次需要从注册中心获取服务，可以使用缓存优化
3、高可用性：保证注册中心不会宕机
4、可扩展性：实现更多种类的注册中心
优化点：
1、心跳检测和续期机制
2、服务节点下线机制
3、消费端服务缓存
4、基于Zookeepker注册中心实现

#### 实现心跳检测和续期功能
心跳检测是指 注册中心定时向服务注册者检测其是否正常工作状态，如果没有接收到来自服务注册者的心跳信号，
就认为服务注册者宕机，或者发生故障不可用，从而触发警告进行处理
实现心跳检测关键就是：**定时和网络请求**
由于Etcd自带了key过期机制，我们可以通过这个机制，给key设置过期时间，然后定期给key续期，重置过期时间
如果服务注册者宕机，那么不会重置过期时间，将对key进行删除

**关键步骤**
1、服务提供者向Etcd注册自己的信息服务，并在注册的时候设置key的TTL（生存时间）
2、Etcd接受到服务提供者的注册信息之后，会自动维护服务信息的TTL 到期自动删除
3、服务提供者定期请求Etcd续签自己的注册信息，重写TTL

注册中心如何找到当前服务提供者注册的节点？利用本地特性，在服务提供者本地维护一个已注册节点集合，注册时
添加节点key到集合中，只需要续期集合内的key即可

给Registry接口提供heartBeat方法 用来实现心跳检测功能
定义一个本机注册节点的key集合用于维护续期
在服务注册时（register方法）将节点的key添加到key集合中
在服务注销时（unRegister方法）将节点的key从key集合中删除

在EtcdRegistry类中实现heartBeat方法：使用Hutool工具类的CronUtil定时任务，对所有集合中的节点进行定时
重写注册，续期时间要小于过期时间，设置为10秒，每10秒对集合中所有的key进行重新注册再次设置过期时间
从而实现续期功能

#### 服务节点下线机制
服务节点下线分为两种：
1、主动下线： 服务提供者正常退出，主动从注册中心移除注册信息
2、被动下线： 服务提供者项目移除退出时，利用Etcd的key过期机制自动移除
被动下线已经利用Etcd的key过期机制实现，只需实现主动退出
实现服务提供者主动下线时，注销注册信息：**利用JVM的ShutdownHook机制**

JVM的ShutdownHook是Java虚拟机提供的一种机制，允许开发者在JVM即将关闭之前执行一些清理工作或其他必要操作，
例如关闭数据库连接、释放资源、保存临时数据等
在destroy方法中取出本地key集合的所有key，对Etcd中key对应的内容全部删除
在RpcApplication的init中利用JVM的ShutdownHook机制，创建一个新的线程执行destroy方法

#### 消费端服务缓存
利用本地缓存来实现，只需要用一个列表存储服务信息即可，提供操作列表的方法 写缓存、读缓存、清空缓存
创建一个缓存类RegistryServiceCache

在具体实现类中创建缓存对象，在发现服务serviceDiscovery方法中，优先从缓存读取服务节点信息，
如果缓存没有 则去Etcd中根据key查询，查询完毕最后将查询的服务信息列表存储到本地缓存对象中，下次直接从缓存读取

**缓存更新**  --当服务注册信息发生变化时，需要即时更新缓存，使用Etcd的watch监听机制，当key发生改变，就会触发
事件来通知监听者
在消费者端去监听，为了防止重复监听同一个key可以使用一个hashSet集合来实现

在Registry中新增watch方法 在实现类中实现此方法，当key对应的value被删除，则清空缓存，需要重新查找服务来更新缓存
 
#### Zookeeper注册中心实现
1、安装Zookeeper  -- https://dlcdn.apache.org/zookeeper (3.8.4)
2、引入客户端依赖
3、实现接口
4、SPI补充Zookeeper注册中心

创建ZooKeeperRegistry类 实现Registry接口，参考官方文档实现功能 

### 自定义协议
核心部分：**自定义网络传输、自定义消息结构**
由于RPC框架注重性能，所以我们要设计一个网络传输，能够高性能通信的网络协议和传输方式

http协议的头信息比较大，会影响传输性能，http本身属于无状态协议，意味着每个http请求都是独立的，每次请求和响应
都要重新建立和关闭连接，也会影响性能

自定义消息结构的设计：用最少的空间传递需要的信息
在定义结构类型时尽量使用轻量的类型 比如byte占一个字节
TCP协议栈由请求头和请求体组成，需要根据TCP协议的结构来设计自定义消息体

请求消息结构设计可以分为：魔数、版本、序列化方式、类型、状态、请求id、请求体数据长度、请求体内容
这个消息结构是拼接在一起的字节数组，由于TCP编码方式不一样，我们还需要设计消息编码器和消息解码器
按照顺序向缓冲区写入数据，解码的时候按照写入的顺序读取数据

新建ProtocolMessage类 将消息头单独封装为一个内部类，消息体使用泛型
新建ProtocolConstant类 记录自定义协议有关的常量，如：魔数、版本号、消息头长度
新建消息字段状态枚举类：定义成功、请求失败、响应失败三种枚举值
定义协议消息类型枚举类，包括请求、响应、心跳等
定义协议消息序列化器枚举类，跟RPC框架支持的序列化器对应

之前Vert.x使用的HTTPServer服务，现在改为TCPServer
创建Vert.xTCP服务类 实现Http服务接口

由于Vert.x的TCP服务器接受消息的类型是Buffer 所有不能直接写入一个对象，需要用到解码器和编码器
将Java消息对象和Buffer进行相互转换
分别设计编码器和解码器类

定义一个TCP的请求处理器 用于处理TCP请求 与Http请求处理器不同的是，Tcp请求处理器需要使用到编码器和解码器
在代理类ServiceProxy中把发送http请求改为发送TCP请求

注意：使用TCP协议网络通信时可能会出现半包和粘包的问题
半包:读取的数据不完整 一段一段的
粘包：读取的数据重复 读出来更多的数据

如何解决半包问题：在消息头中设置请求体的长度，服务端接受时，判断每次消息的长度是否符合预期，不完整就不读
留到下一次读取
如何解决粘包问题：每次只读取指定长度数据，超过长度的留着下一次接受信息时读取

使用Vert.x的RecordParser来解决，调整它的长度来读取数据
思路：1、先完整读取请求头信息，请求头信息是固定的，用RecordParser保证每次完整读取
2、再根据请求头长度信息更改RecordParser固定长度 保证完整获取到请求体

由于ServiceProxy消费者和请求Handler都需要接受Buffer 都需要处理半包和粘包问题，所以对代码进行复用
采用装饰器模式，使用RecordParser对原有的Buffer处理器的能力进行增强
定义TcpBufferHandlerWrapper类 实现并增强Handler<Buffer>接口

### 负载均衡
一个服务可能有多个提供者，在这些提供者中选择一个进行请求，而不是每次请求同一个服务提供者
在我们的RPC框架中 负载均衡的作用就是从y一组可用的服务提供者中选择一个进行调用
常用的负载均衡实现技术有Nginx（七层负载均衡）、LVS（四层负载均衡）等

**常见的负载均衡算法**
轮询（Round Robin）：按照循环的顺序将请求分配给每个服务器，适用于各服务器性能相近情况，依次循环调用
随机（Random）：随机选择一个服务器来处理请求，适用于服务器性能相近且负载均衡的情况
加权轮询（Weighted Round、Robin）：根据服务器的性能或权重分配请求，性能更好的服务器会获得更多请求，适用于服务器性能不均的情况

我们采用一致性Hash算法： 将请求分配到多个节点或服务器上，非常适用于负载均衡
核心思想：将整个哈希值空间划分为一个环状结构，每个节点或服务器在环上占据一个位置，每个请求根据其哈希值映射到
环上的一个点，然后顺时针寻找第一个大于或等于该哈希值的节点，将请求由到该节点上。

一致性哈希还解决了节点下线和倾斜问题，需要引入虚拟节点，使每个服务器接受到的请求会更容易平均

依次实现轮询、随机、一致性hash三种负载均衡算法
新建一个负载均衡器接口，提供一个选择服务的方法
轮询负载均衡器类适用JUC包的AtomicInteger实现原子计数器，防止并发问题。
随机负载均衡器类适用Java自带的Random类实现随机选取
一致性Hash负载均衡器适用TreeMap实现一致性Hash环，该数据结构提供了ceiling和firstEntry两个方法，便于获取符合算法要求的节点

**支持配置和扩展负载均衡器**
利用工厂模式来读取相应配置，根据配置选择负载均衡器
实现一个负载均衡器常量类，定义负载均衡器键名
适用工厂模式，支持key从SPI获取负载均衡对象实例
为RpcConfig配置类添加一个负载均衡器配置属性默认为轮询


### 重试机制
重试策略：
1、什么时候、什么条件下重试？
2、重试时间（确定下一次的重试时间）
3、什么时候、什么条件下停止重试
4、重试后要做什么？

**重试时间**
固定重试间隔：在每次重试之间使用固定的时间间隔 1s、2s、3s、4s等
指数退避重试：每次失败后，重试的时间间隔会以指数级增加，以避免请求过于密集
随机延迟重试：在每次重试之间使用随机时间间隔，以避免请求的同时发生
可变延迟重试：根据先前重试的成功或失败情况，动态调整下一次重试的延迟时间
这几种策略可以组合使用，根据具体情况和需求灵活调整

**停止重试**
一般来说，重试次数是有上限的，否证随着报错的增多，系统同时发生的重试也会越来越多，造成雪崩
主流的停止重试策略：
最大尝试次数：一般重试当达到最大次数时不再重试
超时停止：重试达到最大时间的时候，停止重试

需要注意的时，当重试上限之后，一般还要进行其他操作，如：通知告警、降级容错

我们采用不重试和固定时间间隔重试
需要引入Guava-Retry库来完成固定时间间隔重试

创建重试接口类，创建不重试实现类，来实现接口中的方法，不重试就是执行一次任务就返回
创建固定时间重试类，实现重试接口，需要使用Guava-Retry库提供的RetryBuilder指定重试条件、重写等待策略等

对于RPC框架来说，要支持多种可重试策略，所以需要像序列化器、注册中心、负载均衡器一样，
可以通过配置来指定使用的重写策略，支持自定义重试策略，让框架更医用、可扩展
定义重试常量类，定义重试策略键名值
创建重试策略工厂，利用工厂模式来创建实例对象，通过SPI机制读取资源文件来返回相应的实例对象

