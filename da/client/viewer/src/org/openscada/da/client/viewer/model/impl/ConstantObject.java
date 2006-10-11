package org.openscada.da.client.viewer.model.impl;

public class ConstantObject extends BaseDynamicObject
{
    public ConstantObject ( String id, ConstantOutput output )
    {
        super ( id );
        addOutput ( output );
    }
}
