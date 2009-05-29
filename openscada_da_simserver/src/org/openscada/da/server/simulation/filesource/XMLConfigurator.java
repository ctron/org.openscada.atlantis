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
