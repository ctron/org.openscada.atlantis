package org.openscada.da.server.sysinfo.items;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import org.openscada.da.core.common.DataItemInputCommon;
import org.openscada.da.core.common.impl.FolderCommon;
import org.openscada.da.core.data.Variant;
import org.openscada.da.server.sysinfo.Hive;
import org.openscada.da.server.sysinfo.utils.FileUtils;
import org.openscada.utils.collection.MapBuilder;

public class LoadAverageJob implements Runnable {

	private File _file = new File ( "/proc/loadavg" );
	
	private DataItemInputCommon _avg1 = new DataItemInputCommon("loadavg1");
	private DataItemInputCommon _avg5 = new DataItemInputCommon("loadavg5");
	private DataItemInputCommon _avg15 = new DataItemInputCommon("loadavg15");
	
	private Hive _hive = null;
    private FolderCommon _folder = null;
	
	public LoadAverageJob ( Hive hive, FolderCommon folder )
	{
		_hive = hive;
        _folder = folder;
		
		_hive.registerItem ( _avg1 );
        _folder.add ( "1", _avg1, new MapBuilder<String, Variant>()
                .put ( "description", new Variant ( "The 1 minute load avarage" ) )
                .getMap ()
        );
        
		_hive.registerItem ( _avg5 );
        _folder.add ( "5", _avg5, new MapBuilder<String, Variant>()
                .put ( "description", new Variant ( "The 5 minute load avarage" ) )
                .getMap ()
        );
        
		_hive.registerItem ( _avg15 );
        _folder.add ( "15", _avg15, new MapBuilder<String, Variant>()
                .put ( "description", new Variant ( "The 15 minute load avarage" ) )
                .getMap ()
        );
	}
	
	public void run() {
		try
        {
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
		
		_avg1.updateValue ( new Variant ( Double.parseDouble ( tok.nextToken() ) ) );
		_avg5.updateValue ( new Variant ( Double.parseDouble ( tok.nextToken() ) ) );
		_avg15.updateValue ( new Variant ( Double.parseDouble ( tok.nextToken() ) ) );
	}
}
