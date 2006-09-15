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

package org.openscada.ae.core;

import java.util.Properties;

import org.openscada.core.CancellationNotSupportedException;
import org.openscada.core.InvalidSessionException;
import org.openscada.core.UnableToCreateSessionException;

public interface Storage extends Submission
{
    public Session createSession ( Properties properties ) throws UnableToCreateSessionException;
    public void closeSession ( Session session ) throws InvalidSessionException;
    
    void subscribe ( Session session, String queryID, Listener listener, int maxBatchSize, int archiveSet ) throws InvalidSessionException, NoSuchQueryException;
    void unsubscribe ( Session session, String queryID, Listener listener ) throws InvalidSessionException, NoSuchQueryException;
    Event[] read ( Session session, String queryID ) throws InvalidSessionException, NoSuchQueryException;

    long startList ( Session session, ListOperationListener listener ) throws InvalidSessionException;
    
    public void cancelOperation ( Session session, long id ) throws InvalidSessionException, CancellationNotSupportedException;
    public void thawOperation ( Session session, long id ) throws InvalidSessionException;
}
