package com.ethlo.bucketstore.server;

/**
 * 
 * @author Morten Haraldsen
 *
 */
public class CacheConfig
{
	private long maxObjectSize;

	public long getMaxObjectSize()
	{
		return maxObjectSize;
	}

	public void setMaxObjectSize(long maxObjectSize)
	{
		this.maxObjectSize = maxObjectSize;
	}
}
