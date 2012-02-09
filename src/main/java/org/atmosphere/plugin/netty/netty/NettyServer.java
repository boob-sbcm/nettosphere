/*
 * Copyright 2012 Jeanfrancois Arcand
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
/**
 * NOTE: This code was inspired/duplicated from APL 2 project called https://github.com/devsprint/jersey-netty-container
 */
package org.atmosphere.plugin.netty.netty;

import java.net.SocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class NettyServer {

	private static final Logger LOG = LoggerFactory.getLogger(NettyServer.class);
	private static final ChannelGroup ALL_CHANNELS = new DefaultChannelGroup("atmosphere");
	private final ChannelPipelineFactory pipelineFactory;
	private final ServerBootstrap bootstrap;
	private final SocketAddress localSocket;

	NettyServer(final ChannelPipelineFactory pipelineFactory,
			final SocketAddress localSocket) {
		this.pipelineFactory = pipelineFactory;
		this.localSocket = localSocket;
		this.bootstrap = buildBootstrap();

	}

	public void startServer() {
		LOG.info("Starting server....");
		final Channel serverChannel = bootstrap.bind(localSocket);
		ALL_CHANNELS.add(serverChannel);
	}

	public void stopServer() {
		LOG.info("Stopping server....");
		final ChannelGroupFuture future = ALL_CHANNELS.close();
		future.awaitUninterruptibly();
		bootstrap.getFactory().releaseExternalResources();
		ALL_CHANNELS.clear();

	}

	private ServerBootstrap buildBootstrap() {
		final ServerBootstrap bootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));

		bootstrap.setPipelineFactory(pipelineFactory);
		return bootstrap;
	}

}
