/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.client.common;

import org.junit.Assert;
import org.junit.Test;
import org.openscada.core.Variant;
import org.openscada.core.data.SubscriptionState;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.DataItemValue.Builder;

public class DataItemValueTest
{
    @Test
    public void test1 ()
    {
        final DataItemValue div1 = DataItemValue.DISCONNECTED;
        final DataItemValue div2 = DataItemValue.DISCONNECTED;

        Assert.assertEquals ( div1, div2 );
        Assert.assertEquals ( div2, div1 );
    }

    @Test
    public void test2 ()
    {
        final Builder div1 = new Builder ();
        final Builder div2 = new Builder ();

        Assert.assertEquals ( div1.build (), div2.build () );
        Assert.assertEquals ( div2.build (), div1.build () );
    }

    @Test
    public void test3 ()
    {
        final Builder div1 = new Builder ();
        div1.setValue ( Variant.valueOf ( 1 ) );

        final Builder div2 = new Builder ();
        div2.setValue ( Variant.valueOf ( 1 ) );

        Assert.assertEquals ( div1.build (), div2.build () );
        Assert.assertEquals ( div2.build (), div1.build () );
    }

    @Test
    public void test4 ()
    {
        final Builder div1 = new Builder ();
        div1.setValue ( Variant.valueOf ( 1 ) );
        div1.setSubscriptionState ( SubscriptionState.CONNECTED );

        final Builder div2 = new Builder ();
        div2.setValue ( Variant.valueOf ( 1 ) );
        div2.setSubscriptionState ( SubscriptionState.CONNECTED );

        Assert.assertEquals ( div1.build (), div2.build () );
        Assert.assertEquals ( div2.build (), div1.build () );
    }

    @Test
    public void test5 ()
    {
        final Builder div1 = new Builder ();
        div1.setValue ( Variant.valueOf ( 1 ) );
        div1.setSubscriptionState ( SubscriptionState.CONNECTED );
        div1.setAttribute ( "foo", Variant.valueOf ( "bar" ) );

        final Builder div2 = new Builder ();
        div2.setValue ( Variant.valueOf ( 1 ) );
        div2.setSubscriptionState ( SubscriptionState.CONNECTED );
        div2.setAttribute ( "foo", Variant.valueOf ( "bar" ) );

        Assert.assertEquals ( div1.build (), div2.build () );
        Assert.assertEquals ( div2.build (), div1.build () );
    }

    @Test
    public void testFalse1 ()
    {
        final Builder div1 = new Builder ();
        div1.setValue ( Variant.valueOf ( 1 ) );
        div1.setSubscriptionState ( SubscriptionState.CONNECTED );
        div1.setAttribute ( "foo", Variant.valueOf ( "bar" ) );

        final Builder div2 = new Builder ();
        div2.setValue ( Variant.valueOf ( 1 ) );
        div2.setSubscriptionState ( SubscriptionState.CONNECTED );
        div2.setAttribute ( "bar", Variant.valueOf ( "foo" ) );

        Assert.assertFalse ( div1.equals ( div2 ) );
        Assert.assertFalse ( div2.equals ( div1 ) );
    }

    @Test
    public void testFalse2 ()
    {
        final Builder div1 = new Builder ();
        div1.setValue ( Variant.valueOf ( 1 ) );
        div1.setSubscriptionState ( SubscriptionState.CONNECTED );
        div1.setAttribute ( "foo", Variant.valueOf ( "bar" ) );

        final Builder div2 = new Builder ();
        div2.setValue ( Variant.valueOf ( 1 ) );
        div2.setSubscriptionState ( SubscriptionState.DISCONNECTED );
        div2.setAttribute ( "foo", Variant.valueOf ( "bar" ) );

        Assert.assertFalse ( div1.equals ( div2 ) );
        Assert.assertFalse ( div2.equals ( div1 ) );
    }

    @Test
    public void testFalse3 ()
    {
        final Builder div1 = new Builder ();
        div1.setValue ( Variant.valueOf ( 1 ) );
        div1.setSubscriptionState ( SubscriptionState.CONNECTED );
        div1.setAttribute ( "foo", Variant.valueOf ( "bar" ) );

        final Builder div2 = new Builder ();
        div2.setSubscriptionState ( SubscriptionState.CONNECTED );
        div2.setAttribute ( "foo", Variant.valueOf ( "bar" ) );

        Assert.assertFalse ( div1.equals ( div2 ) );
        Assert.assertFalse ( div2.equals ( div1 ) );
    }

    @Test
    public void testFalse4 ()
    {
        final Builder div1 = new Builder ();
        div1.setValue ( Variant.valueOf ( 1 ) );
        div1.setSubscriptionState ( SubscriptionState.CONNECTED );
        div1.setAttribute ( "foo", Variant.valueOf ( "bar" ) );

        final Builder div2 = new Builder ();
        div2.setValue ( Variant.valueOf ( 1 ) );
        div2.setSubscriptionState ( SubscriptionState.CONNECTED );
        div2.setAttribute ( "foo", Variant.valueOf ( "bar" ) );

        Assert.assertFalse ( div1.equals ( div2 ) );
        Assert.assertFalse ( div2.equals ( div1 ) );
    }
}
