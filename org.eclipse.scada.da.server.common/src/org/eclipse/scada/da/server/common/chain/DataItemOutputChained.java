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

package org.eclipse.scada.da.server.common.chain;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import org.eclipse.scada.core.InvalidOperationException;
import org.eclipse.scada.core.NotConvertableException;
import org.eclipse.scada.core.NullValueException;
import org.eclipse.scada.core.Variant;
import org.eclipse.scada.da.core.DataItemInformation;
import org.eclipse.scada.da.data.IODirection;
import org.eclipse.scada.da.server.common.DataItemInformationBase;
import org.eclipse.scada.da.server.common.SuspendableDataItem;
import org.eclipse.scada.utils.concurrent.NotifyFuture;

public abstract class DataItemOutputChained extends DataItemBaseChained implements SuspendableDataItem
{

    public DataItemOutputChained ( final DataItemInformation dataItemInformation, final Executor executor )
    {
        super ( dataItemInformation, executor );
    }

    public DataItemOutputChained ( final String id, final Executor executor )
    {
        this ( new DataItemInformationBase ( id, EnumSet.of ( IODirection.OUTPUT ) ), executor );
    }

    public NotifyFuture<Variant> readValue () throws InvalidOperationException
    {
        throw new InvalidOperationException ();
    }

    public synchronized void writeValue ( final Variant value ) throws InvalidOperationException, NullValueException, NotConvertableException
    {
        final Variant newValue = process ( value );

        if ( newValue != null )
        {
            writeCalculatedValue ( newValue );
        }
    }

    @Override
    protected void process ()
    {
        process ( null );
    }

    protected Variant process ( Variant value )
    {
        final Map<String, Variant> primaryAttributes = new HashMap<String, Variant> ( this.primaryAttributes );

        for ( final ChainProcessEntry entry : getChainCopy () )
        {
            if ( entry.getWhen ().contains ( IODirection.OUTPUT ) )
            {
                final Variant newValue = entry.getWhat ().process ( value, primaryAttributes );
                if ( newValue != null )
                {
                    value = newValue;
                }
            }
        }

        this.secondaryAttributes.set ( null, primaryAttributes );

        return value;
    }

    public void suspend ()
    {
    }

    public void wakeup ()
    {
        if ( this.secondaryAttributes.get ().size () > 0 )
        {
            notifyData ( null, this.secondaryAttributes.get () );
        }
    }

    protected abstract void writeCalculatedValue ( Variant value ) throws NullValueException, NotConvertableException;

}
