package org.openscada.da.core.browser.common.query;

public class AttributeNameProvider implements NameProvider
{
    private String _attribute = null;
    
    public AttributeNameProvider ( String attribute )
    {
        _attribute = attribute;
    }
    
    public String getName ( ItemDescriptor descriptor )
    {
        if ( _attribute == null )
            return null;
        
        return descriptor.getAttributes ().get ( _attribute ).asString ( null );
    }

}
