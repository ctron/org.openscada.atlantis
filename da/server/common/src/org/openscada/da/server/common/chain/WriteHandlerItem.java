/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

import org.openscada.core.InvalidOperationException;
import org.openscada.core.NotConvertableException;
import org.openscada.core.Variant;
import org.openscada.da.core.DataItemInformation;

public class WriteHandlerItem extends DataItemInputOutputChained
{

    private WriteHandler writeHandler;

    public WriteHandlerItem ( final DataItemInformation di, final WriteHandler writeHandler )
    {
        super ( di );
        this.writeHandler = writeHandler;
    }

    public WriteHandlerItem ( final String itemId, final WriteHandler writeHandler )
    {
        super ( itemId );
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
    public void setWriteHandler ( final WriteHandler writeHandler )
    {
        this.writeHandler = writeHandler;
    }

    @Override
    protected void writeCalculatedValue ( final Variant value ) throws NotConvertableException, InvalidOperationException
    {
        final WriteHandler writeHandler = this.writeHandler;

        // if we don't have a write handler this is not allowed
        if ( writeHandler == null )
        {
            throw new InvalidOperationException ();
        }

        try
        {
            writeHandler.handleWrite ( value );
        }
        catch ( final NotConvertableException e )
        {
            throw e;
        }
        catch ( final InvalidOperationException e )
        {
            throw e;
        }
        catch ( final Throwable e )
        {
            // FIXME: should be a separate "write failed" exception
            throw new InvalidOperationException ();
        }
    }

}
