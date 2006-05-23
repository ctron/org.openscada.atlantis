package org.openscada.da.core.common;

import java.util.EnumSet;

import org.openscada.da.core.DataItemInformation;
import org.openscada.da.core.IODirection;

public class DataItemInformationBase implements
        org.openscada.da.core.DataItemInformation
{
    private String _name = "";
    private EnumSet<IODirection> _ioDirection = EnumSet.noneOf ( IODirection.class );
    
    public DataItemInformationBase ( String name, EnumSet<IODirection> ioDirection )
    {
        super ();
        _name = new String ( name );
        _ioDirection = ioDirection.clone ();
    }
    
    public DataItemInformationBase ( String name )
    {
        super ();
        _name = new String ( name );
    }
    
    public DataItemInformationBase ( DataItemInformation information )
    {
        super();
        
        _name = new String ( information.getName() );
        _ioDirection = information.getIODirection ().clone ();
    }

    public EnumSet<IODirection> getIODirection ()
    {
        return _ioDirection;
    }
    
    public String getName ()
    {
        return _name;
    }

    @Override
    public int hashCode ()
    {
        if ( _name == null )
            return "".hashCode ();
        else
            return _name.hashCode ();
    }

    @Override
    public boolean equals ( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( !( obj instanceof DataItemInformation) )
            return false;
        
        final DataItemInformation other = (DataItemInformation)obj;
        if ( _name == null )
        {
            if ( other.getName() != null )
                return false;
        }
        else
            if ( !_name.equals ( other.getName() ) )
                return false;
        return true;
    }

}
