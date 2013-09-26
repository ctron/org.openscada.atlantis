/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
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

package org.eclipse.scada.net.utils;

import org.eclipse.scada.net.base.data.Message;
import org.eclipse.scada.net.base.data.StringValue;
import org.eclipse.scada.utils.ExceptionHelper;
import org.eclipse.scada.utils.statuscodes.CodedExceptionBase;
import org.eclipse.scada.utils.statuscodes.StatusCode;

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

        final Throwable root = ExceptionHelper.getRootCause ( error );

        if ( root instanceof CodedExceptionBase )
        {
            final StatusCode status = ( (CodedExceptionBase)root ).getStatus ();
            if ( status != null )
            {
                msg = status.toString ();
            }
        }
        else
        {
            msg = ExceptionHelper.getMessage ( error );
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
