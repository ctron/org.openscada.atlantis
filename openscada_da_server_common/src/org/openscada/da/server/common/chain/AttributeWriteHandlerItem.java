/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.da.server.common.chain;

import java.util.Map;
import java.util.concurrent.Executor;

import org.openscada.core.Variant;
import org.openscada.da.core.DataItemInformation;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;

public class AttributeWriteHandlerItem extends DataItemInputChained
{

    private volatile AttributeWriteHandler writeHandler;

    public AttributeWriteHandlerItem ( final DataItemInformation di, final AttributeWriteHandler writeHandler, final Executor executor )
    {
        super ( di, executor );
        this.writeHandler = writeHandler;
    }

    public AttributeWriteHandlerItem ( final String itemId, final AttributeWriteHandler writeHandler, final Executor executor )
    {
        super ( itemId, executor );
        this.writeHandler = writeHandler;
    }

    /**
     * Change the write handler
     * <p>
     * The write handler will not be called for the last written value
     * only for the next one.
     * 
     * @param writeHandler the new write handler
     */
    public void setWriteHandler ( final AttributeWriteHandler writeHandler )
    {
        this.writeHandler = writeHandler;
    }

    @Override
    protected WriteAttributeResults handleUnhandledAttributes ( final WriteAttributeResults writeAttributeResults, final Map<String, Variant> attributes )
    {
        final AttributeWriteHandler handler = this.writeHandler;

        WriteAttributeResults result = null;
        try
        {
            result = handler.handleWrite ( attributes );
            // remove handled attributes
            for ( final String attr : result.keySet () )
            {
                attributes.remove ( attr );
            }
            return super.handleUnhandledAttributes ( result, attributes );
        }
        catch ( final Exception e )
        {
            for ( final String attr : attributes.keySet () )
            {
                result.put ( attr, new WriteAttributeResult ( e ) );
            }
            return result;
        }
    }

}
