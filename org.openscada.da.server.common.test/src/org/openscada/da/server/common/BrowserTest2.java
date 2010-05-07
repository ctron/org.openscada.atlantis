package org.openscada.da.server.common;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;
import org.openscada.core.Variant;
import org.openscada.da.server.browser.common.query.IDNameProvider;
import org.openscada.da.server.browser.common.query.ItemDescriptor;
import org.openscada.da.server.browser.common.query.SplitGroupProvider;
import org.openscada.da.server.browser.common.query.SplitNameProvider;

public class BrowserTest2
{
    @Test
    public void test1 ()
    {
        Assert.assertEquals ( "", testName ( "", 0, 2 ) );
        Assert.assertEquals ( "", testName ( "", 2, 0 ) );
        Assert.assertEquals ( "", testName ( "", 2, 2 ) );
        Assert.assertEquals ( "", testName ( "", 0, 0 ) );

        Assert.assertEquals ( "", testName ( "this.id.the.id", 0, 0 ) );

        Assert.assertEquals ( "the.id", testName ( "this.is.the.id", 0, 2 ) );
        Assert.assertEquals ( "this.is", testName ( "this.is.the.id", 2, 0 ) );

        Assert.assertEquals ( "this.is.the.id", testName ( "this.is.the.id", 10, 0 ) );
        Assert.assertEquals ( "this.is.the.id", testName ( "this.is.the.id", 0, 10 ) );
    }

    private String testName ( final String itemId, final int fromStart, final int fromEnd )
    {
        final SplitNameProvider snp = new SplitNameProvider ( new IDNameProvider (), "\\.", fromStart, fromEnd, "." );
        return snp.getName ( new ItemDescriptor ( new DataItemInputCommon ( itemId ), new HashMap<String, Variant> () ) );
    }

    @Test
    public void test2 ()
    {
        Assert.assertArrayEquals ( new String[] { "this", "is", "the", "id" }, testGroup ( "this.is.the.id", 0, 0 ) );

        Assert.assertArrayEquals ( new String[] { "this", "is", "the" }, testGroup ( "this.is.the.id", 0, 1 ) );
        Assert.assertArrayEquals ( new String[] { "this", "is" }, testGroup ( "this.is.the.id", 0, 2 ) );

        Assert.assertArrayEquals ( new String[] { "is", "the", "id" }, testGroup ( "this.is.the.id", 1, 0 ) );
        Assert.assertArrayEquals ( new String[] { "the", "id" }, testGroup ( "this.is.the.id", 2, 0 ) );
    }

    private String[] testGroup ( final String itemId, final int skipPrefix, final int skipSuffix )
    {
        final SplitGroupProvider sgp = new SplitGroupProvider ( new IDNameProvider (), "\\.", skipPrefix, skipSuffix );
        return sgp.getGrouping ( new ItemDescriptor ( new DataItemInputCommon ( itemId ), new HashMap<String, Variant> () ) );
    }
}
