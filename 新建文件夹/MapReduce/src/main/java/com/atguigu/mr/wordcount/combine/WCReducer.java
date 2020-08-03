package com.atguigu.mr.wordcount.combine;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/*
 * 1. Reducer需要复合Hadoop的Reducer规范
 * 
 * 2. KEYIN, VALUEIN: Mapper输出的keyout-valueout
 * 		KEYOUT, VALUEOUT: 自定义
 * 			
 */		
public class WCReducer extends Reducer<Text, IntWritable, Text, IntWritable>{
	
	private IntWritable out_value=new IntWritable();
	
	// reduce一次处理一组数据，key相同的视为一组
	@Override
	protected void reduce(Text key, Iterable<IntWritable> values,
			Reducer<Text, IntWritable, Text, IntWritable>.Context context) throws IOException, InterruptedException {
		
		int sum=0;
		
		for (IntWritable intWritable : values) {
			
			sum+=intWritable.get();
			
		}
		
		out_value.set(sum);
		
		//将累加的值写出
		context.write(key, out_value);
		
	}

}
