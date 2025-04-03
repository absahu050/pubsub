package com.poc.pubsub.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;

import com.google.api.client.util.Value;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.integration.outbound.PubSubMessageHandler;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;

@Component
public class PubSubConfig {

	@Value("${google.cloud.project-id}")
	private String googleCloudProjectID;

	@Value("${pubsub.blast-subscription}")
	private String blastSubscription;

	@Value("${pubsub.dead-letter-topic}")
	private String deadLetterTopic;

	private static final String CLASS_NAME = PubSubConfig.class.getName();

	// main topic
	// Create a message channel for messages arriving from the subscription.
	@Bean
	public MessageChannel inputMessageChannel() {
		System.out.println(CLASS_NAME + " - Creating inputMessageChannel");
		return new DirectChannel();
	}

	// Create an inbound channel adapter to listen to the subscription `sub-one` and
	// send messages to the input message channel.
	@Bean
	public PubSubInboundChannelAdapter inboundChannelAdapter(
			@Qualifier("inputMessageChannel") MessageChannel messageChannel, PubSubTemplate pubSubTemplate) {
		System.out.println(
				CLASS_NAME + " - Creating PubSubInboundChannelAdapter for user subscription: " + "user-subscription");
		PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(pubSubTemplate, "user-subscription");
		adapter.setOutputChannel(messageChannel);
		adapter.setAckMode(AckMode.MANUAL);
		return adapter;
	}

	// Define what happens to the messages arriving in the message channel.
	@Bean
	@ServiceActivator(inputChannel = "inputMessageChannel")
	public MessageHandler messageReceiver() {
		return message -> {
			String payload = new String((byte[]) message.getPayload());
			System.out.println(CLASS_NAME + " - Received message in user-sub: " + payload);

			// Simulate a failure condition
			// Do not acknowledge the message to simulate failure
			// The message will eventually be redelivered and moved to the dead-letter topic
			
			if (payload.contains("fail")) {
				System.out.println(CLASS_NAME + " - Simulating message processing failure, not acknowledging");				
			} else {
				BasicAcknowledgeablePubsubMessage originalMessage = message.getHeaders()
						.get(GcpPubSubHeaders.ORIGINAL_MESSAGE, BasicAcknowledgeablePubsubMessage.class);
				if (originalMessage != null) {
					originalMessage.ack();
					System.out.println(CLASS_NAME + " - Acknowledged message");
				}
			}
		};
	}

	// for dead-letter

	@Bean
	public MessageChannel deadLetterInputMessageChannel() {
		System.out.println(CLASS_NAME + " - Creating inputMessageChannel");
		return new DirectChannel();
	}

	// Create an inbound channel adapter to listen to the subscription `sub-one` and
	// send messages to the input message channel.
	@Bean
	public PubSubInboundChannelAdapter deadLetterInboundChannelAdapter(
			@Qualifier("deadLetterInputMessageChannel") MessageChannel inputDeadLetterMessageChannel,
			PubSubTemplate pubSubTemplate) {
		System.out.println(CLASS_NAME + " - Creating PubSubInboundChannelAdapter for dead-letter subscription: "
				+ "asyblast-dead-letter-sub");
		PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(pubSubTemplate,
				"asyblast-dead-letter-sub");
		adapter.setOutputChannel(inputDeadLetterMessageChannel);
		adapter.setAckMode(AckMode.MANUAL);
		// adapter.setPayloadType(String.class);
		return adapter;
	}

	// Define what happens to the messages arriving in the message channel.
	@Bean
	@ServiceActivator(inputChannel = "deadLetterInputMessageChannel")
	public MessageHandler deadLetterMessageReceiver() {
		return message -> {
			System.out.println(
					CLASS_NAME + " - Received message in dead-letter: " + new String((byte[]) message.getPayload()));
			BasicAcknowledgeablePubsubMessage originalMessage = message.getHeaders()
					.get(GcpPubSubHeaders.ORIGINAL_MESSAGE, BasicAcknowledgeablePubsubMessage.class);
			originalMessage.ack();
		};
	}

	// end of dead-letter

	@Bean
	@ServiceActivator(inputChannel = "outboundMessageChannel")
	public MessageHandler messageSender(PubSubTemplate pubsubTemplate) {

		System.out.println(CLASS_NAME + " - Creating PubSubMessageHandler for topic: " + "user-topic");
		PubSubMessageHandler adapter = new PubSubMessageHandler(pubsubTemplate, "user-topic");		
		return adapter;
	}
	
	@MessagingGateway(defaultRequestChannel = "outboundMessageChannel")
	public interface OutboundChanel {
		void sendMsgToPubSub(String msg);
	}
}
