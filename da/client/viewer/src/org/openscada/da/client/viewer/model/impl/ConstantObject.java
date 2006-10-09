package org.openscada.da.client.viewer.model.impl;

public class ConstantObject extends BaseDynamicObject
{
    public ConstantObject ( ConstantOutput output )
    {
        super ();
        addOutput ( output );
    }
}
