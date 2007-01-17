package org.openscada.da.client.ice;

import java.util.Map;

import org.apache.log4j.Logger;
import org.openscada.core.ice.AttributesHelper;
import org.openscada.core.ice.VariantHelper;

import Ice.Current;
import OpenSCADA.Core.VariantBase;
import OpenSCADA.DA._DataCallbackDisp;

public class DataCallbackImpl extends _DataCallbackDisp
{
    private static Logger _log = Logger.getLogger ( DataCallbackImpl.class );
    
    private Connection _connection = null;
    
    public DataCallbackImpl ( Connection connection )
    {
        super ();
        _connection = connection;
    } 
   
    public void attributesChange ( String item, Map attributes, boolean full, Current __current )
    {
        _log.debug ( String.format ( "Attribute change: '%s'", item ) );
        _connection.attributesChange ( item, AttributesHelper.fromIce ( attributes ), full );
    }

    public void valueChange ( String item, VariantBase value, boolean cache, Current __current )
    {
        _log.debug ( String.format ( "Value change: '%s'", item ) );
        _connection.valueChange ( item, VariantHelper.fromIce ( value ), cache );
    }

}
