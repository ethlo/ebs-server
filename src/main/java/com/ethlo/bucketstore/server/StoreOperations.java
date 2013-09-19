package com.ethlo.bucketstore.server;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * 
 * @author Morten Haraldsen
 */
public interface StoreOperations
{
	void put(String bucketName, ByteBuffer key, InputStream data) throws IOException;
	
	InputStream get(String bucketName, ByteBuffer key) throws IOException;

	void delete(String bucketName, ByteBuffer key);
}
