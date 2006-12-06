/*
 * This file is part of the OpenSCADA project Copyright (C) 2006 inavare GmbH
 * (http://inavare.com) This program is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version. This program is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with this program; if not, write
 * to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA 02110-1301 USA.
 */

package org.openscada.da.server.opc;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.jinterop.dcom.core.JISession;
import org.openscada.core.Variant;
import org.openscada.da.core.browser.common.FolderCommon;
import org.openscada.da.core.browser.common.query.IDNameProvider;
import org.openscada.da.core.browser.common.query.InvisibleStorage;
import org.openscada.da.core.browser.common.query.ItemDescriptor;
import org.openscada.da.core.browser.common.query.ItemStorage;
import org.openscada.da.core.browser.common.query.Matcher;
import org.openscada.da.core.browser.common.query.QueryFolder;
import org.openscada.da.core.common.impl.HiveCommon;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.utils.timing.Scheduler;

public class Hive extends HiveCommon
{
    private Scheduler _scheduler = null;

    private InvisibleStorage _storage = new InvisibleStorage ();
    
    private OPCConnection _connection = null;
    
    private FolderCommon _rootFolderCommon = null;

    public Hive ()
    {
        super ();

        _scheduler = new Scheduler ( true );

        // create root folder
        _rootFolderCommon = new FolderCommon ();
        setRootFolder ( _rootFolderCommon );
        
        // Add a storage based folder
        QueryFolder queryFolder = new QueryFolder ( new Matcher () {

            public boolean matches ( ItemDescriptor desc )
            {
                return true;
            }
        }, new IDNameProvider () );
        _rootFolderCommon.add ( "flat", queryFolder, new HashMap<String, Variant> () );
        _storage.addChild ( queryFolder );
        
        // test
        ConnectionInformation ci = new ConnectionInformation ();
        ci.setUser ( "jens" );
        ci.setPassword ( "test12" );
        ci.setDomain ( "localhost" );
        ci.setHost ( "172.16.15.128" );
        ci.setClsid ( "F8582CF2-88FB-11D0-B850-00C0F0104305" );
        _connection = new OPCConnection ( this, ci );
        _connection.start ();
        _connection.connect ();
    }

    public Scheduler getScheduler ()
    {
        return _scheduler;
    }

    public ItemStorage getStorage ()
    {
        return _storage;
    }
    
    public FolderCommon getRootFolderCommon ()
    {
        return _rootFolderCommon;
    }
}
