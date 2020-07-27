### HDFS 

Hadoop Distributed File System 是分布式文件管理系统的一种: 用于**存储**和**管理(通过目录树来定位)多台机器**上的文件. 



特点: 适合**一次**写入 **多次**读出 **不支持**文件的修改。适用于做**数据分析**



HDFS的架构: 

![Capture](HDFS.assets/Capture.PNG)

> 注意: Secondary NameNode 不能作为热备 当NameNode挂的时候 不能立刻换掉NameNode 并提供服务

优点: 

​	(1) 容错性高:  副本可以自动**保存**和**恢复**

​	(2) 适合处理大数据 **数据规模**(PB级) 和**文件规模**(百万)

​	(3) 可构建在**廉价机器**上



缺点:

​	(1) 访问数据的**延时**比较高(做不到毫秒级别)

​	(2) 不支持对文件的**随机写**

> 可支持追加 却不能修改 。
>
> 原因: HDFS 是以块进行存储 修改一个块的话 会影响到当前块之后的所有块 效率较低 其次是HDFS不提供在线寻址的功能

​	(3) 不是用于对**小文件**的存储(会占用大量的NameNode内存 用于存储 文件的属性和信息以及块的映射信息 NameNode 的内存是有限的) 

> 例子: 当前运行NN的机器，有64G内存，除去系统开销，分配给NN50G内存！
>
> 文件a (1k), 存储到HDFS上，需要将a文件的元数据保存到NN，加载到内存
>
> ​		文件名  创建时间  所属主  所属组 权限 修改时间+ 块的映射(1块)
>
> ​		150B   最多存储50G/150B个文件a    50G/150B * 1k
>
> 文件b (128M), 存储到HDFS上，需要将b文件的元数据保存到NN，加载到内存
> 		文件名  创建时间  所属主  所属组 权限 修改时间+块的映射(1块)
>
> ​		150B   最多存储50G/150B个文件b   50G/150B * 128M

​	(4) 只支持同时一个线程(客户端)的写



**HDFS 块的大小 ! !**

![image-20200724105258326](HDFS.assets/image-20200724105258326.png)

>  默认为128M 基于最佳传输损耗理论 在一次传输中，寻址时间占用总传输时间的1%时，本次传输的损耗最小，为最佳性价比传输！

不可以设置太小也不可以设置太大(取决于**磁盘传输速率**): 

​	(1) 太小: 增加寻址时间

​	(2) 太大:  导致磁盘的传输数据时间会明显大于定位这个块所需要的时间 处理的时候很慢

>举例: 
>
>太小：
>			文件a,128M
>			1M一块：  128个块，生成128个块的映射信息
>			128M一块， 1个块，一个块的映射信息
>			①块太小，同样大小的文件，会占用过多的NN的元数据空间
>			②块太小，在进行读写操作时，会消耗额外的寻址时间
>
>太大：  
>			当前有文件a, 1G
>			128M一块  1G存8块   ， 取第一块
>			1G一块       1G存1块   ， 取第一块
>			只需要读取a文件0-128M部分的内容
>			①在一些分块读取的场景，不够灵活，会带来额外的网络消耗
>			②在上传文件时，一旦发生故障，会造成资源的浪费



**HDFS的Shell操作**

先启动Hadoop 集群

```shell
start-dfs.sh
start-yarn.sh
```

```shell
hadoop fs -help 
```

设置副本数量(指最大副本数)

```shell
hadoop fs -setrep 5 /aaa/bbb/ccc.txt # 既可以对本地文件系统进行操作还可以操作分布式文件系统！
hdfs dfs # 只能操作分布式文件系统
```

这里设置的副本数只是记录在**NameNode**的元数据中，是否真的会有这么多副本，还得看**DataNode**的数量。

默认块大小为128M，128M指的是块的最大大小！每个块最多存储128M的数据，如果当前块存储的数据不满128M。存了多少数据，就占用多少的磁盘空间！一个块只属于一个文件！

因为目前只有3台设备，最多也就3个副本，只有节点数的增加到10台时，副本数才能达到10。



**HDFS客户端的操作**

- 服务端: 启动NN DN 

- 将Win10编译后的hadoop解压到没有空格的目录

- 设置HADOOP_HOME 环境变量(D:\Dev\hadoop-2.7.2)和Path环境变量(D:\Dev\hadoop-2.7.2\bin D:\Dev\hadoop-2.7.2\sbin)

- 创建maven工程 并配置 log4j.properties

