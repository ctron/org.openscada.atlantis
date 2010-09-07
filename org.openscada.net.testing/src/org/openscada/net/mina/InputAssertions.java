/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://inavare.com)
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

package org.openscada.net.mina;

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
