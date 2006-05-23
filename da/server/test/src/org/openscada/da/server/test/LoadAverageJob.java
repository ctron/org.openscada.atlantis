package org.openscada.da.server.test;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import org.openscada.da.core.common.DataItemInputCommon;
import org.openscada.da.core.data.Variant;

public class LoadAverageJob implements Runnable {

	private File _file = new File ("/proc/loadavg");
	
	private DataItemInputCommon _avg1 = new DataItemInputCommon("loadavg1");
	private DataItemInputCommon _avg5 = new DataItemInputCommon("loadavg5");
	private DataItemInputCommon _avg15 = new DataItemInputCommon("loadavg15");
	
	private Hive _hive;
	
	public LoadAverageJob ( Hive hive )
	{
		_hive = hive;
		
		_hive.registerItem(_avg1);
		_hive.registerItem(_avg5);
		_hive.registerItem(_avg15);
	}
	
	public void run() {
		try {
			read ();
		}
		catch ( Exception e )
		{
			// handle error
		}
	}
	
	private void read () throws IOException
	{
		String [] data = FileUtils.readFile(_file);
		
		StringTokenizer tok = new StringTokenizer ( data[0] );
		
		_avg1.updateValue ( new Variant ( Double.parseDouble(tok.nextToken())) );
		_avg5.updateValue ( new Variant ( Double.parseDouble(tok.nextToken())) );
		_avg15.updateValue ( new Variant ( Double.parseDouble(tok.nextToken())) );
	}
}
