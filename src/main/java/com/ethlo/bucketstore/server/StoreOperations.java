package com.ethlo.bucketstore.server;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * 
 * @author Morten Haraldsen
 */
public interface StoreOperations
{
	void put(String bucketName, ByteBuffer key, byte[] data) throws IOException;
	
	byte[] get(String bucketName, ByteBuffer key) throws IOException;

	void delete(String bucketName, ByteBuffer key);
}
