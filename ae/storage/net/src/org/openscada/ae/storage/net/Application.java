/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.ae.storage.net;

import org.apache.log4j.Logger;

/**
 * Application to export an AE storage using the OpenSCADA NET protocol
 * @author jens
 *
 */
public class Application
{
	private static Logger _log = Logger.getLogger ( Application.class );
	
	public static void main ( String[] args )
	{
		try
        {
            // check if we have a class name
		    if ( args.length != 1 )
		    {
		        System.err.println ( "syntax: Application <hiveClassName>" );
                return;
		    }
            
            // create exporter
            Exporter exporter = new Exporter ( args[0] );
            
            // run the lizzard
            _log.info ( "Running exporter (storage class: " + exporter.getStorageClass ().getCanonicalName () + ")..." );
			exporter.run ();
            _log.warn ( "Exporter returned!" );
		}
		catch ( Exception e )
		{
            // ops
			_log.fatal ( "Error in OpenSCADA DA[NET] Server", e );
		}
	}
}
