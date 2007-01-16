package org.openscada.da.client.ice;

import java.util.Map;

import org.openscada.core.ice.AttributesHelper;
import org.openscada.core.ice.VariantHelper;

import Ice.Current;
import OpenSCADA.Core.VariantBase;
import OpenSCADA.DA._DataCallbackDisp;

public class DataCallbackImpl extends _DataCallbackDisp
{
    private Connection _connection = null;
    
    public DataCallbackImpl ( Connection connection )
    {
        super ();
        _connection = connection;
    } 
   
    public void attributesChange ( String item, Map attributes, boolean full, Current __current )
    {
        _connection.attributesChange ( item, AttributesHelper.fromIce ( attributes ), full );
    }

    public void valueChange ( String item, VariantBase value, boolean cache, Current __current )
    {
        _connection.valueChange ( item, VariantHelper.fromIce ( value ), cache );
    }

}
