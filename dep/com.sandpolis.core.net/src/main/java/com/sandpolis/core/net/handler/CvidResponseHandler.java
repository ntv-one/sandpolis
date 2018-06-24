/******************************************************************************
 *                                                                            *
 *                    Copyright 2017 Subterranean Security                    *
 *                                                                            *
 *  Licensed under the Apache License, Version 2.0 (the "License");           *
 *  you may not use this file except in compliance with the License.          *
 *  You may obtain a copy of the License at                                   *
 *                                                                            *
 *      http://www.apache.org/licenses/LICENSE-2.0                            *
 *                                                                            *
 *  Unless required by applicable law or agreed to in writing, software       *
 *  distributed under the License is distributed on an "AS IS" BASIS,         *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *
 *  See the License for the specific language governing permissions and       *
 *  limitations under the License.                                            *
 *                                                                            *
 *****************************************************************************/
package com.sandpolis.core.net.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sandpolis.core.instance.Instance;
import com.sandpolis.core.net.Sock;
import com.sandpolis.core.net.exception.MessageFlowException;
import com.sandpolis.core.proto.net.MCCvid.RQ_Cvid;
import com.sandpolis.core.proto.net.MCCvid.RS_Cvid;
import com.sandpolis.core.proto.net.MSG.Message;
import com.sandpolis.core.util.IDUtil;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * This handler manages the CVID handshake from the responding instance. Usually
 * the requesting instance will be the server.
 * 
 * @see CvidResponseHandler
 * 
 * @author cilki
 * @since 5.0.0
 */
@Sharable
public class CvidResponseHandler extends SimpleChannelInboundHandler<Message> {

	private static final Logger log = LoggerFactory.getLogger(CvidResponseHandler.class);

	private int cvid;
	private String uuid;

	public CvidResponseHandler(int cvid, String uuid) {
		this.cvid = cvid;
		this.uuid = uuid;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
		Channel ch = ctx.channel();

		autoremove(ch);
		RQ_Cvid rq = msg.getRqCvid();
		if (rq != null && !rq.getUuid().isEmpty() && IDUtil.CVID.toInstance(rq.getIid()) != Instance.SERVER) {

			RS_Cvid.Builder rs = RS_Cvid.newBuilder().setServerCvid(cvid).setServerUuid(uuid)
					.setCvid(IDUtil.CVID.cvid(rq.getIid()));

			ch.writeAndFlush(Message.newBuilder().setRsCvid(rs).build());

			ch.attr(Sock.INSTANCE_KEY).set(IDUtil.CVID.toInstance(rq.getIid()));
			ch.attr(Sock.CVID_KEY).set(rs.getCvid());
			ch.attr(Sock.UUID_KEY).set(rq.getUuid());
			ch.attr(Sock.CVID_HANDLER_KEY).get().complete(null);
		} else {
			ch.attr(Sock.CVID_HANDLER_KEY).get().completeExceptionally(new MessageFlowException(RQ_Cvid.class, msg));
		}
	}

	/**
	 * Remove the handler from the pipeline once the negotation is over.
	 */
	private void autoremove(Channel channel) {
		channel.pipeline().remove(this);
	}

	@Override
	public boolean isSharable() {
		return true;
	}

}
