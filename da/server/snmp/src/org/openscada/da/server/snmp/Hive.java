package org.openscada.da.server.snmp;


import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openscada.da.core.browser.common.FolderCommon;
import org.openscada.da.core.common.impl.HiveCommon;

public class Hive extends HiveCommon
{
    @SuppressWarnings("unused")
    private static Logger _log = Logger.getLogger ( Hive.class );
    
    private Map<String, SNMPNode> _nodeMap = new HashMap<String, SNMPNode> ();
    
	public Hive ()
	{
		super();
		
        // create root folder
		FolderCommon rootFolder = new FolderCommon ();
        setRootFolder ( rootFolder );

        SNMPNode node;
        
        ConnectionInformation connectionInformation = new ConnectionInformation ( "localhost" );
        connectionInformation.setAddress ( "udp:127.0.0.1/161" );
        connectionInformation.setCommunity ( "public" );
        
        node = new SNMPNode ( this, rootFolder, connectionInformation );
        node.register ();
        _nodeMap.put ( "localhost", node );

	}
}
