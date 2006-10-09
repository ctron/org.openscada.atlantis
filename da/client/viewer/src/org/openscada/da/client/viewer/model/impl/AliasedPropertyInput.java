package org.openscada.da.client.viewer.model.impl;

public class AliasedPropertyInput extends PropertyInput
{
    private String _alias = null;
    
    public AliasedPropertyInput ( Object object, String name, String alias )
    {
        super ( object, name );
        _alias = alias;
    }
    
    @Override
    public String getName ()
    {
        return _alias;
    }
}
