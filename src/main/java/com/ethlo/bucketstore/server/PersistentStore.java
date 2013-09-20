package com.ethlo.bucketstore.server;

import java.io.IOException;
import java.nio.ByteBuffer;

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
	public void put(String bucketName, ByteBuffer key, byte[] data) throws IOException
	{
		this.dbManager.getDb(bucketName, true).put(key, data);
	}

	@Override
	public byte[] get(String bucketName, ByteBuffer key)
	{
		return this.dbManager.getDb(bucketName, true).get(key);
	}

	@Override
	public void delete(String bucketName, ByteBuffer key)
	{
		this.dbManager.getDb(bucketName, false).delete(key);
	}
}