# Hive

Facebook 开源的 用于解决海量***结构化*** 日志的数据统计。基于Hadoop的***数据仓库工具*** 。可以将结构化的数据文件映射为一张表，并提供***类SQL***查询功能。

##### 本质是: 将HQL转化成MapReduce程序

![081900](Hive.assets/081900-1597829281766.png)

1）Hive处理的数据存储在HDFS

2）Hive分析数据底层的实现是MapReduce

3）执行程序运行在Yarn上



优点

1. 操作接口采用***类SQL语法***，提供快速开发的能力（简单、容易上手）。
2. 避免了去写MapReduce，减少开发人员的学习成本。
3. Hive的***执行延迟比较高***，因此Hive常用于***数据分析***，对实时性要求不高的场合。
4. Hive优势在于***处理大数据***，对于处理小数据没有优势，因为Hive的执行延迟比较高。
5. Hive支持用户***自定义函数***，用户可以根据自己的需求来实现自己的函数。



缺点

1. Hive的HQL表达能力有限

​	（1）迭代式算法无法表达

​	（2）数据挖掘方面不擅长

2. Hive的效率比较低

​	（1）Hive自动生成的MapReduce作业，通常情况下不够智能化

​	（2）Hive调优比较困难，粒度较粗



Hive架构原理

![image-20200819173658208](Hive.assets/image-20200819173658208.png)

1. 用户接口：Client

   CLI（hive shell）、JDBC/ODBC(java访问hive)、WEBUI（浏览器访问hive）

2. 元数据：Metastore

   元数据包括：表名、表所属的数据库（默认是default）、表的拥有者、列/分区字段、表的类型（是否是外部表）、表的数据所在目录等；

   默认存储在自带的derby数据库中，推荐使用MySQL存储Metastore

3. Hadoop

   使用HDFS进行存储，使用MapReduce进行计算。

4. 驱动器：Driver

   （1）解析器（SQL Parser）：将SQL字符串转换成抽象语法树AST，这一步一般都用第三方工具库完成，比如antlr；对AST进行语法分析，比如表是否存在、字段是否存在、SQL语义是否有误。

   （2）编译器（Physical Plan）：将AST编译生成逻辑执行计划。

   （3）优化器（Query Optimizer）：对逻辑执行计划进行优化。

   （4）执行器（Execution）：把逻辑执行计划转换成可以运行的物理计划。对于Hive来说，就是MR/Spark。

![081901](Hive.assets/081901.png)

Hive数据的存储

1. Hive要分析的数据是存储在HDFS上
   		hive中的**库**的位置，在hdfs上就是一个**目录**
   		hive中的**表**的位置，在hdfs上也是一个目录，在所在的库目录下创建了一个**子目录**
   		hive中的**数据**，是存在在表**目录中的文件**

2. 在hive中，存储的数据必须是**结构化的数据**，而且
   这个数据的格式要和表的属性紧密相关！
   表在创建时，有分隔符属性，这个分隔符属性，代表在执行MR程序时，使用哪个分隔符去分割每行中的字段

   hive中默认字段的分隔符： ctrl+A, 进入编辑模式，ctrl+V 再ctrl+A

3. hive中的元数据(schema)存储在关系型数据库
   默认存储在derby中！
   derby是使用Java语言编写的一个微型，常用于内嵌在Java中的数据库！
   derby同一个数据库的实例文件不支持多个客户端同时访问！

4. 将hive的元数据的存储设置存储在Mysql中！
   Mysql支持多用户同时访问一个库的信息！

   >注意事项： 
   >
   >①metastore库的字符集必须是latin1
   >②5.5mysql，改 binlog_format=mixed | row 默认为statement mysql的配置文件： /etc/my.cnf

















