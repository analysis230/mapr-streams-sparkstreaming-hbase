/* Copyright (c) 2009 & onwards. MapR Tech, Inc., All rights reserved */
package solution;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class verify_consumer {

    // Declare a new consumer.
    public static KafkaConsumer consumer_pass;
    public static KafkaConsumer consumer_fail;

    public static void main(String[] args) throws IOException {
        configureConsumer(args);

        String topic_pass = "/user/vipulrajan/streaming/pass:test";
        String topic_fail = "/user/vipulrajan/streaming/fail:test";
        
        /*if (args.length == 1) {
            topic = args[0];
        }*/

        List<String> topics_fail = new ArrayList<String>();
        topics_fail.add(topic_fail);
        
        List<String> topics_pass = new ArrayList<String>();
        topics_pass.add(topic_pass);
        
        // Subscribe to the topic.
        consumer_pass.subscribe(topics_pass);
        consumer_fail.subscribe(topics_fail);
        // Set the timeout interval for requests for unread messages.
        long pollTimeOut = 1000;
        long waitTime = 30 * 1000;  // loop for while loop 30 seconds
        long numberOfMsgsReceived_fail = 0;
        long numberOfMsgsReceived_pass = 0;
        
        while (waitTime > 0) {
            // Request unread messages from the topic.
            ConsumerRecords<String, String> msg_fail = consumer_fail.poll(pollTimeOut);
            ConsumerRecords<String, String> msg_pass = consumer_pass.poll(pollTimeOut);
     
            System.out.println("Reading from fail stream");
            if (msg_fail.count() == 0) 
            {
                System.out.println("No messages after 1 second wait.");
            } 
            
            else 
            {
                System.out.println("Read " + msg_fail.count() + " messages");
                numberOfMsgsReceived_fail += msg_fail.count();

                // Iterate through returned records, extract the value
                // of each message, and print the value to standard output.
                Iterator<ConsumerRecord<String, String>> iter = msg_fail.iterator();
                while (iter.hasNext()) 
                {
                    ConsumerRecord<String, String> record = iter.next();
                    
                    System.out.println("Consuming " + record.toString());
                }
                
            
            }
            
            System.out.println("Reading from pass stream");
            if (msg_pass.count() == 0)
            {
            	System.out.println("No message after 1 second wait.");
            }
            
            else
            {
            	System.out.println("Read " + msg_pass.count() + " messages");
            	numberOfMsgsReceived_pass += msg_pass.count();
            	
            	Iterator<ConsumerRecord<String, String>> iter = msg_pass.iterator();
                while (iter.hasNext()) 
                {
                    ConsumerRecord<String, String> record = iter.next();
                    
                    System.out.println("Consuming " + record.toString());
                }
            	
            }
            waitTime = waitTime - 1000; // decrease time for loop
        }
        consumer_pass.close();
        consumer_fail.close();
        System.out.println("Total number of messages failed: " + numberOfMsgsReceived_fail);
        System.out.println("Total number of messages passed: " + numberOfMsgsReceived_pass);
        
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

        consumer_fail = new KafkaConsumer<String, String>(props);
        consumer_pass = new KafkaConsumer<String, String>(props);
        
    }

}
