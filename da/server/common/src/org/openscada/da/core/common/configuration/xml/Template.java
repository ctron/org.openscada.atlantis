package org.openscada.da.core.common.configuration.xml;

import java.util.regex.Pattern;

public class Template extends ItemBase
{
    private Pattern _pattern = null;
    
    public Template ()
    {
        super ();
    }
    
    public Template ( Template arg0 )
    {
        super ( arg0 );
        
        _pattern = arg0._pattern;
        
    }
    
    public Pattern getPattern ()
    {
        return _pattern;
    }

    public void setPattern ( Pattern pattern )
    {
        _pattern = pattern;
    }
}
