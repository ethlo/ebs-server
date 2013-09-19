package com.ethlo.bucketstore.server.grizzly;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.util.HttpStatus;

import com.ethlo.bucketstore.server.StoreOperations;

/**
 * 
 * @author mha
 *
 */
public class EbsHttpHandler extends HttpHandler
{
	private StoreOperations store;
	
	public EbsHttpHandler(StoreOperations store)
	{
		this.store = store;
	}
	
	@Override
    public void service(Request request, Response response) throws Exception
    {
        final String method = request.getMethod().getMethodString();
        final String pathInfo = request.getRequestURI();
        final String bucketName = getBucketName(pathInfo);
        final ByteBuffer key = getKey(pathInfo);
        if ("GET".equalsIgnoreCase(method))
        {
        	final InputStream data = store.get(bucketName, key);
        	if (data != null)
        	{
        		IOUtils.copy(data, response.getOutputStream());
        	}
        	else
        	{
        		response.sendError(HttpStatus.NOT_FOUND_404.getStatusCode());
			}
        }
        else if ("PUT".equalsIgnoreCase(method))
        {
        	store.put(bucketName, key, request.getInputStream());
        }
        else if ("DELETE".equalsIgnoreCase(method))
        {
        	store.delete(bucketName, key);
        }
        else
        {
        	response.sendError(HttpStatus.METHOD_NOT_ALLOWED_405.getStatusCode());
		}
    }

	private ByteBuffer getKey(String pathInfo)
	{
		final String filename = FilenameUtils.getName(pathInfo);
		return ByteBuffer.wrap(filename.getBytes(StandardCharsets.UTF_8));
	}

	private String getBucketName(String pathInfo)
	{
		return FilenameUtils.getPath(pathInfo).split("/")[0];
	}
}
