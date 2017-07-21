package com.sparkStreaming;
import java.util.HashMap;
import java.util.HashSet;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import scala.Tuple2;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
//import org.apache.spark.serializer.KryoSerializer;
import org.apache.spark.streaming.api.java.*;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import kafka.serializer.StringDecoder;

import com.mytest.JavaAPIMain;

import org.apache.spark.streaming.Durations;

/**
 * Consumes messages from one or more topics in Kafka and does wordcount.
 * Usage: JavaDirectKafkaWordCount <brokers> <topics>
 *   <brokers> is a list of one or more Kafka brokers
 *   <topics> is a list of one or more kafka topics to consume from
 *
 * Example:
 *    $ bin/run-example streaming.JavaDirectKafkaWordCount broker1-host:port,broker2-host:port \
 *      topic1,topic2
 */

public final class JavaDirectKafkaWordCount {
  private static final Pattern SPACE = Pattern.compile(" ");
  static InputStream input = null;

  public static void main(String[] args) throws Exception {
	
	  Properties properties = new Properties();
	  input = new FileInputStream("config.properties");
	  properties.load(input);
	  
	  
	  Map<String, Object> kafkaParams = new HashMap<String, Object>();
		kafkaParams.put("bootstrap.servers",properties.getProperty("KAFKA_BOOTSTRAP_SERVERS"));
		kafkaParams.put("key.deserializer",properties.getProperty("KAFKA_KEY_DESERAILIZER"));
		kafkaParams.put("value.deserializer",properties.getProperty("KAFKA_VALUE_DESERAILIZER"));
		kafkaParams.put("group.id",properties.getProperty("KAFKA_GROUP_ID"));
		

		Collection<String> topics = Arrays.asList("test");

    // Create context with a 2 seconds batch interval
    SparkConf sparkConf = new SparkConf().setAppName("JavaDirectKafkaWordCount").setMaster("local[2]");
    JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, Durations.seconds(2));


   
    // Create direct kafka stream with brokers and topics
    	JavaInputDStream<ConsumerRecord<String,String>> messages=KafkaUtils.createDirectStream(
    	    jssc,
    	    LocationStrategies.PreferConsistent(),
    	    ConsumerStrategies.<String, String>Subscribe(topics, kafkaParams)
    	  );

    	messages.mapToPair(
    			  new PairFunction<ConsumerRecord<String, String>, String, String>() {
    			  private static final long serialVersionUID = 1L;

    				public Tuple2<String, String> call(ConsumerRecord<String, String> record) {
    			    System.out.println("key"+record.key()+"record"+record.value());
    					return new Tuple2<String, String>(record.key(), record.value());
    			    }
    			  });
    	
    	messages.foreachRDD(new VoidFunction<JavaRDD<ConsumerRecord<String,String>>>() {
			
			public void call(JavaRDD<ConsumerRecord<String, String>> data) throws Exception {
				// TODO Auto-generated method stub
				System.out.println(g.count());
				
				JavaAPIMain obj =new JavaAPIMain();
				obj.test(""+data);
				System.out.println("data Saved"+data);
					
			}
		});
    	    	
    // Get the lines, split them into words, count the words and print
    
    // Start the computation
    jssc.start();
    jssc.awaitTermination();
  }
}