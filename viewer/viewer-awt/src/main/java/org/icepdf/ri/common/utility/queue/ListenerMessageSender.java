package org.icepdf.ri.common.utility.queue;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.icepdf.ri.util.PropertiesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.consultec.esigns.core.queue.IMessageSender;

/**
 * The Class MessageSender.
 */
@Component("MessageSender")
public class ListenerMessageSender implements IMessageSender {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(ListenerMessageSender.class);

	/** The connection factory. */
	@Autowired
	private ConnectionFactory connectionFactory;

	/** The jms template. */
	private JmsTemplate jmsTemplate;

	/**
	 * Inits the.
	 */
	@PostConstruct
	public void init() {
		this.jmsTemplate = new JmsTemplate(connectionFactory);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.consultec.esigns.core.queue.IMessageSender#sendMessage(java.lang.String)
	 */
	public void sendMessage(final String message) {
		logger.info("sending to ["
				+ PropertiesManager.getInstance().getPreferences().get(PropertiesManager.QUEUE_SERVER_NAME, null) + "]"
				+ message);
		jmsTemplate.send(
				PropertiesManager.getInstance().getPreferences().get(PropertiesManager.QUEUE_SERVER_NAME, null),
				new MessageCreator() {
					public Message createMessage(Session session) throws JMSException {
						return session.createTextMessage(message);
					}
				});
	}
}