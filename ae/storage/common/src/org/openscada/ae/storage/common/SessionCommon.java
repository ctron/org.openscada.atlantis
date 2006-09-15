/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
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

package org.openscada.ae.storage.common;

import java.util.List;

import org.openscada.ae.core.Listener;
import org.openscada.ae.core.Session;
import org.openscada.core.server.common.SessionCommonOperations;

public class SessionCommon implements Session
{
    private StorageCommon _storage = null;
    
    private List<Subscription> _subscriptions = null;
    private SessionCommonOperations _operations = new SessionCommonOperations ();
    
    public SessionCommon ( StorageCommon storage )
    {
        super ();
        _storage = storage;
    }

    public StorageCommon getStorage ()
    {
        return _storage;
    }
    
    /**
     * Invalidate the session. May only be called when the session
     * is already closed by SessionCommon 
     *
     */
    protected void invalidate ()
    {
        _storage = null;
    }

    public void addSubscription ( Subscription subscription )
    {
        _subscriptions.add ( subscription );
    }
    
    public void removeSubscription ( Subscription subscription )
    {
        _subscriptions.remove ( subscription );
    }
    
    public List<Subscription> getSubscriptions ()
    {
        return _subscriptions;
    }
    
    public Subscription findSubscription ( Query query, Listener listener )
    {
        for ( Subscription subscription : _subscriptions )
        {
            if ( subscription.getListener () == listener && subscription.getQuery () == query )
                return subscription;
        }
        return null;
    }

    public SessionCommonOperations getOperations ()
    {
        return _operations;
    }

    public void setOperations ( SessionCommonOperations operations )
    {
        _operations = operations;
    }
}
