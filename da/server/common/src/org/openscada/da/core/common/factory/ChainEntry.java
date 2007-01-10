package org.openscada.da.core.common.factory;

import java.util.EnumSet;

import org.openscada.da.core.IODirection;

public class ChainEntry
{
    private Class _what = null;
    private EnumSet<IODirection> _when = EnumSet.noneOf ( IODirection.class );
    
    public Class getWhat ()
    {
        return _what;
    }
    public void setWhat ( Class what )
    {
        _what = what;
    }
    public EnumSet<IODirection> getWhen ()
    {
        return _when;
    }
    public void setWhen ( EnumSet<IODirection> when )
    {
        _when = when;
    }
    
}
