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

创建一个新的结点 create /username abelrose

```xml
<dependency>
    <groupId>org.apache.zookeeper</groupId>
    <artifactId>zookeeper</artifactId>
    <version>3.4.12</version>
</dependency>
```

```java
import java.util.concurrent.CountDownLatch;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class ZooKeeperProSync implements Watcher {
 
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zk = null;
    private static Stat stat = new Stat();
 
    public static void main(String[] args) throws Exception {
        //zookeeper配置数据存放路径
        String path = "/username";
        //连接zookeeper并且注册一个默认的监听器
        zk = new ZooKeeper("192.168.31.100:2181", 5000, //
                new ZooKeeperProSync());
        //等待zk连接成功的通知
        connectedSemaphore.await();
        //获取path目录节点的配置数据，并注册默认的监听器
        System.out.println(new String(zk.getData(path, true, stat)));
 
        Thread.sleep(Integer.MAX_VALUE);
    }
 
    public void process(WatchedEvent event) {
        if (KeeperState.SyncConnected == event.getState()) {  //zk连接成功通知事件
            if (EventType.None == event.getType() && null == event.getPath()) {
                connectedSemaphore.countDown();
            } else if (event.getType() == EventType.NodeDataChanged) {  //zk目录节点数据变化通知事件
                try {
                    System.out.println("配置已修改，新值为：" + new String(zk.getData(event.getPath(), true, stat)));
                } catch (Exception e) {
                }
            }
        }
    }
}
```

修改数据 set /username sherlock

监听数据并返回 

abelrose

配置已修改 新值为: sherlock



Zookeeper集群模式的安装

zoo.cfg 中配置 

```
server.1=hadoop101:2888:3888
server.2=hadoop102:2888:3888
server.3=hadoop103:2888:3888
```

标识server ID

myid 1 

myid 2

myid 3


