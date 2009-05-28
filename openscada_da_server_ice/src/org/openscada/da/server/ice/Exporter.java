/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.ice;

import org.openscada.core.ConnectionInformation;
import org.openscada.da.core.server.Hive;
import org.openscada.da.server.common.impl.ExporterBase;
import org.openscada.da.server.ice.impl.HiveImpl;

import Ice.Communicator;

public class Exporter extends ExporterBase
{
    private Communicator communicator = null;

    private Ice.ObjectAdapter adapter = null;

    private boolean running = false;

    @Deprecated
    public Exporter ( final String hiveClassName, final Communicator communicator, final ConnectionInformation connectionInformation ) throws Exception
    {
        super ( hiveClassName, connectionInformation );
        this.communicator = communicator;
    }

    public Exporter ( final Hive hive, final Communicator communicator, final ConnectionInformation connectionInformation ) throws Exception
    {
        super ( hive, connectionInformation );
        this.communicator = communicator;
    }

    @Deprecated
    public Exporter ( final Class<Hive> hiveClass, final Communicator communicator, final ConnectionInformation connectionInformation ) throws Exception
    {
        super ( hiveClass, connectionInformation );
        this.communicator = communicator;
    }

    public synchronized void start ()
    {
        if ( this.running )
        {
            return;
        }

        this.running = true;

        final String endpoints = getEndpoints ();
        if ( endpoints == null )
        {
            this.adapter = this.communicator.createObjectAdapter ( "Hive" );
        }
        else
        {
            this.adapter = this.communicator.createObjectAdapterWithEndpoints ( "Hive", endpoints );
        }

        this.adapter.add ( new HiveImpl ( this.hive, this.adapter ), this.communicator.stringToIdentity ( "hive" ) );
        this.adapter.activate ();
    }

    private String getEndpoints ()
    {
        final String name = this.connectionInformation.getTarget ();
        return this.connectionInformation.getProperties ().get ( name );
    }

    public synchronized void stop ()
    {
        if ( this.running )
        {
            this.adapter.deactivate ();
            this.communicator.shutdown ();

            this.adapter = null;
            this.communicator = null;

            this.running = false;
        }
    }

}
