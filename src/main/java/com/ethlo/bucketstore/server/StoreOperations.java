package com.ethlo.bucketstore.server;

import java.io.IOException;

import com.ethlo.keyvalue.keys.ByteArrayKey;

/**
 * 
 * @author Morten Haraldsen
 */
public interface StoreOperations
{
	void put(String bucketName, ByteArrayKey key, byte[] data) throws IOException;
	
	byte[] get(String bucketName, ByteArrayKey key) throws IOException;

	void delete(String bucketName, ByteArrayKey key);
}
