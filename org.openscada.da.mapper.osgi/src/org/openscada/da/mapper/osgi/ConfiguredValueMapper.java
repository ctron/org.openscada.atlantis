/*
 * This file is part of the openSCADA project
 * Copyright (C) 2011-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * openSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * openSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with openSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.da.mapper.osgi;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.openscada.ca.ConfigurationDataHelper;
import org.openscada.core.Variant;
import org.openscada.da.mapper.ValueMapper;
import org.openscada.da.mapper.ValueMapperListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfiguredValueMapper implements ValueMapper
{

    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock ( false );

    private final Lock readLock = this.readWriteLock.readLock ();

    private final Lock writeLock = this.readWriteLock.writeLock ();

    private final static Logger logger = LoggerFactory.getLogger ( ConfiguredValueMapper.class );

    private final Map<String, String> data = new HashMap<String, String> ();

    private final Set<ValueMapperListener> listeners = new LinkedHashSet<ValueMapperListener> ();

    public void update ( final Map<String, String> parameters )
    {
        ValueMapperListener[] listeners;
        try
        {
            this.writeLock.lock ();
            final ConfigurationDataHelper cfg = new ConfigurationDataHelper ( parameters );
            this.data.clear ();
            this.data.putAll ( cfg.getPrefixed ( "data." ) );

            listeners = this.listeners.toArray ( new ValueMapperListener[this.listeners.size ()] );
        }
        finally
        {
            this.writeLock.unlock ();
        }

        fireStateChange ( listeners );
    }

    private void fireStateChange ( final ValueMapperListener[] listeners )
    {
        for ( final ValueMapperListener listener : listeners )
        {
            try
            {
                listener.stateChanged ();
            }
            catch ( final Exception e )
            {
                logger.warn ( "Failed to handle state change", e );
            }
        }
    }

    @Override
    public Variant mapValue ( final Variant value )
    {
        try
        {
            this.readLock.lock ();
            return Variant.valueOf ( this.data.get ( value.asString ( null ) ) );
        }
        finally
        {
            this.readLock.unlock ();
        }
    }

    public void dispose ()
    {
        this.listeners.clear ();
    }

    @Override
    public void addListener ( final ValueMapperListener listener )
    {
        try
        {
            this.writeLock.lock ();
            this.listeners.add ( listener );
        }
        finally
        {
            this.writeLock.unlock ();
        }
    }

    @Override
    public void removeListener ( final ValueMapperListener listener )
    {
        try
        {
            this.writeLock.lock ();
            this.listeners.remove ( listener );
        }
        finally
        {
            this.writeLock.unlock ();
        }
    }

}
