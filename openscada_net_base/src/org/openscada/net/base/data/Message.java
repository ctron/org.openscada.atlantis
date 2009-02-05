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

package org.openscada.net.base.data;

public class Message
{
    public final static int CC_UNKNOWN_COMMAND_CODE = 0x00000001;
    public final static int CC_FAILED = 0x00000002;
    public final static int CC_ACK = 0x00000003;
    public final static int CC_PING = 0x00000010;
    public final static int CC_PONG = 0x00000011;

    public final static String FIELD_ERROR_INFO = "error-info";

    private int _commandCode = 0;
    private long _sequence = 0;
    private long _replySequence = 0;
    private long _timestamp = System.currentTimeMillis ();

    private MapValue _values = null;

    // ctors

    public Message ()
    {
        super ();
        _values = new MapValue ();
    }

    public Message ( int commandCode )
    {
        super ();
        _commandCode = commandCode;
        _values = new MapValue ();
    }

    public Message ( int commandCode, long replySequence )
    {
        super ();
        _commandCode = commandCode;
        _replySequence = replySequence;
        _values = new MapValue ();
    }

    // methods

    public int getCommandCode ()
    {
        return _commandCode;
    }

    public void setCommandCode ( int commandCode )
    {
        _commandCode = commandCode;
    }

    public long getSequence ()
    {
        return _sequence;
    }

    public void setSequence ( long sequence )
    {
        _sequence = sequence;
    }

    public MapValue getValues ()
    {
        return _values;
    }

    public void setValues ( MapValue values )
    {
        _values = values;
    }

    public long getReplySequence ()
    {
        return _replySequence;
    }

    public void setReplySequence ( long replySequence )
    {
        _replySequence = replySequence;
    }

    // tool methods
    public void setValue ( String name, Value value )
    {
        _values.put ( name, value );
    }

    public void setValue ( String name, String value )
    {
        _values.put ( name, new StringValue ( value ) );
    }

    public void unsetValue ( String name )
    {
        _values.remove ( name );
    }

    public long getTimestamp ()
    {
        return _timestamp;
    }

    public void setTimestamp ( long timestamp )
    {
        _timestamp = timestamp;
    }

}
