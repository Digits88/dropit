package com.anghiari.dropit.requestserver.service;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.anghiari.dropit.commons.DropItPacket;

/**
 * @author madhawa
 * 
 */
public class ServerClient {
	final String host;
	final int port;
	final DropItPacket hashPacket;

	public ServerClient(String[] args, DropItPacket hashPacket) {
		this.host = args[0];
		this.port = Integer.parseInt(args[1]);
		this.hashPacket = hashPacket;
	}

	public void sendHash() throws Exception {
		ChannelFactory factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool(), 3);
		// Create the bootstrap
		ClientBootstrap bootstrap = new ClientBootstrap(factory);
		// Create the global ChannelGroup
		ChannelGroup channelGroup = new DefaultChannelGroup(ServerClient.class.getName());
		// Create the associated Handler
		ServerClientHandler handler = new ServerClientHandler(hashPacket);

		// Add the handler to the pipeline and set some options
		bootstrap.getPipeline().addLast("handler", handler);
		bootstrap.setOption("tcpNoDelay", true);
		bootstrap.setOption("keepAlive", true);
		bootstrap.setOption("reuseAddress", true);
		bootstrap.setOption("connectTimeoutMillis", 100);

		// *** Start the Netty running ***

		// Connect to the server, wait for the connection and get back the
		// channel
		Channel channel = bootstrap.connect(new InetSocketAddress(host, port))
				.awaitUninterruptibly().getChannel();
		// Add the parent channel to the group
		channelGroup.add(channel);
		// Wait for the response from the fileServer
		DropItPacket respondHashPacket = handler.getRespond();

		// *** Start the Netty shutdown ***

		// Now close all channels
		System.out.println("close channelGroup");
		channelGroup.close().awaitUninterruptibly();
		// Now release resources
		System.out.println("close external resources");
		factory.releaseExternalResources();
	}
}