package org.openscada.da.core.browser.common.query;

public class SplitGroupProvider implements GroupProvider
{
    private NameProvider _nameProvider = null;
    private String _regex = "";
    
    public SplitGroupProvider ( NameProvider nameProvider, String regex )
    {
        _nameProvider = nameProvider;
        _regex = regex;
    }
    
    public String[] getGrouping ( ItemDescriptor descriptor )
    {
        if ( _nameProvider == null )
            return null;
        
        String name = _nameProvider.getName ( descriptor );
        
        if ( name == null )
            return null;
        
        try
        {
            return name.split ( _regex );
        }
        catch ( Exception e )
        {
            return null;
        }
    }

}
