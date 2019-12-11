//============================================================================//
//                                                                            //
//                Copyright © 2015 - 2020 Subterranean Security               //
//                                                                            //
//  This source file is subject to the terms of the Mozilla Public License    //
//  version 2. You may not use this file except in compliance with the MPL    //
//  as published by the Mozilla Foundation at:                                //
//                                                                            //
//    https://mozilla.org/MPL/2.0                                             //
//                                                                            //
//=========================================================S A N D P O L I S==//
package com.sandpolis.core.net.future;

import com.sandpolis.core.net.ChannelConstant;
import com.sandpolis.core.net.sock.Sock;

import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;

/**
 * A {@link SockFuture} is a wrapper for {@link ChannelFuture} which provides an
 * easy way to obtain a configured {@link Sock}.
 *
 * @author cilki
 * @since 5.0.0
 */
public class SockFuture extends DefaultPromise<Sock> {

	/**
	 * Construct a new {@link SockFuture} that is not waiting on a
	 * {@link ChannelFuture} directly.
	 *
	 * @param executor A custom executor
	 */
	public SockFuture(EventExecutor executor) {
		super(executor);
	}

	/**
	 * Construct a new {@link SockFuture} which will create a new {@link Sock} once
	 * the given connection is established.
	 *
	 * @param connect A connection future
	 */
	public SockFuture(ChannelFuture connect) {
		this(connect.channel().eventLoop(), connect);
	}

	/**
	 * Construct a new {@link SockFuture} which will create a new {@link Sock} once
	 * the given connection is established.
	 *
	 * @param executor A custom executor
	 * @param connect  A connection future
	 */
	public SockFuture(EventExecutor executor, ChannelFuture connect) {
		super(executor);

		connect.addListener((ChannelFuture future) -> {
			if (!future.isSuccess()) {
				SockFuture.this.setFailure(future.cause());
				return;
			}

			future.channel().attr(ChannelConstant.SOCK).get().getHandshakeFuture().addListener(cvidFuture -> {
				if (cvidFuture.isSuccess()) {
					SockFuture.this.setSuccess(future.channel().attr(ChannelConstant.SOCK).get());
				} else {
					SockFuture.this.setFailure(cvidFuture.cause());
				}
			});
		});
	}
}
