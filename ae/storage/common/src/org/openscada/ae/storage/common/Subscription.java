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

import org.openscada.ae.core.Listener;
import org.openscada.ae.storage.common.impl.SessionCommon;

public class Subscription
{
    private Query _query = null;
    private SessionCommon _session = null;
    private Listener _listener = null;
    private SubscriptionReader _reader = null;
    
    private int _maxBatchSize = 0;
    
    public Listener getListener ()
    {
        return _listener;
    }
    public void setListener ( Listener listener )
    {
        _listener = listener;
    }
    public Query getQuery ()
    {
        return _query;
    }
    public void setQuery ( Query query )
    {
        _query = query;
    }
    public SessionCommon getSession ()
    {
        return _session;
    }
    public void setSession ( SessionCommon session )
    {
        _session = session;
    }
    public SubscriptionReader getReader ()
    {
        return _reader;
    }
    public void setReader ( SubscriptionReader reader )
    {
        _reader = reader;
    }
    public int getMaxBatchSize ()
    {
        return _maxBatchSize;
    }
    public void setMaxBatchSize ( int maxBatchSize )
    {
        _maxBatchSize = maxBatchSize;
    }
}
