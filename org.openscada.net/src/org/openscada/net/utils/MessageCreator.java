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

package org.openscada.net.utils;

import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;
import org.openscada.utils.statuscodes.CodedExceptionBase;
import org.openscada.utils.statuscodes.StatusCode;

public class MessageCreator
{

    public static Message createUnknownMessage ( final Message inputMessage )
    {
        final Message msg = new Message ( Message.CC_UNKNOWN_COMMAND_CODE );

        msg.setReplySequence ( inputMessage.getSequence () );

        return msg;
    }

    public static Message createFailedMessage ( final Message inputMessage, final Throwable error )
    {
        String msg = null;
        if ( error instanceof CodedExceptionBase )
        {
            final StatusCode status = ( (CodedExceptionBase)error ).getStatus ();
            if ( status != null )
            {
                msg = status.toString ();
            }
        }
        else
        {
            msg = error.getMessage ();
        }

        // if we still don't have a message ... use toString()
        if ( msg == null )
        {
            msg = error.toString ();
        }

        return createFailedMessage ( inputMessage, msg );
    }

    public static Message createFailedMessage ( final Message inputMessage, final String failMessage )
    {
        return createFailedMessage ( inputMessage, Message.CC_FAILED, failMessage );
    }

    public static Message createFailedMessage ( final Message inputMessage, final int commandCode, final String failMessage )
    {
        final Message msg = new Message ( commandCode );

        msg.setReplySequence ( inputMessage.getSequence () );
        msg.setValue ( Message.FIELD_ERROR_INFO, failMessage );

        return msg;
    }

    public static Message createPing ()
    {
        final Message msg = new Message ( Message.CC_PING );
        msg.getValues ().put ( "ping-data", new StringValue ( String.valueOf ( System.currentTimeMillis () ) ) );
        return msg;
    }

    public static Message createPong ( final Message inputMessage )
    {
        final Message msg = new Message ( Message.CC_PONG, inputMessage.getSequence () );
        msg.getValues ().put ( "pong-data", inputMessage.getValues ().get ( "ping-data" ) );
        return msg;
    }

    public static Message createACK ( final Message inputMessage )
    {
        return new Message ( Message.CC_ACK, inputMessage.getSequence () );
    }

}
