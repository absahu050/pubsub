package com.poc.pubsub.service;

import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway(defaultRequestChannel = "outboundMessageChannel")
public interface OutboundChanel {
	void sendMsgToPubSub(String msg);
}
