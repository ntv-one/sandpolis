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
package com.sandpolis.server.vanilla.exe;

import static com.sandpolis.core.instance.store.thread.ThreadStore.ThreadStore;
import static com.sandpolis.core.instance.util.ProtoUtil.begin;
import static com.sandpolis.core.instance.util.ProtoUtil.failure;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;
import com.sandpolis.core.net.command.Exelet;
import com.sandpolis.core.proto.net.Message.MSG;
import com.sandpolis.core.proto.net.MsgGenerator.RQ_Generate;
import com.sandpolis.core.proto.net.MsgGenerator.RS_Generate;
import com.sandpolis.server.vanilla.gen.Generator;
import com.sandpolis.server.vanilla.gen.MegaGen;

/**
 * Generator message handlers.
 *
 * @author cilki
 * @since 5.0.0
 */
public final class GenExe extends Exelet {

	private static final Logger log = LoggerFactory.getLogger(GenExe.class);

	@Auth
	@Handler(tag = MSG.RQ_GENERATE_FIELD_NUMBER)
	public static MessageOrBuilder rq_generate(RQ_Generate rq) throws Exception {
		ExecutorService pool = ThreadStore.get("server.generator");

		Future<MessageOrBuilder> future = pool.submit(() -> {
			var outcome = begin();

			Generator generator;
			switch (rq.getConfig().getPayload()) {
			case OUTPUT_MEGA:
				generator = MegaGen.build(rq.getConfig());
				break;
			case OUTPUT_MICRO:
			default:
				log.warn("No generator found for type: {}", rq.getConfig().getPayload());
				return failure(outcome);
			}

			generator.run();

			var rs = RS_Generate.newBuilder().setReport(generator.getReport());
			if (generator.getResult() != null)
				rs.setOutput(ByteString.copyFrom(generator.getResult()));

			return rs;
		});

		return future.get();
	}

	private GenExe() {
	}
}
