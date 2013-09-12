/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.hd.server.storage.hds;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.eclipse.scada.hds.DataFilePool;
import org.eclipse.scada.hds.DataStoreAccesor;

public class StorageHelper
{

    public static void create ( final String id, final File file, final StorageConfiguration configuration, final DataFilePool pool ) throws Exception
    {
        file.mkdir ();

        final Properties p = new Properties ();
        p.put ( "id", id );
        p.storeToXML ( new FileOutputStream ( new File ( file, "settings.xml" ) ), "openSCADA HD HDS Storage Settings" );

        DataStoreAccesor.create ( new File ( file, "native" ), configuration.getTimeSlice (), TimeUnit.MILLISECONDS, configuration.getCount (), pool );
    }

}
