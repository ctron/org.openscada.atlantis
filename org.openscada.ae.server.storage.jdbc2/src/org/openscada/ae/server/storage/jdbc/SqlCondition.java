package org.openscada.ae.server.storage.jdbc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SqlCondition
{

    public String condition = "";

    public List<String> joins = new ArrayList<String> ();

    public List<String> joinParameters = new ArrayList<String> ();

    public List<Serializable> parameters = new ArrayList<Serializable> ();

    @Override
    public String toString ()
    {
        return condition + " (params = " + parameters + ")";
    }

}
