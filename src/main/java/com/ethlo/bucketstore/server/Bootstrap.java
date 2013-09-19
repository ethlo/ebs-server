package com.ethlo.bucketstore.server;

import java.io.File;

import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * 
 * @author Morten Haraldsen
 *
 */
public class Bootstrap
{
	public static void main(String[] args) throws Exception
	{
		// Install logger
		SLF4JBridgeHandler.install();

		if (args.length == 1)
		{
			final String configLocation = args[0];
			final File cfgFile = new File(configLocation, "ebs.xml");
			if (! cfgFile.exists())
			{
				System.err.println("Cannot find config file ebs.xml in config directory " + configLocation);
				System.exit(2);
			}
			
			new FileSystemXmlApplicationContext(new String[]{cfgFile.toURI().toString()}, true);
		}
		else
		{
			System.err.println("Usage: ebs-server <config-directory>");
			System.exit(1);
		}
	}
}
