package com.atgugiu.mr.flowbean;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/*
 * 1. 统计手机号(String)的上行(long,int)，下行(long,int)，总流量(long,int)
 * 
 * 手机号为key,Bean{上行(long,int)，下行(long,int)，总流量(long,int)}为value
 * 		
 * 
 * 
 * 
 */
public class FlowBeanMapper extends Mapper<LongWritable, Text, Text, FlowBean>{
	
	private Text out_key=new Text();
	private FlowBean out_value=new FlowBean();
	
	// (0,1	13736230513	192.196.100.1	www.atguigu.com	2481	24681	200)
	@Override
	protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, FlowBean>.Context context)
			throws IOException, InterruptedException {
		
		String[] words = value.toString().split("\t");
		
		//封装手机号
		out_key.set(words[1]);
		// 封装上行
		out_value.setUpFlow(Long.parseLong(words[words.length-3]));
		// 封装下行
		out_value.setDownFlow(Long.parseLong(words[words.length-2]));
		
		context.write(out_key, out_value);
	
	}

}
