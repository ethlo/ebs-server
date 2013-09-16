package com.ethlo.bucketstore.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import com.ethlo.keyvalue.CasKeyValueDb;
import com.ethlo.keyvalue.KeyValueDb;
import com.ethlo.keyvalue.KeyValueDbManager;
import com.ethlo.keyvalue.hashmap.HashmapKeyValueDbManager;

/**
 * 
 * @author Morten Haraldsen
 *
 */
public class BucketStoreServer extends HttpServlet
{
	private static final long serialVersionUID = -181396322687204009L;
	
	// Backing store
	private KeyValueDbManager<ByteBuffer, byte[], CasKeyValueDb<ByteBuffer, byte[], Long>> dbManager = new HashmapKeyValueDbManager();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		final KeyValueDb<ByteBuffer , byte[]> db = getDb(req.getPathInfo());
		final byte[] value = db.get(getKey(req.getPathInfo()));
		if (value != null)
		{
			resp.sendError(HttpServletResponse.SC_OK);
			resp.setHeader("Content-Length", Long.toString(value.length));
			IOUtils.copy(new ByteArrayInputStream(value), resp.getOutputStream());
		}
		else
		{
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	private KeyValueDb<ByteBuffer, byte[]> getDb(String pathInfo)
	{
		final String dbName = FilenameUtils.getFullPathNoEndSeparator(pathInfo);
		return this.dbManager.getDb(dbName, true);
	}

	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		final KeyValueDb<ByteBuffer , byte[]> db = getDb(req.getPathInfo());
		final byte[] value = db.get(getKey(req.getPathInfo()));
		if (value != null)
		{
			resp.sendError(HttpServletResponse.SC_OK);
		}
		else
		{
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		final KeyValueDb<ByteBuffer , byte[]> db = getDb(req.getPathInfo());
		final ByteBuffer key = getKey(req.getPathInfo());
		final byte[] value = IOUtils.toByteArray(req.getInputStream());
		db.put(key, value);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		final KeyValueDb<ByteBuffer , byte[]> db = getDb(req.getPathInfo());
		final ByteBuffer key = getKey(req.getPathInfo());
		db.delete(key);
	}
	
	private ByteBuffer getKey(String pathInfo)
	{
		final String filename = FilenameUtils.getName(pathInfo);
		return ByteBuffer.wrap(filename.getBytes(StandardCharsets.UTF_8));
	}

}