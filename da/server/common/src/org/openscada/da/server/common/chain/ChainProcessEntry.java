package org.openscada.da.server.common.chain;

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

    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( _what == null ) ? 0 : _what.hashCode () );
        return result;
    }

    @Override
    public boolean equals ( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass () != obj.getClass () )
            return false;
        final ChainProcessEntry other = (ChainProcessEntry)obj;
        if ( _what == null )
        {
            if ( other._what != null )
                return false;
        }
        else if ( !_what.equals ( other._what ) )
            return false;
        return true;
    }
}
