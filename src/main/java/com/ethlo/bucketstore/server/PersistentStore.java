package com.ethlo.bucketstore.server;

import java.io.IOException;

import com.ethlo.keyvalue.DataCompressor;
import com.ethlo.keyvalue.HexKeyEncoder;
import com.ethlo.keyvalue.KeyValueDb;
import com.ethlo.keyvalue.KeyValueDbManager;
import com.ethlo.keyvalue.keys.ByteArrayKey;

/**
 * 
 * @author Morten Haraldsen
 *
 */
public class PersistentStore implements StoreOperations
{
	private final KeyValueDbManager<ByteArrayKey, byte[], KeyValueDb<ByteArrayKey, byte[]>> dbManager;
	private final HexKeyEncoder keyEncoder = new HexKeyEncoder();
	private DataCompressor dataCompressor;
	
	public PersistentStore(KeyValueDbManager<ByteArrayKey, byte[], KeyValueDb<ByteArrayKey, byte[]>> dbManager)
	{
		this.dbManager = dbManager;
	}

	@Override
	public void put(String bucketName, ByteArrayKey key, byte[] data) throws IOException
	{
		this.dbManager.getDb(bucketName, true, keyEncoder, dataCompressor).put(key, data);
	}

	@Override
	public byte[] get(String bucketName, ByteArrayKey key)
	{
		return this.dbManager.getDb(bucketName, true, keyEncoder, dataCompressor).get(key);
	}

	@Override
	public void delete(String bucketName, ByteArrayKey key)
	{
		this.dbManager.getDb(bucketName, false, keyEncoder, dataCompressor).delete(key);
	}
}