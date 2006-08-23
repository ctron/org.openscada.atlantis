package org.openscada.da.core.common.chained;

import java.util.EnumSet;

import org.openscada.da.core.IODirection;

public class ChainProcessEntry
{
    private EnumSet<IODirection> _when = EnumSet.noneOf ( IODirection.class );
    private ChainItem _what = null;
    
    public ChainProcessEntry ( EnumSet<IODirection> when, ChainItem what )
    {
        super ();
        _when = when;
        _what = what;
    }
    
    public ChainProcessEntry ()
    {
        super ();
    }
    
    public ChainItem getWhat ()
    {
        return _what;
    }
    public void setWhat ( ChainItem what )
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
