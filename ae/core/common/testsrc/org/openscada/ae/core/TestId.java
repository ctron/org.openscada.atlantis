package org.openscada.ae.core;

import org.junit.Assert;
import org.junit.Test;
import org.openscada.core.Variant;
import org.openscada.utils.collection.MapBuilder;

public class TestId
{
    @Test
    public void testEqual1 ()
    {
        QueryDescription qd1 = new QueryDescription ( "id1" );
        qd1.setAttributes ( new MapBuilder<String, Variant> ().put ( "test", new Variant ( 1 ) ).getMap () );

        QueryDescription qd2 = new QueryDescription ( "id1" );
        qd1.setAttributes ( new MapBuilder<String, Variant> ().put ( "test", new Variant ( 1 ) ).getMap () );

        Assert.assertEquals ( qd1, qd2 );
    }

    @Test
    public void testNotEqual1 ()
    {
        QueryDescription qd1 = new QueryDescription ( "id1" );
        qd1.setAttributes ( new MapBuilder<String, Variant> ().put ( "test", new Variant ( 1 ) ).getMap () );

        QueryDescription qd2 = new QueryDescription ( "id2" );
        qd1.setAttributes ( new MapBuilder<String, Variant> ().put ( "test", new Variant ( 1 ) ).getMap () );

        Assert.assertFalse ( "Must not be equal", qd1.equals ( qd2 ) );
    }

    @Test
    public void testEqual2 ()
    {
        QueryDescription qd1 = new QueryDescription ( "id1" );
        qd1.setAttributes ( new MapBuilder<String, Variant> ().put ( "test", new Variant ( 1 ) ).getMap () );

        QueryDescription qd2 = new QueryDescription ( "id1" );
        qd1.setAttributes ( new MapBuilder<String, Variant> ().put ( "test2", new Variant ( 1 ) ).getMap () );

        Assert.assertEquals ( qd1, qd2 );
    }

    @Test
    public void testNotEqual3 ()
    {
        QueryDescription qd1 = new QueryDescription ( "id1" );
        qd1.setAttributes ( new MapBuilder<String, Variant> ().put ( "test", new Variant ( 1 ) ).getMap () );

        QueryDescription qd2 = new QueryDescription ( "id1" );
        qd1.setAttributes ( new MapBuilder<String, Variant> ().put ( "test", new Variant ( 2 ) ).getMap () );

        Assert.assertEquals ( qd1, qd2 );
    }
}
