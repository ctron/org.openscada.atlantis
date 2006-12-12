package org.openscada.da.server.opc;

import org.openscada.opc.lib.common.ConnectionInformation;

public class ConnectionSetup
{
    private ConnectionInformation _connectionInformation = null;
    private boolean _initialConnect = true;
    private AccessMethod _accessMethod = AccessMethod.ASYNC20;
    private int _refreshTimeout = 500;
    
    public ConnectionSetup ()
    {
        super ();
    }

    public ConnectionSetup ( ConnectionInformation connectionInformation )
    {
        super ();
        _connectionInformation = connectionInformation; 
    }
    
    public AccessMethod getAccessMethod ()
    {
        return _accessMethod;
    }
    public void setAccessMethod ( AccessMethod accessMethod )
    {
        _accessMethod = accessMethod;
    }
    public ConnectionInformation getConnectionInformation ()
    {
        return _connectionInformation;
    }
    public void setConnectionInformation ( ConnectionInformation connectionInformation )
    {
        _connectionInformation = connectionInformation;
    }
    public boolean isInitialConnect ()
    {
        return _initialConnect;
    }
    public void setInitialConnect ( boolean initialConnect )
    {
        _initialConnect = initialConnect;
    }
    public int getRefreshTimeout ()
    {
        return _refreshTimeout;
    }
    public void setRefreshTimeout ( int refreshTimeout )
    {
        _refreshTimeout = refreshTimeout;
    } 
}
