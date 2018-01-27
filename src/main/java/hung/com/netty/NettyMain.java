package hung.com.netty;

import java.net.InetSocketAddress;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyMain {

	public static void main(String[] args) throws Exception{
		EventLoopGroup group = new NioEventLoopGroup();

		try{
			//create a tcp server
		    ServerBootstrap serverBootstrap = new ServerBootstrap();
		    serverBootstrap.group(group);
		    serverBootstrap.channel(NioServerSocketChannel.class);
		    serverBootstrap.localAddress(new InetSocketAddress("localhost", 9999));
		    
		    //capture the socket connect event (new socket)
		    serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
		        protected void initChannel(SocketChannel socketChannel) throws Exception {
		            socketChannel.pipeline().addLast(new HelloServerHandler());
		        }
		    });
		    
		    //start tcp server
		    ChannelFuture channelFuture = serverBootstrap.bind().sync();
		    channelFuture.channel().closeFuture().sync();
		} catch(Exception e){
		    e.printStackTrace();
		} finally {
		    group.shutdownGracefully().sync();
		}

	}

}
