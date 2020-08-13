### zookeeper

Apache Hadoop的一个子项目 zookeeper = 文件系统+监听通知机制

- 文件系统(类似于Unix)

  ![img](zookeeper.assets/201807121434154)

  > 每个子目录项如 NameService 都被称作为 znode(目录节点)，和文件系统一样，我们能够自由的增加、删除znode，在一个znode下增加、删除子znode，唯一的不同在于znode是可以存储数据的。

  四种类型的znode

  - **PERSISTENT-持久化目录节点**
  - **PERSISTENT_SEQUENTIAL-持久化顺序编号目录节点**
  - **EPHEMERAL-临时目录节点**
  - **EPHEMERAL_SEQUENTIAL-临时顺序编号目录节点**

- 监听通知系统

  客户端注册监听它关心的目录节点，当目录节点发生变化（数据改变、被删除、子目录节点增加删除）时，zookeeper会通知客户端。



功能:

***分布式应用******配置管理***、***统一命名服务***、***状态同步服务***、***集群管理***等

- 分布式的配置管理

![img](zookeeper.assets/20180712143454552)



> 将这些配置全部放到zookeeper上去，保存在 zookeeper  的某个目录节点中，然后所有相关应用程序对这个目录节点进行***监听***，一旦配置信息***发生变化***，每个应用程序就会收到 zookeeper 的通知，然后从  zookeeper 获取新的配置信息应用到系统中。



客户端的使用:

- 启动

```shell
./zkServer.sh start
```

```shell
./zkCli.sh
```

- 创建新结点znode

```shell
create /zKPro myData
ls /   # [zkPro, zookeeper]
get /zkPro #myData
```

- 更新

```shell
set /zkPro myData123
```

- 删除

```shell
delete /zkPro
```



使用JavaAPI操作zookeeper



Zookeeper集群模式的安装

























