- 创建包名和TestHDFS类

  - 创建目录

  ```java
  public class TestHDFS{	
  @Test
  public void testMkdirs() throws IOException, InterruptedException, URISyntaxException{
  		
  		// 1 获取文件系统
  		Configuration configuration = new Configuration();
  		// 配置在集群上运行
  		// configuration.set("fs.defaultFS", "hdfs://hadoop101:9000");
  		// FileSystem fs = FileSystem.get(configuration);
  
  		FileSystem fs = FileSystem.get(new URI("hdfs://hadoop101:9000"), configuration, "sherlock");
  		
  		// 2 创建目录
  		fs.mkdirs(new Path("/aaa/bbb/ccc"));
  		
  		// 3 关闭资源
  		fs.close();
  	}
  }
  ```

  - 文件上传

  ```java
  @Test
  public void testCopyFromLocalFile() throws IOException, InterruptedException, URISyntaxException {
  		// 1 获取文件系统
  		Configuration configuration = new Configuration();
  		configuration.set("dfs.replication", "2");
  		FileSystem fs = FileSystem.get(new URI("hdfs://hadoop102:9000"), configuration, "sherlock");
  
  		// 2 上传文件
  		fs.copyFromLocalFile(new Path("d:/aaa.txt"), new Path("/aaa.txt"));
  
  		// 3 关闭资源
  		fs.close();
  
  		System.out.println("upload over");
  }
  ```

  - 拷贝hdfs-site.xml 到项目的根目录

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
    
    <configuration>
    	<property>
    		<name>dfs.replication</name>
            <value>1</value>
    	</property>
    </configuration>
    ```

  - 参数优先级 

    1）客户端代码中设置的值 >（2）ClassPath下的用户自定义配置文件 >（3）然后是服务器的默认配置

  - 文件下载

    ```java
    @Test
    public void testCopyToLocalFile() throws IOException, InterruptedException, URISyntaxException{
    		// 1 获取文件系统
    		Configuration configuration = new Configuration();
    		FileSystem fs = FileSystem.get(new URI("hdfs://hadoop102:9000"), configuration, "sherlock");
    		
    		// 2 执行下载操作
    		// boolean delSrc 指是否将原文件删除
    		// Path src 指要下载的文件路径
    		// Path dst 指将文件下载到的路径
    		// boolean useRawLocalFileSystem 是否开启文件校验
    		fs.copyToLocalFile(false, new Path("/aaa.txt"), new Path("d:/aaa.txt"), true);
    		
    		// 3 关闭资源
    		fs.close();
    }
    ```

  - 文件夹删除

    ```java
    @Test
    public void testDelete() throws IOException, InterruptedException, URISyntaxException{
    	// 1 获取文件系统
    	Configuration configuration = new Configuration();
    	FileSystem fs = FileSystem.get(new URI("hdfs://hadoop102:9000"), configuration, "sherlock");
    		
    	// 2 执行删除
    	fs.delete(new Path("/aaa/"), true);
    		
    	// 3 关闭资源
    	fs.close();
    }
    ```

  - 文件名更改

    ```java
    @Test
    public void testRename() throws IOException, InterruptedException, URISyntaxException{
    	// 1 获取文件系统
    	Configuration configuration = new Configuration();
    	FileSystem fs = FileSystem.get(new URI("hdfs://hadoop102:9000"), configuration, "sherlock"); 
    		
    	// 2 修改文件名称
    	fs.rename(new Path("/aaa.txt"), new Path("/aaa.txt"));
    		
    	// 3 关闭资源
    	fs.close();
    }
    ```

  - 文件详情查看

    ```java
    @Test
    public void testListFiles() throws IOException, InterruptedException, URISyntaxException{
    	// 1获取文件系统
    	Configuration configuration = new Configuration();
    	FileSystem fs = FileSystem.get(new URI("hdfs://hadoop102:9000"), configuration, "sherlock"); 
    		
    	// 2 获取文件详情
    	RemoteIterator<LocatedFileStatus> listFiles = fs.listFiles(new Path("/"), true);
    		
    	while(listFiles.hasNext()){
    		LocatedFileStatus status = listFiles.next();
    			
    		// 输出详情
    		// 文件名称
    		System.out.println(status.getPath().getName());
    		// 长度
    		System.out.println(status.getLen());
    		// 权限
    		System.out.println(status.getPermission());
    		// 分组
    		System.out.println(status.getGroup());
    			
    		// 获取存储的块信息
    		BlockLocation[] blockLocations = status.getBlockLocations();
    			
    		for (BlockLocation blockLocation : blockLocations) {
    				
    			// 获取块存储的主机节点
    			String[] hosts = blockLocation.getHosts();
    				
    			for (String host : hosts) {
    				System.out.println(host);
    			}
    		}
    			
    		System.out.println("-----------分割线----------");
    	}
    // 3 关闭资源
    fs.close();
    }
    ```

  - 文件和文件夹的判断

    ```java
    @Test
    public void testListStatus() throws IOException, InterruptedException, URISyntaxException{
    		
    	// 1 获取文件配置信息
    	Configuration configuration = new Configuration();
    	FileSystem fs = FileSystem.get(new URI("hdfs://hadoop102:9000"), configuration, "sherlock");
    		
    	// 2 判断是文件还是文件夹
    	FileStatus[] listStatus = fs.listStatus(new Path("/"));
    		
    	for (FileStatus fileStatus : listStatus) {
    			
    		// 如果是文件
    		if (fileStatus.isFile()) {
    				System.out.println("f:"+fileStatus.getPath().getName());
    			}else {
    				System.out.println("d:"+fileStatus.getPath().getName());
    			}
    		}
    		
    	// 3 关闭资源
    	fs.close();
    }
    ```

- HDFS的I/O操作

  - a
  - a
  - a
  - 