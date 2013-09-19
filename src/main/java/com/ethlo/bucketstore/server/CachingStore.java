package com.ethlo.bucketstore.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.apache.commons.io.IOUtils;
import org.apache.directmemory.cache.CacheService;

/**
 * 
 * @author Morten Haraldsen
 *
 */
public class CachingStore implements StoreOperations
{
	private StoreOperations persistentStore;
	private final CacheService<ByteBuffer, byte[]> cacheManager;
	private final CacheConfig cacheConfig;
	
	public CachingStore(StoreOperations persistentBackend, CacheService<ByteBuffer, byte[]> cacheManager, CacheConfig cacheConfig)
	{
		this.persistentStore = persistentBackend;
		this.cacheManager = cacheManager;
		this.cacheConfig = cacheConfig;
	}

	@Override
	public void put(String bucketName, ByteBuffer key, InputStream data) throws IOException
	{
		getCache(bucketName).free(key);
		persistentStore.put(bucketName, key, data);
		getCache(bucketName).putByteArray(key, IOUtils.toByteArray(data));
	}
	
	@Override
	public InputStream get(String bucketName, ByteBuffer key) throws IOException
	{
		final CacheService<ByteBuffer, byte[]> cache = getCache(bucketName);
		final byte[] data = cache.retrieveByteArray(key);
		if (data != null)
		{
			return new ByteArrayInputStream(data);
		}
		return persistentStore.get(bucketName, key);
	}

	private CacheService<ByteBuffer, byte[]> getCache(String bucketName)
	{
		// TODO: Need one cache per bucket
		return this.cacheManager;
	}

	@Override
	public void delete(String bucketName, ByteBuffer key)
	{
		
	}
}