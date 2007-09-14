package org.openscada.da.server.browser.common.query;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IDNamePatternProvider extends IDNameProvider
{
    /**
     * A name pattern. If set the name will be built from the group #0
     * match of this pattern.
     */
    private Pattern _namePattern;
    private int _matchGroup = 0;

    public void setNamePattern ( Pattern namePattern )
    {
        _namePattern = namePattern;
    }
    
    public void setMatchGroup ( int matchGroup )
    {
        _matchGroup = matchGroup;
    }
    
    @Override
    public String getName ( ItemDescriptor descriptor )
    {
        String name = super.getName ( descriptor );
        if ( name == null )
        {
            return null;
        }
        
        Matcher m = _namePattern.matcher ( name );
        if ( m.matches () )
        {
            return m.group ( _matchGroup );
        }
        else
        {
            return null;
        }
    }

}
