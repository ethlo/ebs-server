package com.ethlo.bucketstore.server.grizzly;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.springframework.beans.factory.config.AbstractFactoryBean;

/**
 * 
 * @author Morten Haraldsen
 *
 */
public class GrizzlyFactoryBean extends AbstractFactoryBean<HttpServer>
{
	private String networkInterface = "0.0.0.0";
	private int port = 7790;
	private String path = "/";
	private final HttpHandler httpHandler;
	
	@Override
	public Class<?> getObjectType()
	{
		return HttpServer.class;
	}

	@Override
	protected HttpServer createInstance() throws Exception
	{
		final HttpServer server = new HttpServer();
		server.getServerConfiguration().addHttpHandler(this.httpHandler, path);
		
		final NetworkListener listener = new NetworkListener("grizzly", networkInterface, port);
        server.addListener(listener);
        
	    server.start();
	    
	    Thread.currentThread().join();
	    
	    return server;
	}

	public GrizzlyFactoryBean(HttpHandler httpHandler)
	{
		this.httpHandler = httpHandler;
	}

	public void setNetworkInterface(String networkInterface)
	{
		this.networkInterface = networkInterface;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public void setPath(String path)
	{
		this.path = path;
	}
}
