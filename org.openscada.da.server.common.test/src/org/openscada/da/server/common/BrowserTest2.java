package org.openscada.da.server.common;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;
import org.openscada.core.Variant;
import org.openscada.da.server.browser.common.query.IDNameProvider;
import org.openscada.da.server.browser.common.query.ItemDescriptor;
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
}
