package org.openscada.hd.server.proxy;

import java.util.HashMap;

import org.openscada.hd.QueryParameters;
import org.openscada.hd.QueryState;
import org.openscada.hd.Value;
import org.openscada.hd.ValueInformation;

public interface QueryDataHolder
{

    public QueryParameters getParameters ();

    public QueryState getState ();

    public ValueInformation[] getValueInformation ();

    public HashMap<String, Value[]> getValues ();

}
