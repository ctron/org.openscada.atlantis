/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
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
