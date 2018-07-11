package org.icepdf.ri.common.utility.queue;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.icepdf.ri.util.PropertiesManager;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;

import com.consultec.esigns.core.queue.IQueueConfig;

/**
 * The Class QueueConfig.
 */
@Configuration
@ComponentScan
@EnableJms
public class ListenerQueueConfig implements IQueueConfig {

	/** The Constant QUEUE_NAME. */
	public static final String QUEUE_NAME = PropertiesManager.getInstance().getValue(PropertiesManager.QUEUE_SERVER_NAME);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.consultec.esigns.core.queue.IQueueConfig#connectionFactory()
	 */
	@Bean
	public ConnectionFactory connectionFactory() {
		PropertiesManager props = PropertiesManager.getInstance();
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(PropertiesManager.getInstance().getValue(PropertiesManager.QUEUE_SERVER_HOST) + ":"
				+ props.getValue(PropertiesManager.QUEUE_SERVER_PORT));
		return connectionFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.consultec.esigns.core.queue.IQueueConfig#jmsListenerContainerFactory()
	 */
	@SuppressWarnings("rawtypes")
	@Bean
	public JmsListenerContainerFactory jmsListenerContainerFactory() {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory());
		// core poll size=4 threads and max poll size 8 threads
		factory.setConcurrency("4-8");
		return factory;
	}

	/**
	 * Send message MQ.
	 *
	 * @param msg
	 *            the msg
	 */
	public void sendMessageMQ(String msg) {
		@SuppressWarnings("resource")
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ListenerQueueConfig.class);
		context.register(ListenerMessageSender.class);
		ListenerMessageSender ms = context.getBean(ListenerMessageSender.class);
		ms.sendMessage(msg);
	}
}