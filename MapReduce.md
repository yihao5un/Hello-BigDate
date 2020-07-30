### MapReduce

一个分布式**运算程序**的程序框架 基于Hadoop的**数据的分析应用** 的核心框架

- 核心功能: 将用户的**业务逻辑**代码 和 自带的**默认组件** 整合成一个完整的**分布式运算程序** 并运行在一个Hadoop集群上

- 优点: 1) 易于编程 2) 扩展性好 3) 容错性高 4) 适合PB级别以上海量数据的离线处理 

  缺点: 1) 不可以进行实时计算 2) 不擅长流式计算 3) 不擅长DAG(有向图)计算

- 编程思想

  ![0730](MapReduce.assets/0730.png)

  WordCount 数据流走向

  ![07300](MapReduce.assets/07300.png)

  MapReduce 进程

  ![073000](MapReduce.assets/073000.png)

- MapReduce 的编程规范

  用户编写的程序分为Mapper Reducer Driver

  ![070000](MapReduce.assets/070000.png)

  ![0700000](MapReduce.assets/0700000-1596095627319.png)

























