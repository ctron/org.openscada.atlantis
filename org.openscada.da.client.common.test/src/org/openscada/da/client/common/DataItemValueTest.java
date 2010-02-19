package org.openscada.da.client.common;

import org.junit.Assert;
import org.junit.Test;
import org.openscada.core.Variant;
import org.openscada.core.subscription.SubscriptionState;
import org.openscada.da.client.DataItemValue;
import org.openscada.da.client.DataItemValue.Builder;

public class DataItemValueTest
{
    @Test
    public void test1 ()
    {
        final DataItemValue div1 = new DataItemValue ();
        final DataItemValue div2 = new DataItemValue ();

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
        div1.setValue ( new Variant ( 1 ) );

        final Builder div2 = new Builder ();
        div2.setValue ( new Variant ( 1 ) );

        Assert.assertEquals ( div1.build (), div2.build () );
        Assert.assertEquals ( div2.build (), div1.build () );
    }

    @Test
    public void test4 ()
    {
        final Builder div1 = new Builder ();
        div1.setValue ( new Variant ( 1 ) );
        div1.setSubscriptionState ( SubscriptionState.CONNECTED );

        final Builder div2 = new Builder ();
        div2.setValue ( new Variant ( 1 ) );
        div2.setSubscriptionState ( SubscriptionState.CONNECTED );

        Assert.assertEquals ( div1.build (), div2.build () );
        Assert.assertEquals ( div2.build (), div1.build () );
    }

    @Test
    public void test5 ()
    {
        final Builder div1 = new Builder ();
        div1.setValue ( new Variant ( 1 ) );
        div1.setSubscriptionState ( SubscriptionState.CONNECTED );
        div1.setAttribute ( "foo", new Variant ( "bar" ) );

        final Builder div2 = new Builder ();
        div2.setValue ( new Variant ( 1 ) );
        div2.setSubscriptionState ( SubscriptionState.CONNECTED );
        div2.setAttribute ( "foo", new Variant ( "bar" ) );

        Assert.assertEquals ( div1.build (), div2.build () );
        Assert.assertEquals ( div2.build (), div1.build () );
    }

    @Test
    public void testFalse1 ()
    {
        final Builder div1 = new Builder ();
        div1.setValue ( new Variant ( 1 ) );
        div1.setSubscriptionState ( SubscriptionState.CONNECTED );
        div1.setAttribute ( "foo", new Variant ( "bar" ) );

        final Builder div2 = new Builder ();
        div2.setValue ( new Variant ( 1 ) );
        div2.setSubscriptionState ( SubscriptionState.CONNECTED );
        div2.setAttribute ( "bar", new Variant ( "foo" ) );

        Assert.assertFalse ( div1.equals ( div2 ) );
        Assert.assertFalse ( div2.equals ( div1 ) );
    }

    @Test
    public void testFalse2 ()
    {
        final Builder div1 = new Builder ();
        div1.setValue ( new Variant ( 1 ) );
        div1.setSubscriptionState ( SubscriptionState.CONNECTED );
        div1.setAttribute ( "foo", new Variant ( "bar" ) );

        final Builder div2 = new Builder ();
        div2.setValue ( new Variant ( 1 ) );
        div2.setSubscriptionState ( SubscriptionState.DISCONNECTED );
        div2.setAttribute ( "foo", new Variant ( "bar" ) );

        Assert.assertFalse ( div1.equals ( div2 ) );
        Assert.assertFalse ( div2.equals ( div1 ) );
    }

    @Test
    public void testFalse3 ()
    {
        final Builder div1 = new Builder ();
        div1.setValue ( new Variant ( 1 ) );
        div1.setSubscriptionState ( SubscriptionState.CONNECTED );
        div1.setAttribute ( "foo", new Variant ( "bar" ) );

        final Builder div2 = new Builder ();
        div2.setSubscriptionState ( SubscriptionState.CONNECTED );
        div2.setAttribute ( "foo", new Variant ( "bar" ) );

        Assert.assertFalse ( div1.equals ( div2 ) );
        Assert.assertFalse ( div2.equals ( div1 ) );
    }

    @Test
    public void testFalse4 ()
    {
        final Builder div1 = new Builder ();
        div1.setValue ( new Variant ( 1 ) );
        div1.setSubscriptionState ( SubscriptionState.CONNECTED );
        div1.setAttribute ( "foo", new Variant ( "bar" ) );

        final Builder div2 = new Builder ();
        div2.setValue ( new Variant ( 1 ) );
        div2.setSubscriptionState ( SubscriptionState.CONNECTED );
        div2.setAttribute ( "foo", new Variant ( "bar" ) );

        Assert.assertFalse ( div1.equals ( div2 ) );
        Assert.assertFalse ( div2.equals ( div1 ) );
    }
}
