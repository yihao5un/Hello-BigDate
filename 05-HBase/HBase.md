### HBase

- NOSQL(Not Only SQL)

  高并发的更新(插入、修改、删除)、多表关联后的复杂查询(order by、group by)

  - CAP 定理:

    > 背景: 解决数据库压力的一个好方法是分摊压力，即扩展为**分布式**的数据库。但是，可能会带来一些原子性的问题。没有原子性，事务就无从谈起了，关系型数据库也就失去了存在的意义。

    20世纪90年代初期Berkerly大学有位Eric Brewer教授提出了一个**CAP理论**。

    全称是**Consistency Availability and Partition tolerance**。

    - Consistency（强一致性）：数据更新操作的一致性，所有数据变动都是同步的。
    - Availability（高可用性）：良好的响应性能。
    - Partition tolerance（高分区容错性）：可靠性。

    > 教授说只能满足其中的两点， 没法让三者全部满足。架构师们应该适当的进行取舍

    ![image-20201030103954708](HBase.assets/image-20201030103954708.png)

  - NOSQL

    数据库的**最终一致性**: 即数据的操作存在**延迟**(有时候是不被允许的) 因此推出了NOSQL(非关系型数据库)的概念。

- HBase

  基于BigTable论文研发了BigTable的Java开源版本, 即HBase.

  ![image-20201030104804846](HBase.assets/image-20201030104804846.png)

  - **分布式**、**可扩展**、**支持海量数据存储**的NOSQL数据库。面向**列存储**(即列族，列族下可以有很多的列，需要在建表的时候指定)， 构建于Hadoop之上，提供对1**0亿级别**表数据的快速随机实时读写。

  - 逻辑架构

    ![Selection_041](HBase.assets/Selection_041.png)

  - 物理存储架构

    ![Selection_042](HBase.assets/Selection_042.png)

  - HBase架构

    ![Selection_043](HBase.assets/Selection_043.png)

    1. Region Server

       RegionServer是一个服务，负责多个Region的管理。其实现类为**HRegionServer**

       主要作用如下:

       对于**数据**的操作：**get, put, delete**；

       对于**Region**的操作：**splitRegion**、**compactRegion**。

       **客户端**从**ZooKeeper**获取**RegionServer**的地址，从而调用相应的服务，获取数据。

    2. Master

       Master是所有Region Server的管理者，其实现类为**HMaster**

       主要作用如下：

       对于**表**的操作：**create, delete, alter**，这些操作可能需要跨**多个ReginServer**，因此需要Master来进行协调！

       对于**RegionServer**的操作：分配regions到每个RegionServer，监控每个RegionServer的状态，**负载均衡**和**故障转移**。

       > 即使Master进程宕机，集群依然可以执行数据的读写，只是不能进行表的创建和修改等操作！
       >
       > 当然Master也不能宕机太久，有很多必要的操作，比如创建表、修改列族配置，以及更重要的分割和合并都需要它的操作。

    3. ZooKeeper

       RegionServer非常依赖ZooKeeper服务，ZooKeeper管理了HBase所有RegionServer的信息，包括具体的数据段存放在哪个RegionServer上。

       客户端每次与HBase连接，其实都是先与ZooKeeper通信，查询出哪个RegionServer需要连接，然后再连接RegionServer。Zookeeper中记录了读取数据所需要的**元数据表**hbase:meata,因此关闭Zookeeper后，客户端是无法实现读操作的！

       > HBase通过Zookeeper来做master的高可用、RegionServer的监控、元数据的入口以及集群配置的维护等工作。

    4. HDFS

       **HDFS**为Hbase提供最终的底层**数据存储服务**，同时为HBase提供高可用的支持。

- a

- a

- a

- a

- a

- 、





















- a
- a
- 