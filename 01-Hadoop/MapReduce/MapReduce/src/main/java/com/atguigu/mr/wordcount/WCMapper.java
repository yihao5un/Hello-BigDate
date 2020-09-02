package com.atguigu.mr.wordcount;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.RecordReader;

/*
 * 注意：导包时，导入 org.apache.hadoop.mapreduce包下的类(2.0的新api)
 * 
 * 1. 自定义的类必须复合MR的Mapper的规范
 * 
 * 2.在MR中，只能处理key-value格式的数据
 * 	 KEYIN, VALUEIN： mapper输入的k-v类型。 由当前Job的InputFormat的RecordReader决定！
 * 		封装输入的key-value由RR自动进行。
 * 
 *   KEYOUT, VALUEOUT： mapper输出的k-v类型: 自定义
 *   
 *  3. InputFormat的作用：
 *  		①验证输入目录中文件格式，是否符合当前Job的要求
 *  		②生成切片，每个切片都会交给一个MapTask处理
 *  		③提供RecordReader，由RR从切片中读取记录，交给Mapper进行处理
 *  
 *  	方法： List<InputSplit> getSplits： 切片
 *  		RecordReader<K,V> createRecordReader： 创建RR
 *  
 *  	默认hadoop使用的是TextInputFormat，TextInputFormat使用LineRecordReader!
 *  
 *  4. 在Hadoop中，如果有Reduce阶段。通常key-value都需要实现序列化协议！
 *  		
 *  			MapTask处理后的key-value，只是一个阶段性的结果！
 *  			这些key-value需要传输到ReduceTask所在的机器！
 *  			将一个对象通过序列化技术，序列化到一个文件中，经过网络传输到另外一台机器，
 *  			再使用反序列化技术，从文件中读取数据，还原为对象是最快捷的方式！
 *  
 *  	java的序列化协议： Serilizxxxxx
 *  			特点：不仅保存对象的属性值，类型，还会保存大量的包的结构，子父类和接口的继承信息！
 *  				重
 *  	hadoop开发了一款轻量级的序列化协议： Wriable机制！
 *  		
 *  	
 *  	
 *  
 *  			
 *  
 *  
 */
public class WCMapper extends Mapper<LongWritable, Text, Text, IntWritable>{
	
	private Text out_key=new Text();
	private IntWritable out_value=new IntWritable(1);
	
	// 针对输入的每个 keyin-valuein调用一次   （0，hello	hi	hello	hi）
	@Override
	protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, IntWritable>.Context context)
			throws IOException, InterruptedException {
	
		System.out.println("keyin:"+key+"----keyout:"+value);
		
		String[] words = value.toString().split("\t");
		
		for (String word : words) {
			
			out_key.set(word);
			
			//写出数据(单词,1)
			context.write(out_key, out_value);
			
		}	
	}

}
