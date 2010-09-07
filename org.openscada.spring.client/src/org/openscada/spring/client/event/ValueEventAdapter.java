/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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

package org.openscada.spring.client.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.openscada.core.Variant;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.util.Assert;

/**
 * <p>
 * A value event adapter which can be set on a property so that the object
 * of a property can attach itself to the event adapter.
 * </p>
 * @author Jens Reimann
 *
 */
public class ValueEventAdapter implements ValueEventListener, InitializingBean
{
    protected List<ValueEventListener> target = new CopyOnWriteArrayList<ValueEventListener> ();

    protected String alias;

    protected TaskExecutor executor = new SyncTaskExecutor ();

    public void valueEvent ( final String topic, final Variant value )
    {
        this.executor.execute ( new Runnable () {

            public void run ()
            {
                notifyValueEvent ( topic, value );
            }
        } );
    }

    private void notifyValueEvent ( final String topic, final Variant value )
    {
        for ( final ValueEventListener listener : this.target )
        {
            // only forward if we have a target
            if ( listener != null )
            {
                final String source = this.alias == null ? topic : this.alias;
                listener.valueEvent ( source, value );
            }
        }
    }

    /**
     * <p>Attach the listener as target listener. Once set the target
     * listener will receive the events.</p>
     * <p>The name explicitly is violating the bean specification (<em>attach</em>
     * instead of <em>set</em>) order to disappear from the spring beans since this
     * should be set from inside the parenting bean in
     * {@link InitializingBean#afterPropertiesSet()}
     * </p>
     * 
     * @param target the target that should receive the events
     */
    public void attachTarget ( final ValueEventListener target )
    {
        this.target.add ( target );
    }

    public void setAlias ( final String alias )
    {
        this.alias = alias;
    }

    public void setExecutor ( final TaskExecutor executor )
    {
        this.executor = executor;
    }

    public void afterPropertiesSet () throws Exception
    {
        Assert.notNull ( this.executor, "'executor' must be set" );
    }
}
