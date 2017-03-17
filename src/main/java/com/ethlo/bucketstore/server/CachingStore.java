package com.ethlo.bucketstore.server;

import java.io.IOException;

import org.apache.directmemory.cache.CacheService;

import com.ethlo.keyvalue.keys.ByteArrayKey;

/**
 * 
 * @author Morten Haraldsen
 *
 */
public class CachingStore implements StoreOperations
{
	private StoreOperations persistentStore;
	private final CacheService<ByteArrayKey, byte[]> cacheManager;
	private final CacheConfig cacheConfig;
	
	public CachingStore(StoreOperations persistentBackend, CacheService<ByteArrayKey, byte[]> cacheManager, CacheConfig cacheConfig)
	{
		this.persistentStore = persistentBackend;
		this.cacheManager = cacheManager;
		this.cacheConfig = cacheConfig;
	}

	@Override
	public void put(String bucketName, ByteArrayKey key, byte[] data) throws IOException
	{
		final CacheService<ByteArrayKey, byte[]> cache = getCache(bucketName);
		cache.free(key);
		persistentStore.put(bucketName, key, data);
		
		if (data.length <= cacheConfig.getMaxObjectSize())
		{
			cache.putByteArray(key, data);
		}
	}
	
	@Override
	public byte[] get(String bucketName, ByteArrayKey key) throws IOException
	{
		final CacheService<ByteArrayKey, byte[]> cache = getCache(bucketName);
		final byte[] data = cache.retrieveByteArray(key);
		if (data != null)
		{
			return data;
		}
		return persistentStore.get(bucketName, key);
	}

	private CacheService<ByteArrayKey, byte[]> getCache(String bucketName)
	{
		// TODO: Need one cache per bucket
		return this.cacheManager;
	}

	@Override
	public void delete(String bucketName, ByteArrayKey key)
	{
		
	}
}