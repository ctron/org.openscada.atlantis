package org.openscada.da.server.snmp.utils;

import org.apache.log4j.Logger;
import org.openscada.da.server.snmp.SNMPNode;
import org.snmp4j.smi.OID;

public class DumpOIDWalker extends WalkOperation
{
    private static Logger _log = Logger.getLogger ( DumpOIDWalker.class );
    
    public DumpOIDWalker ( SNMPNode node, OID oid, boolean useBulkGet )
    {
        super ( node, oid, useBulkGet );
    }

    @Override
    protected void handleOID ( OID oid )
    {
       _log.info ( "OID found: " + oid.toString () );
    }

}
