package org.openscada.da.server.snmp;

public class ConnectionInformation implements Cloneable
{
    private String _name = null;
    private String _address = null;
    
    private String _community = null;
   
    public ConnectionInformation ( String name )
    {
        _name = name;
    }
    
    public ConnectionInformation ( ConnectionInformation arg0 )
    {
        if ( arg0._address != null )
            _address = new String ( arg0._address );
        if ( arg0._name != null )
            _name = new String ( arg0._name );
        
        if ( arg0._community != null )
            _community = new String ( arg0._community );
    }

    public String getAddress ()
    {
        return _address;
    }

    public void setAddress ( String address )
    {
        _address = address;
    }
    
    @Override
    public Object clone ()
    {
        return new ConnectionInformation ( this ); 
    }

    public String getName ()
    {
        return _name;
    }

    public void setName ( String name )
    {
        _name = name;
    }

    public String getCommunity ()
    {
        return _community;
    }

    public void setCommunity ( String community )
    {
        _community = community;
    }
}
