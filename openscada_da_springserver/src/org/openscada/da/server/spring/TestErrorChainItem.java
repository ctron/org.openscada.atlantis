package org.openscada.da.server.spring;

import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.da.server.common.chain.AttributeBinder;
import org.openscada.da.server.common.chain.BaseChainItemCommon;
import org.openscada.da.server.common.chain.VariantBinder;

public class TestErrorChainItem extends BaseChainItemCommon
{

    private final AttributeBinder testErrorFlagBinder = new VariantBinder ( new Variant () );

    public TestErrorChainItem ()
    {
        super ( null );
        addBinder ( "test.error", this.testErrorFlagBinder );
    }

    @Override
    public boolean isPersistent ()
    {
        return false;
    }

    public Variant process ( final Variant value, final Map<String, Variant> attributes )
    {
        // do nothing

        // add my attributes
        addAttributes ( attributes );

        return null;
    }
}
