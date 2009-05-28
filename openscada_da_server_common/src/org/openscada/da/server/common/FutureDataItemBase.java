/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.common;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.openscada.core.InvalidOperationException;
import org.openscada.core.NotConvertableException;
import org.openscada.core.NullValueException;
import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.da.core.DataItemInformation;
import org.openscada.da.core.WriteAttributeResults;

/**
 * A data item base which implements {@link FutureDataItem}
 * <p>
 * One should not rely on the methods {@link #writeValue(Variant)} and {@link #setAttributes(Map)} since they are
 * only for compatibility and will be removed in the next release.
 * </p>
 * @author Jens Reimann
 * @since 0.13.0
 *
 */
public abstract class FutureDataItemBase extends DataItemBase implements FutureDataItem
{

    public FutureDataItemBase ( final DataItemInformation information )
    {
        super ( information );
    }

    /**
     * @deprecated Wrapper method for the old data item
     */
    public WriteAttributeResults setAttributes ( final Map<String, Variant> attributes )
    {
        try
        {
            return startSetAttributes ( attributes ).get ();
        }
        catch ( final Throwable e )
        {
            final WriteAttributeResults result = new WriteAttributeResults ();
            return WriteAttributesHelper.errorUnhandled ( result, attributes );
        }
    }

    /**
     * @deprecated Wrapper method for the old data item
     */
    public void writeValue ( final Variant value ) throws InvalidOperationException, NullValueException, NotConvertableException, OperationException
    {
        try
        {
            startWriteValue ( value ).get ();
        }
        catch ( final InterruptedException e )
        {
            throw new OperationException ( e );
        }
        catch ( final ExecutionException e )
        {
            final Throwable cause = e.getCause ();
            if ( cause instanceof InvalidOperationException )
            {
                throw (InvalidOperationException)cause;
            }
            else if ( cause instanceof NullValueException )
            {
                throw (NullValueException)cause;
            }
            else if ( cause instanceof NotConvertableException )
            {
                throw (NotConvertableException)cause;
            }
            else if ( cause instanceof OperationException )
            {
                throw (OperationException)cause;
            }
            else
            {
                throw new OperationException ( cause );
            }
        }
    }
}
