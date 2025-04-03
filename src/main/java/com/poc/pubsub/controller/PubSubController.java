package com.poc.pubsub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import com.poc.pubsub.service.OutboundChanel;

@Controller
public class PubSubController {
	
	@Autowired
	OutboundChanel messagingGateway;
	
	@PostMapping("/publishMessage")
	public RedirectView publishMessage(@RequestParam("message") String message) {
        System.out.println("Received message to publish: " + message);
        messagingGateway.sendMsgToPubSub(message);
        System.out.println("Message published, redirecting to index page.");
        return new RedirectView("/");
    }

}
