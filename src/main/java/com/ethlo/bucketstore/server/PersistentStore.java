package com.ethlo.bucketstore.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.apache.commons.io.IOUtils;

import com.ethlo.keyvalue.KeyValueDb;
import com.ethlo.keyvalue.KeyValueDbManager;

/**
 * 
 * @author Morten Haraldsen
 *
 */
public class PersistentStore implements StoreOperations
{
	private final KeyValueDbManager<ByteBuffer, byte[], KeyValueDb<ByteBuffer, byte[]>> dbManager;
	
	public PersistentStore(KeyValueDbManager<ByteBuffer, byte[], KeyValueDb<ByteBuffer, byte[]>> dbManager)
	{
		this.dbManager = dbManager;
	}

	@Override
	public void put(String bucketName, ByteBuffer key, InputStream data) throws IOException
	{
		this.dbManager.getDb(bucketName, true).put(key, IOUtils.toByteArray(data));
	}

	@Override
	public InputStream get(String bucketName, ByteBuffer key)
	{
		final byte[] data = this.dbManager.getDb(bucketName, true).get(key);
		return data != null ? new ByteArrayInputStream(data) : null;
	}

	@Override
	public void delete(String bucketName, ByteBuffer key)
	{
		this.dbManager.getDb(bucketName, false).delete(key);
	}
}