package com.atguigu.mr.wordcount.keyvalue;

import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.NLineInputFormat;
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
		
		Path inputPath=new Path("e:/mrinput/keyvalue");
		Path outputPath=new Path("e:/mroutput/keyvalue");
	
		//作为整个Job的配置
		Configuration conf = new Configuration();
		
		// 分隔符只是一个byte类型的数据，即便传入的是个字符串，只会取字符串的第一个字符
		conf.set("mapreduce.input.keyvaluelinerecordreader.key.value.separator", "***");
		
		// 设置输入格式
		conf.set("mapreduce.job.inputformat.class", "org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat");
		
		
		//保证输出目录不存在
		FileSystem fs=FileSystem.get(conf);
		
		if (fs.exists(outputPath)) {
			
			fs.delete(outputPath, true);
			
		}
		
		// ①创建Job
		Job job = Job.getInstance(conf);
		
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
		
		// 声明使用NLineInputFormat
		//job.setInputFormatClass(NLineInputFormat.class);
		
		// 设置输入目录和输出目录
		FileInputFormat.setInputPaths(job, inputPath);
		FileOutputFormat.setOutputPath(job, outputPath);
		
		// ③运行Job
		job.waitForCompletion(true);
		
		
	}

}
