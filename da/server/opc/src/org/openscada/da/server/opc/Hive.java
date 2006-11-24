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

import org.openscada.core.Variant;
import org.openscada.da.core.browser.common.FolderCommon;
import org.openscada.da.core.browser.common.query.IDNameProvider;
import org.openscada.da.core.browser.common.query.InvisibleStorage;
import org.openscada.da.core.browser.common.query.ItemDescriptor;
import org.openscada.da.core.browser.common.query.ItemStorage;
import org.openscada.da.core.browser.common.query.Matcher;
import org.openscada.da.core.browser.common.query.QueryFolder;
import org.openscada.da.core.common.impl.HiveCommon;
import org.openscada.utils.timing.Scheduler;

public class Hive extends HiveCommon
{
    private Scheduler _scheduler = null;

    private InvisibleStorage _storage = new InvisibleStorage ();

    public Hive ()
    {
        super ();

        _scheduler = new Scheduler ( true );

        // create root folder
        FolderCommon rootFolder = new FolderCommon ();
        setRootFolder ( rootFolder );
    }

    public Scheduler getScheduler ()
    {
        return _scheduler;
    }

    public ItemStorage getStorage ()
    {
        return _storage;
    }
}
