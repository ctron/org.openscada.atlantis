package org.openscada.hd.server.storage.common;

import java.util.Date;

public interface DataFactory
{

    public QueryDataBuffer.Data create ( Date start, Date end );

}
