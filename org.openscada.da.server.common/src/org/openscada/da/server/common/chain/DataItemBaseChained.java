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

package org.openscada.da.server.common.chain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;

import org.openscada.core.Variant;
import org.openscada.core.server.common.session.UserSession;
import org.openscada.da.core.DataItemInformation;
import org.openscada.da.core.IODirection;
import org.openscada.da.core.WriteAttributeResult;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.da.server.common.AttributeManager;
import org.openscada.da.server.common.DataItemBase;
import org.openscada.da.server.common.WriteAttributesHelper;
import org.openscada.utils.concurrent.FutureTask;
import org.openscada.utils.concurrent.NotifyFuture;

public abstract class DataItemBaseChained extends DataItemBase
{
    protected Map<String, Variant> primaryAttributes = null;

    protected AttributeManager secondaryAttributes = null;

    /**
     * The chain if items used for calculation
     */
    protected volatile Set<ChainProcessEntry> chain = new CopyOnWriteArraySet<ChainProcessEntry> ();

    protected final Executor executor;

    public DataItemBaseChained ( final DataItemInformation dataItemInformation, final Executor executor )
    {
        super ( dataItemInformation );
        this.executor = executor;

        this.primaryAttributes = new HashMap<String, Variant> ();
        this.secondaryAttributes = new AttributeManager ( this );
    }

    public Map<String, Variant> getAttributes ()
    {
        return this.secondaryAttributes.get ();
    }

    /**
     * This method sets the attributes.
     * <p>
     * It is intended to be overridden by subclasses that wish to handle attribute
     * writes differently. The method needs to remove attributes from the parameter map
     * that were handled and return a result for all attributes that were requested.
     * 
     * @param attributes Attributes to set
     * @return status for the attribute write request  
     */
    public NotifyFuture<WriteAttributeResults> startSetAttributes ( final UserSession session, final Map<String, Variant> attributes )
    {
        final FutureTask<WriteAttributeResults> task = new FutureTask<WriteAttributeResults> ( new Callable<WriteAttributeResults> () {

            public WriteAttributeResults call () throws Exception
            {
                return DataItemBaseChained.this.processSetAttributes ( attributes );
            }
        } );

        this.executor.execute ( task );

        return task;
    }

    protected WriteAttributeResults processSetAttributes ( final Map<String, Variant> attributes )
    {
        final WriteAttributeResults writeAttributeResults = new WriteAttributeResults ();

        for ( final ChainProcessEntry chainEntry : this.chain )
        {
            final ChainItem chainItem = chainEntry.getWhat ();

            final WriteAttributeResults partialResult = chainItem.setAttributes ( attributes );
            if ( partialResult != null )
            {
                for ( final Map.Entry<String, WriteAttributeResult> entry : partialResult.entrySet () )
                {
                    if ( entry.getValue ().isError () )
                    {
                        attributes.remove ( entry.getKey () );
                    }
                    writeAttributeResults.put ( entry.getKey (), entry.getValue () );
                }
            }
        }

        process ();

        return handleUnhandledAttributes ( writeAttributeResults, attributes );
    }

    /**
     * Handle all unhandled set attribute requests
     * @param writeAttributeResults the result set
     * @param attributes the attributes to set
     * @return the final result
     */
    protected WriteAttributeResults handleUnhandledAttributes ( final WriteAttributeResults writeAttributeResults, final Map<String, Variant> attributes )
    {
        return WriteAttributesHelper.errorUnhandled ( writeAttributeResults, attributes );
    }

    protected abstract void process ();

    /**
     * Replace the current chain with the new one
     * @param chain the new chain
     */
    public void setChain ( final Collection<ChainProcessEntry> chain )
    {
        for ( final ChainProcessEntry entry : this.chain )
        {
            entry.getWhat ().dataItemChanged ( this );
        }

        if ( chain == null )
        {
            this.chain = new CopyOnWriteArraySet<ChainProcessEntry> ();
        }
        else
        {
            final Set<ChainProcessEntry> newChain = new CopyOnWriteArraySet<ChainProcessEntry> ( chain );
            for ( final ChainProcessEntry entry : newChain )
            {
                entry.getWhat ().dataItemChanged ( this );
            }
            this.chain = newChain;
        }
        process ();
    }

    public void addChainElement ( final EnumSet<IODirection> when, final ChainItem item )
    {
        if ( this.chain.add ( new ChainProcessEntry ( when, item ) ) )
        {
            item.dataItemChanged ( this );
            process ();
        }
    }

    public void addChainElement ( final IODirection when, final ChainItem item )
    {
        if ( this.chain.add ( new ChainProcessEntry ( EnumSet.of ( when ), item ) ) )
        {
            item.dataItemChanged ( this );
            process ();
        }
    }

    public void removeChainElement ( final EnumSet<IODirection> when, final ChainItem item )
    {
        int n = 0;

        for ( final Iterator<ChainProcessEntry> i = this.chain.iterator (); i.hasNext (); )
        {
            final ChainProcessEntry entry = i.next ();

            if ( entry.getWhen ().equals ( when ) )
            {
                if ( entry.getWhat () == item )
                {
                    i.remove ();
                    n++;
                }
            }
        }

        if ( n > 0 )
        {
            process ();
            item.dataItemChanged ( null );
        }
    }

    protected Collection<ChainProcessEntry> getChainCopy ()
    {
        return new ArrayList<ChainProcessEntry> ( this.chain );
    }

}