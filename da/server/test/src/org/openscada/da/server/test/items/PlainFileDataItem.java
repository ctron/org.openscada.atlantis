/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.da.server.test.items;


import java.io.File;
import java.io.IOException;

import org.openscada.da.core.Variant;
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
		try
		{
			read ();
            getAttributeManager ().update ( "error-message", new Variant () );
		}
		catch ( Exception e )
		{
			// handle error
            getAttributeManager ().update ( "error-message", new Variant ( e.getMessage () ) );
		}
		
	}
	
	private void read () throws IOException
	{
		String []data = FileUtils.readFile(_file);
		updateValue(new Variant(data[0]));
	}
	
}
