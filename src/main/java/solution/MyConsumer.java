/* Copyright (c) 2009 & onwards. MapR Tech, Inc., All rights reserved */
package solution;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class MyConsumer {

    // Declare a new consumer.
    public static KafkaConsumer consumer;
    public static KafkaProducer producer_pass;
    public static KafkaProducer producer_fail;
    

    public static void main(String[] args) throws IOException {
        configureConsumer(args);

        String topic = "/user/vipulrajan/streaming/original:sensor";
        String topic_pass = "/user/vipulrajan/streaming/pass:test";
        String topic_fail = "/user/vipulrajan/streaming/fail:test";
        
        if (args.length == 1) {
            topic = args[0];
        }

        List<String> topics = new ArrayList<String>();
        topics.add(topic);
        // Subscribe to the topic.
        consumer.subscribe(topics);

        // Set the timeout interval for requests for unread messages.
        long pollTimeOut = 1000;
        long waitTime = 30 * 1000;  // loop for while loop 30 seconds
        long numberOfMsgsReceived = 0;
        while (waitTime > 0) {
            // Request unread messages from the topic.
            ConsumerRecords<String, String> msg = consumer.poll(pollTimeOut);
            if (msg.count() == 0) {
                System.out.println("No messages after 1 second wait.");
            } else {
                System.out.println("Read " + msg.count() + " messages");
                numberOfMsgsReceived += msg.count();

                // Iterate through returned records, extract the value
                // of each message, and print the value to standard output.
                Iterator<ConsumerRecord<String, String>> iter = msg.iterator();
                while (iter.hasNext()) {
                    ConsumerRecord<String, String> record = iter.next();
                    
                    //System.out.println("Consuming " + record.toString());
                    
                    String[] temp = record.toString().split(",");
                    
                    String[] value = record.toString().split("value = ");
                    
                    double c = Double.parseDouble(temp[9]);
                    if (c == 0)
                    {
                        ProducerRecord<String, String> rec = new ProducerRecord<String, String>(topic_fail, temp[5],value[1] );
                        producer_fail.send(rec);
                        System.out.println("written to fail stream");
                    }
                    else
                    {
                    	ProducerRecord<String, String> rec = new ProducerRecord<String, String>(topic_pass, temp[5],value[1] );
                        producer_pass.send(rec);
                        System.out.println("written to pass stream");
                    }
                    

                }
            }
            waitTime = waitTime - 1000; // decrease time for loop
        }
        consumer.close();
        producer_fail.close();
        producer_pass.close();
        System.out.println("Total number of messages received: " + numberOfMsgsReceived);
        System.out.println("All done.");

    }

    /* Set the value for configuration parameters.*/
    public static void configureConsumer(String[] args) {
        Properties props = new Properties();
        // cause consumers to start at beginning of topic on first read
        props.put("auto.offset.reset", "earliest");
        props.put("key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        //  which class to use to deserialize the value of each message
        props.put("value.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");

        consumer = new KafkaConsumer<String, String>(props);
        
        Properties props2 = new Properties();
        props2.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        props2.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        producer_pass = new KafkaProducer<String, String>(props2);
        producer_fail = new KafkaProducer<String, String>(props2);
    }

}
