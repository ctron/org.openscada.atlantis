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
            this.errorItem.updateData ( Variant.valueOf ( rc >= 2 ), null, null );
            this.warningItem.updateData ( Variant.valueOf ( rc >= 1 ), null, null );
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
