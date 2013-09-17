/*******************************************************************************
 * Copyright (c) 2013 IBH SYSTEMS GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBH SYSTEMS GmbH - initial API and implementation
 *******************************************************************************/
package org.openscada.da.server.common.io;

public interface PollRequest
{

    /**
     * Create poll request
     */
    public Object createPollRequest ();

    /**
     * Handle an incoming message while the job was active
     * 
     * @param message
     *            the incoming message
     * @return if the message as processed
     */
    public boolean handleMessage ( Object message );

    public void handleFailure ();

    public void handleDisconnect ();

    public long updatePriority ( long now );

    public void dispose ();

}
