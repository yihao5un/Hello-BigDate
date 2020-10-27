### Kafka

- 消息队列(用于解耦)内部实现原理

  ![image-20200918171044168](kafka.assets/image-20200918171044168.png)

  - 点对点模式(一对一)

    基于***拉取***或者***轮询*** 。特点是发送到队列的消息被***一个且只有一个***接收者接收处理，即使有多个消息监听者也是如此。

  - 发布/订阅模式(一对多)

    基于***推送***的消息传输模型。分为临时订阅者(只在主动监听主题时才接收消息)和持久订阅者(监听主题的所有消息, 即使当前不可用, 处于离线状态)。

- Kafka定义: 

  Kafka 主要是用于缓存数据 (Scala编写) 是一个分布式的消息队列(端口: 2181)

  - 根据**Topic** 进行分类, 发送方称为 **Producer** 接收方称为**Consumer** 。

    > **Kafka 集群**有多个Kafka**实例**组成, 每个实例(Server)称为**broker(注意broker.id 不可以重复)**。无论是集群还是consumer都依赖于zookeeper中的meta信息

  - kafka 架构

    ![image-20201026111721089](kafka.assets/image-20201026111721089.png)

    ![Selection_036](kafka.assets/Selection_036.png)

    > Partition：为了实现扩展性，一个非常大的topic可以分布到多个broker（即服务器）上。
    >
    > 一个**topic**可以分为多个**partition**，每个**partition**是一个有序的**队列**。
    >
    > partition中的每条消息都会被分配一个**有序的id**（offset）。
    >
    > kafka只保证按一个partition中的**顺序**将消息发给**consumer**，**不保证**一个topic的**整体**（多个partition间）的顺序

  - Kafka 命令行操作:

    1. 查看所有topic

       ```shell
       bin/kafka-topics.sh --zookeeper hadoop102:2181 --list
       ```

    2. 创建topic

       ```shell
       bin/kafka-topics.sh --zookeeper hadoop102:2181 --create --topic first --partitions 1 --replication-factor 3 
       ```

    3. 删除topic

       ```shell
       bin/kafka-topics.sh --zookeeper hadoop102:2181 --delete --topic first
       ```

       > 需要在server.properties中设置delete.topic.enable = true 否则只是标记删除

    4. 发送消息

       ```shell
       bin/kafka-console-producer.sh --broker-list hadoop102:9092 --topic first
       >hello world
       >fxxku  fxxku
       ```

    5. 消费消息

       ```shell
       bin/kafka-console-consumer.sh --zookeeper hadoop102:2181 --from-beginning --topic first
       ```

       > --from-begining 会把first主题中之前所有的数据都读取出来(根据业务选择是否开启)

    6. 查看某个topic详情

       ```shell
       bin/kafka-topics.sh --zookeeper hadoop102:2181 --describe --topic first
       ```

- Kafka工作流程分析:

  ![Selection_036](kafka.assets/Selection_036.png)

  - 生产过程分析:

    1. 写入方式: 

       push(producer) -> broker -> append -> partition (顺序写磁盘)

       > 顺序写磁盘比随机写内存的效率更高 保证Kafka吞吐率

    2. 分区(partition):

       消息发送的时候都被发送到一个topic (本质是一个目录)中, 而topic是由一些partition log组成。

       ![image-20201026164751013](kafka.assets/image-20201026164751013.png)

       ![image-20201026165004408](kafka.assets/image-20201026165004408.png)

       > partition消息是**有序的**， 生产的消息都有一个唯一的**offset值** ，分区是为了方便在集群中扩展
       >
       > 分区原则: 
       >
       > 1) 指定了patition，则直接使用 
       >
       > 2) 未指定patition但指定key，通过对key的value进行hash出一个patition 
       >
       > 3) patition和key都未指定，使用轮询选出一个patition。

       

    3. 副本(replication)

       为了防止broker宕机而所有的partition不可消费。

       引入replication之后，同一个partition可能会有多个replication，而这时需要在这些replication之间选出一个**leader**，producer和consumer只与这个**leader交互**，其它replication作为follower从leader 中复制数据。

    4. producer 生产流程:

    ![Selection_037](kafka.assets/Selection_037.png)

  - Borker保存消息

    1. 物理上把topic分成一个或多个patition（

       > 对应 server.properties 中的 num.partitions=3配置）
       >
       > 每个patition物理上对应一个**文件夹**（该文件夹存储该patition的所有消息和索引文件）

    2. 存储策略: 

       基于时间、基于大小

    3. zookeeper 存储结构

       ![image-20201027110532815](kafka.assets/image-20201027110532815.png)

  - 消费过程分析 (高级API和低级API)

    1. 高级API

       不用管理offset(通过zookeeper自行管理，记录上次的offset,  默认是1分钟更新一次offset)

       不用管理分区、副本(系统自动管理)

    2. 低级API

       可以自己控制offset (妈妈再也不用担心我的offset， 想读哪里读哪里, 对zookeeper的依赖性降低)

    3. 消费者组 (消费一个topic, 每个partition 同一时间只能由group中的一个消费者读取 ) 

       ![Selection_038](kafka.assets/Selection_038.png)

    4. 消费方式

       **pull(拉)**模式从broker中自主读取数据

       > 根据consumer的消费能力 以适合的速率消费消息
       >
       > 为了防止在pull的时候 broker没有数据 ，在pull中加入参数(等待给定的字节数) 允许消费者请求等待数据到达的"长轮询"中进行阻塞

- Kafka API 

  - a

    

  - 



























