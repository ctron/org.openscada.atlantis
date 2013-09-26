/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

package org.openscada.da.server.common.chain;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.scada.core.Variant;
import org.eclipse.scada.core.server.OperationParameters;
import org.eclipse.scada.da.core.DataItemInformation;
import org.eclipse.scada.da.core.WriteAttributeResults;
import org.eclipse.scada.da.core.WriteResult;
import org.eclipse.scada.da.data.IODirection;
import org.eclipse.scada.utils.concurrent.DirectExecutor;
import org.eclipse.scada.utils.concurrent.InstantFuture;
import org.eclipse.scada.utils.concurrent.NotifyFuture;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.DataItemInformationBase;

public class MemoryItemChained extends DataItemInputOutputChained
{
    public MemoryItemChained ( final DataItemInformation di )
    {
        super ( di, DirectExecutor.INSTANCE );
    }

    public MemoryItemChained ( final String id )
    {
        this ( new DataItemInformationBase ( id, EnumSet.of ( IODirection.INPUT, IODirection.OUTPUT ) ) );
    }

    @Override
    protected WriteAttributeResults handleUnhandledAttributes ( final WriteAttributeResults writeAttributeResults, final Map<String, Variant> attributes )
    {
        final Map<String, Variant> addAttributes = new HashMap<String, Variant> ();

        testFlag ( attributes, addAttributes, "test.error", "test.error" );
        testFlag ( attributes, addAttributes, "test.alarm", "test.alarm" );
        testFlag ( attributes, addAttributes, "test.warning", "warning" );
        testFlag ( attributes, addAttributes, "test.manual", "manual" );

        updateData ( null, addAttributes, AttributeMode.UPDATE );

        return super.handleUnhandledAttributes ( writeAttributeResults, attributes );
    }

    private void testFlag ( final Map<String, Variant> attributes, final Map<String, Variant> addAttributes, final String writeAttribute, final String readAttribute )
    {
        final Variant attributeRequest = attributes.remove ( writeAttribute );
        if ( attributeRequest != null )
        {
            addAttributes.put ( readAttribute, attributeRequest );
        }
    }

    @Override
    protected NotifyFuture<WriteResult> startWriteCalculatedValue ( final Variant value, final OperationParameters operationParameters )
    {
        updateData ( value, null, null );
        return new InstantFuture<WriteResult> ( WriteResult.OK );
    }
}
