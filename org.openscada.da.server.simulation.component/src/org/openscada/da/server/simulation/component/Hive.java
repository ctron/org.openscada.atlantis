/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.simulation.component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.openscada.core.Variant;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.browser.common.query.AttributeNameProvider;
import org.openscada.da.server.browser.common.query.GroupFolder;
import org.openscada.da.server.browser.common.query.IDNameProvider;
import org.openscada.da.server.browser.common.query.InvisibleStorage;
import org.openscada.da.server.browser.common.query.ItemDescriptor;
import org.openscada.da.server.browser.common.query.ItemStorage;
import org.openscada.da.server.browser.common.query.Matcher;
import org.openscada.da.server.browser.common.query.QueryFolder;
import org.openscada.da.server.browser.common.query.SplitGroupProvider;
import org.openscada.da.server.common.impl.HiveCommon;
import org.openscada.da.server.simulation.component.modules.BaseModule;
import org.openscada.da.server.simulation.component.modules.SimpleMOV;
import org.openscada.da.server.simulation.component.modules.SimpleScale;

public class Hive extends HiveCommon
{
    private final ScheduledExecutorService executor;

    private final List<BaseModule> _modules = new LinkedList<BaseModule> ();

    private final InvisibleStorage _storage = new InvisibleStorage ();

    public Hive ()
    {
        super ();

        this.executor = Executors.newSingleThreadScheduledExecutor ();

        // create root folder
        final FolderCommon rootFolder = new FolderCommon ();
        setRootFolder ( rootFolder );

        addModule ( new SimpleMOV ( this, "1000" ) );
        addModule ( new SimpleScale ( this, "1001" ) );

        final QueryFolder queryFolder = new QueryFolder ( new Matcher () {

            public boolean matches ( final ItemDescriptor desc )
            {
                return true;
            }
        }, new IDNameProvider () );
        rootFolder.add ( "all", queryFolder, new HashMap<String, Variant> () );
        this._storage.addChild ( queryFolder );

        final GroupFolder groupFolder = new GroupFolder ( new SplitGroupProvider ( new AttributeNameProvider ( "tag" ), "\\." ), new IDNameProvider () );
        rootFolder.add ( "components", groupFolder, new HashMap<String, Variant> () );
        this._storage.addChild ( groupFolder );
    }

    public ScheduledExecutorService getExecutor ()
    {
        return this.executor;
    }

    @Override
    public void stop () throws Exception
    {
        this.executor.shutdown ();
        super.stop ();
    }

    public ItemStorage getStorage ()
    {
        return this._storage;
    }

    public void addModule ( final BaseModule module )
    {
        this._modules.add ( module );
    }
}
