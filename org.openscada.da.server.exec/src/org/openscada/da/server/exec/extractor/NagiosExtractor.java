/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.exec.extractor;

import org.openscada.core.Variant;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.item.factory.FolderItemFactory;
import org.openscada.da.server.exec.Hive;

/**
 * Extract information based on the nagios scheme using the return code
 * @author Jens Reimann
 *
 */
public class NagiosExtractor extends AbstractReturnCodeExtractor
{
    private DataItemInputChained errorItem;

    private DataItemInputChained warningItem;

    public NagiosExtractor ( final String id )
    {
        super ( id );
    }

    @Override
    protected void handleReturnCode ( final int rc )
    {
        if ( rc < 0 )
        {
            throw new RuntimeException ( String.format ( "Command excution failed: rc = %s", rc ) );
        }

        if ( rc == 0 )
        {
            this.errorItem.updateData ( new Variant ( rc >= 2 ), null, null );
            this.warningItem.updateData ( new Variant ( rc >= 1 ), null, null );
        }
    }

    @Override
    public void register ( final Hive hive, final FolderItemFactory folderItemFactory )
    {
        super.register ( hive, folderItemFactory );
        this.errorItem = createInput ( "error" );
        this.warningItem = createInput ( "warning" );
    }

}
