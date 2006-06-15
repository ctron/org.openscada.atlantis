package org.openscada.da.server.snmp.utils;

import java.util.HashSet;
import java.util.Set;

import org.openscada.da.server.snmp.SNMPNode;
import org.snmp4j.smi.OID;

public class ListOIDWalker extends WalkOperation
{

    private Set<OID> _oidSet = new HashSet<OID> ();
    
    public ListOIDWalker ( SNMPNode node, OID oid, boolean useBulkGet )
    {
        super ( node, oid, useBulkGet );
    }

    @Override
    protected void handleOID ( OID oid )
    {
       _oidSet.add ( oid );
    }
    
    public Set<OID> getList ()
    {
        return _oidSet;
    }
    
}
