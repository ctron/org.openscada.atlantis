package org.openscada.da.server.opc2.connection;

import org.jinterop.dcom.core.JIVariant;

public class OPCWriteRequest
{
    private JIVariant value;

    private String itemId;

    public OPCWriteRequest ()
    {
    }

    public OPCWriteRequest ( String itemId, JIVariant value )
    {
        this.value = value;
        this.itemId = itemId;
    }

    public JIVariant getValue ()
    {
        return value;
    }

    public void setValue ( JIVariant value )
    {
        this.value = value;
    }

    public String getItemId ()
    {
        return itemId;
    }

    public void setItemId ( String itemId )
    {
        this.itemId = itemId;
    }
}
