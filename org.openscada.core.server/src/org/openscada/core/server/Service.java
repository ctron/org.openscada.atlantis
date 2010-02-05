/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.core.server;

import java.util.Properties;

import org.openscada.core.InvalidSessionException;
import org.openscada.core.UnableToCreateSessionException;
import org.openscada.utils.lifecycle.LifecycleAware;

public interface Service extends LifecycleAware
{
    /**
     * Create a new session for further accessing the hive
     * @param props properties used to create the session
     * @return a new session
     * @throws UnableToCreateSessionException in the case the session could not be created
     */
    public Session createSession ( Properties props ) throws UnableToCreateSessionException;

    /**
     * Close the provided session
     * 
     * Closing the session includes: unregistering from all items, canceling all running operations
     * 
     * @param session the session to close
     * @throws InvalidSessionException In the case the session is not a valid session
     */
    public void closeSession ( Session session ) throws InvalidSessionException;

}
