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
        final Variant value = Variant.valueOf ( result.getOutput () );
        attributes.put ( "exec.error", Variant.FALSE );
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
