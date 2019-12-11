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
package com.sandpolis.core.net.util;

import static com.sandpolis.core.instance.store.thread.ThreadStore.ThreadStore;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import com.sandpolis.core.util.ValidationUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.dns.DefaultDnsQuestion;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsRawRecord;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.handler.codec.dns.DnsResponse;
import io.netty.handler.codec.dns.DnsSection;
import io.netty.resolver.dns.DnsNameResolverBuilder;

/**
 * DNS utilities.
 *
 * @author cilki
 * @since 5.0.0
 */
public final class DnsUtil {

	/**
	 * Get the port associated with an SRV record.
	 *
	 * @param server The DNS name
	 * @return The SRV port
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public static Optional<Integer> getPort(String server) throws InterruptedException, ExecutionException {
		Objects.requireNonNull(server);

		DnsQuestion question = new DefaultDnsQuestion(server, DnsRecordType.SRV);
		DnsResponse response = new DnsNameResolverBuilder(ThreadStore.get("net.dns.resolver"))
				.channelFactory(() -> new NioDatagramChannel()).build().query(question).get().content();
		DnsRawRecord record = response.recordAt(DnsSection.ANSWER);
		if (record == null)
			return Optional.empty();

		ByteBuf buffer = record.content();

		// Skip priority
		buffer.readShort();

		// Skip weight
		buffer.readShort();

		// Read port
		int port = buffer.readShort();
		if (!ValidationUtil.port(port))
			return Optional.empty();

		return Optional.of(port);
	}

	private DnsUtil() {
	}
}
