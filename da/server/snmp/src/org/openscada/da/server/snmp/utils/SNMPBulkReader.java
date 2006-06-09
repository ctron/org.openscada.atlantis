package org.openscada.da.server.snmp.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.openscada.da.server.snmp.Connection;
import org.openscada.da.server.snmp.SNMPNode;
import org.openscada.da.server.snmp.items.SNMPItem;
import org.snmp4j.PDU;
import org.snmp4j.Target;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

public class SNMPBulkReader
{
    private static Logger _log = Logger.getLogger ( SNMPBulkReader.class );
    
    private SNMPNode _node = null;
    
    private Map<OID, SNMPItem> _list = new HashMap<OID, SNMPItem> ();
    
    public SNMPBulkReader ( SNMPNode node )
    {
        _node = node;
    }
    
    public void add ( SNMPItem item )
    {
        synchronized ( _list )
        {
            _list.put ( item.getOID (), item );
        }
    }
    
    public void remove ( SNMPItem item )
    {
        synchronized ( _list )
        {
            _list.remove ( item.getOID () );    
        }
    }
    
    public void read ()
    {
        try
        {
            performRead ();
        }
        catch ( Exception e )
        {
          _log.warn ( "GETBULK failed: ", e );
          setError ( e );
        }
    }
    
    @SuppressWarnings("unchecked")
    private void performRead () throws Exception
    {
        Connection connection = _node.getConnection ();
        
        Target target = connection.createTarget ();
        PDU pdu = connection.createPDU ( target, PDU.GET );
        
        synchronized ( _list )
        {
            if ( _list.size () == 0 )
                return;
            
            for ( OID oid : _list.keySet () )
            {
                pdu.add ( new VariableBinding ( oid ) );
            }
        }
        
        _log.debug ( "Sending PDU..." );
        ResponseEvent response = connection.send ( target, pdu );
        _log.debug ( "Sending PDU...response!" );
        
        if ( response == null )
        {
            throw new Exception ( "No response" );
        }
        
        PDU reply = response.getResponse ();
        if ( reply == null )
        {
            throw new Exception ( "No reply" );
        }
        
        _log.debug ( "VB size: " + reply.size () );
        
        Vector<VariableBinding> vbs = reply.getVariableBindings ();
        
        synchronized ( _list )
        {
            for ( VariableBinding vb : vbs )
            {
                _log.debug ( "Variable: " + vb );
                
                OID oid = vb.getOid ();

                if ( !_list.containsKey ( oid ) )
                {
                    _log.info ( "Skipping unknown item: " + oid.toString() );
                    continue;
                }

                SNMPItem item = _list.get ( oid );
                item.readComplete ( vb );
            }
        }
    }
    
    private void setError ( Throwable t )
    {
        synchronized ( _list )
        {
            for ( SNMPItem item : _list.values () )
            {
                item.readFailed ( t );
            }
        }
    }
}
