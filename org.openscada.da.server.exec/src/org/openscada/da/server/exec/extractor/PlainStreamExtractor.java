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

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.item.factory.FolderItemFactory;
import org.openscada.da.server.exec.Hive;
import org.openscada.da.server.exec.command.ExecutionResult;

/**
 * Extractor which takes the stream string value as value
 * @author Jens Reimann
 *
 */
public class PlainStreamExtractor extends AbstractBaseExtractor
{
    private DataItemInputChained valueItem;

    public PlainStreamExtractor ( final String id )
    {
        super ( id );
    }

    @Override
    protected void doProcess ( final ExecutionResult result )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
        final Variant value = new Variant ( result.getOutput () );
        attributes.put ( "exec.error", new Variant ( false ) );
        attributes.put ( "exec.error.message", null );
        fillNoError ( attributes );

        this.valueItem.updateData ( value, attributes, AttributeMode.UPDATE );
    }

    @Override
    public void register ( final Hive hive, final FolderItemFactory folderItemFactory )
    {
        super.register ( hive, folderItemFactory );
        this.valueItem = createInput ( "value" );
    }
}
