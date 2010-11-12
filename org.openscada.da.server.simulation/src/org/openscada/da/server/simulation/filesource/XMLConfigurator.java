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

package org.openscada.da.server.simulation.filesource;

import java.io.File;

import org.openscada.da.server.common.item.factory.FolderItemFactory;
import org.openscada.da.simulation.configuration.RootDocument;

public class XMLConfigurator
{
    private final RootDocument document;

    public XMLConfigurator ( final RootDocument document )
    {
        this.document = document;
    }

    public void configure ( final Hive hive ) throws Exception
    {
        String name = null;
        String filename = null;
        String filenameJs = null;
        if ( this.document.getRoot ().getExcelConfig () != null )
        {
            name = this.document.getRoot ().getExcelConfig ().getName ();
            filename = this.document.getRoot ().getExcelConfig ().getInputFile ();
            filenameJs = this.document.getRoot ().getExcelConfig ().getJsFile ();
            final File fileJs = filenameJs == null ? null : new File ( filenameJs );
            hive.setFactory ( new FolderItemFactory ( hive, hive.getRootFolder (), name, name ) );
            final HiveBuilder hb = HiveBuilder.create ( name );
            new ExcelFile ( new File ( filename ), fileJs, hb ).configureHive ();
            hb.configureHive ( hive );
        }
        else if ( this.document.getRoot ().getOpenOfficeConfig () != null )
        {
            name = this.document.getRoot ().getOpenOfficeConfig ().getName ();
            filename = this.document.getRoot ().getOpenOfficeConfig ().getInputFile ();
            filenameJs = this.document.getRoot ().getOpenOfficeConfig ().getJsFile ();
            final File fileJs = filenameJs == null ? null : new File ( filenameJs );
            hive.setFactory ( new FolderItemFactory ( hive, hive.getRootFolder (), name, name ) );
            final HiveBuilder hb = HiveBuilder.create ( name );
            new OpenOfficeFile ( new File ( filename ), fileJs, hb ).configureHive ();
            hb.configureHive ( hive );
        }
    }
}
