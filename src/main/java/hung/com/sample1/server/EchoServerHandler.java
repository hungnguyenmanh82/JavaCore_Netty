package hung.com.sample1.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Handler implementation for the echo server.
 */
@Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
	private static final Logger log = LogManager.getLogger("root");
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
    	log.info("***msg="+ msg);
    	ByteBuf buf = (ByteBuf)msg;
    	log.info("readIndex:"+buf.readerIndex() + "  writeIndex:"+buf.writerIndex() + "  capacity:"+buf.capacity());
    	ByteArrayOutputStream bOutput = new ByteArrayOutputStream();
    	
    	try {
			buf.getBytes(buf.readerIndex(), bOutput, buf.writerIndex() - buf.readerIndex());
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	log.info(bOutput.toString());
    	
//        ctx.write(msg);   //send data to client
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
    	log.info("***");
//        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}