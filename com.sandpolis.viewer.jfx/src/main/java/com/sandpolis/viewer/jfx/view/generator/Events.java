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
package com.sandpolis.viewer.jfx.view.generator;

import com.sandpolis.core.instance.event.Event;
import com.sandpolis.core.instance.event.ParameterizedEvent;
import com.sandpolis.core.proto.net.MsgGenerator.RS_Generate;

public final class Events {

	/**
	 * Add a new network target to the configuration tree.
	 */
	public static class AddServerEvent extends ParameterizedEvent<AddServerEvent.Container> {
		public static final class Container {
			public final String address;
			public final String port;
			public final String strict_certs;
			public final String cooldown;

			public Container(String address, String port, String strict_certs, String cooldown) {
				this.address = address;
				this.port = port;
				this.strict_certs = strict_certs;
				this.cooldown = cooldown;
			}
		}
	}

	/**
	 * Add a new plugin to the configuration tree.
	 */
	public static class AddPluginEvent extends ParameterizedEvent<AddPluginEvent.Container> {
		public static final class Container {

		}
	}

	/**
	 * Add a new auth group to the configuration tree.
	 */
	public static class AddGroupEvent extends ParameterizedEvent<AddGroupEvent.Container> {
		public static final class Container {

		}
	}

	/**
	 * Close the detail panel.
	 */
	public static class DetailCloseEvent extends Event {
	}

	/**
	 * Indicates that a generation attempt has completed.
	 */
	public static class GenerationCompletedEvent extends ParameterizedEvent<RS_Generate> {
	}

	/**
	 * Indicates that the output location has been changed.
	 */
	public static class OutputLocationChangedEvent extends ParameterizedEvent<String> {
	}

	/**
	 * Indicates that the output format has been changed.
	 */
	public static class OutputFormatChangedEvent extends ParameterizedEvent<String> {
	}

}
