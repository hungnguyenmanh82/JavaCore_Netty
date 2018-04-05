package hung.com.sample1.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

/**
 * Echoes back any received data from a client.
 */
public final class EchoServer {
	private static final Logger log = LogManager.getLogger("root");
	
	static final boolean SSL = System.getProperty("ssl") != null;
	static final int PORT = Integer.parseInt(System.getProperty("port", "8007"));

	public static void main(String[] args) throws Exception {
		log.debug("start Tcp server: 127.0.0.1:8007");
		// Configure SSL.
		final SslContext sslCtx;
		if (SSL) {
			SelfSignedCertificate ssc = new SelfSignedCertificate();
			sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
		} else {
			sslCtx = null;
		}

		// Configure the server.
		int NumberThreadLoopAcepter = 1 ;
		int NumberThreadLoopSocket = 10;
		EventLoopGroup bossGroup = new NioEventLoopGroup(NumberThreadLoopAcepter); // event of a new connection
		EventLoopGroup workerGroup = new NioEventLoopGroup(NumberThreadLoopSocket); // event of a connected socket
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			//lưu ý 2 EventLoop 1 cho New connect (server), 1 cho các connected Sockets
			bootstrap.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG, 1000)
			.handler(new LoggingHandler(LogLevel.INFO))
			.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					log.debug("*");
					ChannelPipeline p = ch.pipeline();  //quản lý chung các filters (interceptor)
					if (sslCtx != null) {
						p.addLast(sslCtx.newHandler(ch.alloc()));
					}
					//p.addLast(new LoggingHandler(LogLevel.INFO));
					
					//EchoServerHandler => chính là phần xử lý cho 1 socket cụ thể
					//giống với filter, có thể add nhiều filter
					//EchoServerHandler có thể hiểu là Context để callback function thực hiện
					p.addLast(new EchoServerHandler());  
				}
			});

			// Start the server.
			ChannelFuture f = bootstrap.bind(PORT).sync();

			// Wait until the server socket is closed.
			f.channel().closeFuture().sync();
		} finally {
			// Shut down all event loops to terminate all threads.
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
