package org.openscada.da.server.test.items;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.openscada.da.core.data.Variant;
import org.openscada.da.server.test.utils.FileUtils;
import org.openscada.utils.timing.Scheduler;

public class PlainFileDataItem extends ScheduledDataItem {

	private File _file;
	
	public PlainFileDataItem(String name, File file, Scheduler scheduler, int period) {
		super(name, scheduler, period);
		_file = file;
	}

	public void run()
    {
		Map<String,Variant> attributes = new HashMap<String,Variant>();
		try
		{
			read ();
			attributes.put("error-message",new Variant());
		}
		catch ( Exception e )
		{
			// handle error
			attributes.put("error-message",new Variant(e.getMessage()));
		}
		updateAttributes(attributes);
	}
	
	private void read () throws IOException
	{
		String []data = FileUtils.readFile(_file);
		updateValue(new Variant(data[0]));
	}
	
}
