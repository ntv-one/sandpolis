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
import static com.sandpolis.server.vanilla.store.listener.ListenerStore.ListenerStore;
import static com.sandpolis.server.vanilla.store.user.UserStore.UserStore;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.sandpolis.core.net.command.ExeletTest;
import com.sandpolis.core.proto.net.MsgListener.RQ_AddListener;
import com.sandpolis.core.proto.pojo.Listener.ListenerConfig;
import com.sandpolis.core.proto.pojo.User.UserConfig;
import com.sandpolis.core.proto.util.Result.Outcome;

class ListenerExeTest extends ExeletTest {

	@BeforeEach
	void setup() {
		UserStore.init(config -> {
			config.ephemeral();

			config.defaults.add(UserConfig.newBuilder().setUsername("junit").setPassword("12345678").build());
		});
		ListenerStore.init(config -> {
			config.ephemeral();

			config.defaults
					.add(ListenerConfig.newBuilder().setOwner("junit").setPort(5000).setAddress("0.0.0.0").build());
		});
		ThreadStore.init(config -> {
			config.ephemeral();

			config.defaults.put("store.event_bus", Executors.newSingleThreadExecutor());
		});

		initTestContext();
	}

	@Test
	void testDeclaration() {
		testNameUniqueness(ListenerExe.class);
	}

	@Test
	@DisplayName("Add a listener with a valid configuration")
	void rq_add_listener_1() {
		var rq = RQ_AddListener.newBuilder()
				.setConfig(ListenerConfig.newBuilder().setId(2).setOwner("junit").setPort(5000).setAddress("0.0.0.0"))
				.build();
		var rs = ListenerExe.rq_add_listener(rq);

		assertTrue(((Outcome) rs).getResult());
	}

}
