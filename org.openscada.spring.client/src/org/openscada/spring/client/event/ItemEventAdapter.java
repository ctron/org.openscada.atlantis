/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

import org.openscada.da.client.DataItemValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.util.Assert;

public class ItemEventAdapter implements ItemEventListener, InitializingBean
{

    private final static Logger logger = LoggerFactory.getLogger ( ItemEventAdapter.class );

    protected List<ItemEventListener> target = new CopyOnWriteArrayList<ItemEventListener> ();

    protected String alias;

    protected boolean logAsInfo = false;

    protected TaskExecutor executor = new SyncTaskExecutor ();

    @Override
    public String toString ()
    {
        return "ItemEventAdapter#" + this.alias;
    }

    @Override
    public void itemEvent ( final String topic, final DataItemValue value )
    {
        if ( this.logAsInfo )
        {
            logger.info ( String.format ( "Value change for topic: '%s' -> %s", topic, value.toString () ) );
        }
        else
        {
            logger.debug ( String.format ( "Value change for topic: '%s' -> %s", topic, value.toString () ) );
        }

        this.executor.execute ( new Runnable () {

            @Override
            public void run ()
            {
                notifyChangeEvent ( topic, value );
            }

        } );
    }

    /**
     * Perform the notification of the item event
     * @param topic the topic that was notified
     * @param value the value
     */
    private void notifyChangeEvent ( final String topic, final DataItemValue value )
    {
        for ( final ItemEventListener listener : this.target )
        {
            // only forward if we have a target
            if ( listener != null )
            {
                final String source = this.alias == null ? topic : this.alias;
                listener.itemEvent ( source, value );
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
    public void attachTarget ( final ItemEventListener target )
    {
        this.target.add ( target );
    }

    /**
     * Remove the target as an event listener
     * @param target the target to remove
     */
    public void detachTarget ( final ItemEventListener target )
    {
        this.target.remove ( target );
    }

    public void setExecutor ( final TaskExecutor executor )
    {
        this.executor = executor == null ? new SyncTaskExecutor () : executor;
    }

    @Override
    public void afterPropertiesSet () throws Exception
    {
        Assert.notNull ( this.executor, "'executor' must be set" );
    }

}
