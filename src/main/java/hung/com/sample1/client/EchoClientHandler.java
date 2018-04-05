package hung.com.sample1.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class EchoClientHandler extends ChannelInboundHandlerAdapter {
	private static final Logger log = LogManager.getLogger("root");
	
	private final ByteBuf firstMessage;

	/**
	 * Creates a client-side handler.
	 */
	public EchoClientHandler() {
		firstMessage = Unpooled.buffer(256);
		String string = "ko co viec gi kho";
		
		firstMessage.writeBytes(string.getBytes());
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		//the first time when Client connected to server
		log.debug("*");
		ctx.writeAndFlush(firstMessage);   //send data to server
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		//socket read from server
		log.debug("*");
//		ctx.write(msg);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		log.debug("*");
//		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// Close the connection when an exception is raised.
		cause.printStackTrace();
		ctx.close();
	}
}
