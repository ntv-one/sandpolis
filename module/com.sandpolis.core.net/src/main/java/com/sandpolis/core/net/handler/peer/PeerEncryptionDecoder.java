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
package com.sandpolis.core.net.handler.peer;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

/**
 * This handler decrypts incoming messages on a peer connection.
 *
 * @author cilki
 * @since 5.0.0
 */
public class PeerEncryptionDecoder extends MessageToMessageDecoder<ByteBuf> {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
		// TODO Auto-generated method stub
		out.add(msg);
	}

}
