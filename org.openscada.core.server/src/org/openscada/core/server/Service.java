/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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
