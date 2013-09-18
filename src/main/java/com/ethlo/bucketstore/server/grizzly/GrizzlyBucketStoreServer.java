package com.ethlo.bucketstore.server.grizzly;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.ethlo.bucketstore.server.EbsContextLoader;
import com.ethlo.keyvalue.KeyValueDb;
import com.ethlo.keyvalue.KeyValueDbManager;
import com.google.common.io.BaseEncoding;

/**
 * 
 * @author Morten Haraldsen
 *
 */
public class GrizzlyBucketStoreServer
{
	private final Logger logger = LoggerFactory.getLogger(GrizzlyBucketStoreServer.class);
	private final KeyValueDbManager<ByteBuffer, byte[], KeyValueDb<ByteBuffer, byte[]>> dbManager;
	private final HttpServer server;
	
	public GrizzlyBucketStoreServer(String cfgDir, String host, int port) throws IOException, InterruptedException
	{
		SLF4JBridgeHandler.install();
		
		this.dbManager = new EbsContextLoader(cfgDir).getDbManager();
		
		this.server = new HttpServer();
		this.server.getServerConfiguration().addHttpHandler(new HttpHandler()
		{
			@Override
	        public void service(Request request, Response response) throws Exception
	        {
	            final String method = request.getMethod().getMethodString();
	            final String pathInfo = request.getRequestURI();
	            final KeyValueDb<ByteBuffer, byte[]> db = getDb(pathInfo);
	            final ByteBuffer key = getKey(pathInfo);
	            if ("GET".equalsIgnoreCase(method))
	            {
	            	doGet(db, key, response);
	            }
	            else if ("PUT".equalsIgnoreCase(method))
	            {
	            	doPut(db,  key, request.getInputStream(), response);
	            }
	            else
	            {
	            	response.sendError(HttpStatus.METHOD_NOT_ALLOWED_405.getStatusCode());
				}
	        }
		}, "/");
		
		final NetworkListener listener = new NetworkListener("grizzly", host, port);
        server.addListener(listener);
        
	    server.start();
	    logger.info("Listening on {}, port {}", host, port);
	    Thread.currentThread().join();
	}
	
	private void doGet(KeyValueDb<ByteBuffer, byte[]> db, ByteBuffer key, Response response) throws IOException
	{
		final byte[] value = db.get(key);
		if (value != null)
		{
			response.setHeader("Content-Length", Long.toString(value.length));
			final OutputStream out = response.getOutputStream();
			out.write(value);
			out.flush();
			out.close();
			if (logger.isTraceEnabled())
			{
				logger.trace("Served {} from key {}", FileUtils.byteCountToDisplaySize(value.length), BaseEncoding.base16().encode(key.array()));
			}
		}
		else
		{
			response.sendError(HttpStatus.NOT_FOUND_404.getStatusCode());
		}
	}

	private void doPut(KeyValueDb<ByteBuffer, byte[]> db, ByteBuffer key, InputStream inputStream, Response response) throws IOException
	{
		final byte[] value = IOUtils.toByteArray(inputStream);
		db.put(key, value);
		if (logger.isDebugEnabled())
		{
			logger.debug("Stored {} under key {}", FileUtils.byteCountToDisplaySize(value.length), BaseEncoding.base16().encode(key.array()));
		}
	}
	
	private ByteBuffer getKey(String pathInfo)
	{
		final String filename = FilenameUtils.getName(pathInfo);
		return ByteBuffer.wrap(filename.getBytes(StandardCharsets.UTF_8));
	}

	private KeyValueDb<ByteBuffer, byte[]> getDb(String pathInfo)
	{
		final String dbName = FilenameUtils.getFullPathNoEndSeparator(pathInfo);
		return this.dbManager.getDb(dbName, true);
	}
	
	public static void main(String[] args) throws Exception
	{
		String cfgLocation = "";
		String host = "0.0.0.0";
		int port = 7790;
		
		if (args.length == 3)
		{
			cfgLocation = args[0];
			host = args[1];
			port = Integer.parseInt(args[2]);
		}
		else
		{
			System.err.println("Usage: [configdir] <host> <port>");
			System.exit(1);
		}
		new GrizzlyBucketStoreServer(cfgLocation, host, port);
	}
}