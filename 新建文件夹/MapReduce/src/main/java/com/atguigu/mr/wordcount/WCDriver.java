package com.atguigu.mr.wordcount;

import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/*
 * 1.一旦启动这个线程，运行Job
 * 
 * 2.本地模式主要用于测试程序是否正确！
 * 
 * 3. 报错：
 * 	ExitCodeException exitCode=1: /bin/bash: line 0: fg: no job control
 */
public class WCDriver {
	
	public static void main(String[] args) throws Exception {
		
		Path inputPath=new Path("e:/mrinput/wordcount");
		Path outputPath=new Path("e:/mroutput/wordcount");
		
		/*Path inputPath=new Path("/wordcount");
		Path outputPath=new Path("/mroutput/wordcount");*/
		
		//作为整个Job的配置
		Configuration conf = new Configuration();
		
		/*conf.set("fs.defaultFS", "hdfs://hadoop101:9000");
		
		// 在YARN上运行
		conf.set("mapreduce.framework.name", "yarn");
		// RM所在的机器
		conf.set("yarn.resourcemanager.hostname", "hadoop102");*/
		
		//保证输出目录不存在
		FileSystem fs=FileSystem.get(conf);
		
		if (fs.exists(outputPath)) {
			
			fs.delete(outputPath, true);
			
		}
		
		// ①创建Job
		Job job = Job.getInstance(conf);
		
		// 告诉NM运行时，MR中Job所在的Jar包在哪里
		//job.setJar("MapReduce-0.0.1-SNAPSHOT.jar");
		// 将某个类所在地jar包作为job的jar包
		job.setJarByClass(WCDriver.class);
		
		
		// 为Job创建一个名字
		job.setJobName("wordcount");
		
		// ②设置Job
		// 设置Job运行的Mapper，Reducer类型，Mapper,Reducer输出的key-value类型
		job.setMapperClass(WCMapper.class);
		job.setReducerClass(WCReducer.class);
		
		// Job需要根据Mapper和Reducer输出的Key-value类型准备序列化器，通过序列化器对输出的key-value进行序列化和反序列化
		// 如果Mapper和Reducer输出的Key-value类型一致，直接设置Job最终的输出类型
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		// 设置输入目录和输出目录
		FileInputFormat.setInputPaths(job, inputPath);
		FileOutputFormat.setOutputPath(job, outputPath);
		
		// ③运行Job
		job.waitForCompletion(true);
		
		
	}

}
