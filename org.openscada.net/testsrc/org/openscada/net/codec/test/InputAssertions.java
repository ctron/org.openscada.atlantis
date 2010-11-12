/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.net.codec.test;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;


import org.apache.mina.core.filterchain.IoFilter.NextFilter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.junit.Assert;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.Value;

public class InputAssertions implements ProtocolDecoderOutput
{

    private final Collection<Message> messages = new LinkedList<Message> ();

    public void flush ( final NextFilter nextFilter, final IoSession session )
    {
    }

    public void write ( final Object message )
    {
        this.messages.add ( (Message)message );
    }

    public void assertMessages ( final Collection<Message> assertMessages )
    {
        Assert.assertEquals ( "Number of messages", assertMessages.size (), this.messages.size () );

        final Iterator<Message> i1 = assertMessages.iterator ();
        final Iterator<Message> i2 = this.messages.iterator ();

        while ( i1.hasNext () )
        {
            final Message m1 = i1.next ();
            final Message m2 = i2.next ();

            Assert.assertEquals ( "Command code", m1.getCommandCode (), m2.getCommandCode () );
            Assert.assertEquals ( "Sequence", m1.getSequence (), m2.getSequence () );
            Assert.assertEquals ( "Reply sequence", m1.getReplySequence (), m2.getReplySequence () );
            Assert.assertEquals ( "Number of values", m1.getValues ().size (), m2.getValues ().size () );

            final Iterator<Map.Entry<String, Value>> vi1 = m1.getValues ().getValues ().entrySet ().iterator ();
            final Iterator<Map.Entry<String, Value>> vi2 = m2.getValues ().getValues ().entrySet ().iterator ();

            while ( vi1.hasNext () )
            {
                final Map.Entry<String, Value> e1 = vi1.next ();
                final Map.Entry<String, Value> e2 = vi2.next ();

                Assert.assertEquals ( "Entry name", e1.getKey (), e2.getKey () );
                Assert.assertEquals ( "Equal Type", e1.getValue ().getClass (), e2.getValue ().getClass () );
                Assert.assertEquals ( "Equal Value", e1.getValue (), e2.getValue () );
            }
        }
    }

}
