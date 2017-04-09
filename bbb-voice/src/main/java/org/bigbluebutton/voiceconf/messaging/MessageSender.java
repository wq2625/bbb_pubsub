package org.bigbluebutton.voiceconf.messaging;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
/*import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;*/

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;

public class MessageSender {
	private static Logger log = Red5LoggerFactory.getLogger(MessageSender.class, "bigbluebutton");
	
	//private JedisPool redisPool;
	private ConnectionFactory connectionFactory;	
	private String host;
	private int port;
	private volatile boolean sendMessage = false;
	
	private final Executor msgSenderExec = Executors.newSingleThreadExecutor();
	private BlockingQueue<MessageToSend> messages = new LinkedBlockingQueue<MessageToSend>();
	
	public void stop() {
		sendMessage = false;
	}
	
	public void start() {	
		connectionFactory = new ActiveMQConnectionFactory("tcp://" + host + ":" + port);
		log.info("ActiveMQ message publisher starting!");
		try {
			sendMessage = true;
			
			Runnable messageSender = new Runnable() {
			    public void run() {
			    	while (sendMessage) {
				    	try {
							MessageToSend msg = messages.take();
							publish(msg.getChannel(), msg.getMessage());
						} catch (InterruptedException e) {
							log.warn("Failed to get message from queue.");
						}    			    		
			    	}
			    }
			};
			msgSenderExec.execute(messageSender);
		} catch (Exception e) {
			log.error("Error subscribing to channels: " + e.getMessage());
		}			
	}
	
	public void send(String channel, String message) {
		MessageToSend msg = new MessageToSend(channel, message);
		messages.add(msg);
	}
	
	private void publish(String channel, String message) {
		//Jedis jedis = redisPool.getResource();
		Connection connection = null;
		try {
			//jedis.publish(channel, message);
			connection = connectionFactory.createConnection();
			connection.start();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Topic t = new ActiveMQTopic(channel);
			
			MessageProducer producer = session.createProducer(t);
			producer.setDeliveryMode(DeliveryMode.PERSISTENT);
			
			TextMessage msg = session.createTextMessage(message);
			
			producer.send(msg);
		} catch(Exception e){
			log.warn("Cannot publish the message to ActiveMQ", e);
		} finally {
			//redisPool.returnResource(jedis);
			if (connection != null) {
                try {
                	connection.close();
                } catch (JMSException e) {
                    log.warn("Could not close an open connection...");
                }
            }
		}
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	/*public void setRedisPool(JedisPool redisPool){
		this.redisPool = redisPool;
	}*/
}