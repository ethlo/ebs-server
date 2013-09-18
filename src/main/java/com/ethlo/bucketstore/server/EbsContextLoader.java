package com.ethlo.bucketstore.server;

import java.io.File;
import java.nio.ByteBuffer;

import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.ethlo.keyvalue.KeyValueDb;
import com.ethlo.keyvalue.KeyValueDbManager;

/**
 * 
 * @author mha
 */
public class EbsContextLoader
{
	private KeyValueDbManager<ByteBuffer, byte[], KeyValueDb<ByteBuffer,byte[]>> dbManager;
	
	@SuppressWarnings("unchecked")
	public EbsContextLoader(final String configLocation)
	{
		try (final FileSystemXmlApplicationContext ctx = new FileSystemXmlApplicationContext(new String[]{new File(configLocation, "ebs.xml").toURI().toString()}, false))
		{
			ctx.refresh();
			this.dbManager = ctx.getBean(KeyValueDbManager.class);
		}
	}
	
	public KeyValueDbManager<ByteBuffer, byte[], KeyValueDb<ByteBuffer, byte[]>> getDbManager()
	{
		return this.dbManager;
	}
}
