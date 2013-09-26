/*
 * This file is part of the openSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

package org.openscada.core.client.net;

import org.eclipse.scada.utils.concurrent.AbstractFuture;
import org.openscada.net.base.MessageStateListener;
import org.openscada.net.base.data.Message;

public abstract class MessageFuture<T> extends AbstractFuture<T> implements MessageStateListener
{
    @Override
    public void messageTimedOut ()
    {
        setError ( new InterruptedException ( "Message timed out" ) );
    }

    @Override
    public void messageReply ( final Message message )
    {
        try
        {
            setResult ( process ( message ) );
        }
        catch ( final Exception e )
        {
            setError ( e );
        }
    }

    protected abstract T process ( Message message ) throws Exception;
}