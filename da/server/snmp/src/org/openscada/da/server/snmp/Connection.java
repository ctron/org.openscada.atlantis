package org.openscada.da.server.snmp;

import java.io.IOException;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;

public class Connection
{
    private ConnectionInformation _connectionInformation = null;
    private Snmp _snmp = null;
    private TransportMapping _transport = null;
    private Address _address = null;
    
    public Connection ( ConnectionInformation connectionInformation )
    {
        _connectionInformation = new ConnectionInformation ( connectionInformation );
    }
    
    public void start () throws IOException
    {
        _address = GenericAddress.parse ( _connectionInformation.getAddress () );
        
        _transport = new DefaultUdpTransportMapping ();
        _snmp = new Snmp ( _transport );
        
        //MPv3 mpv3 = (MPv3)_snmp.getMessageProcessingModel ( MessageProcessingModel.MPv3 );
        USM usm = new USM ( SecurityProtocols.getInstance (), new OctetString ( MPv3.createLocalEngineID() ), 0 );
        SecurityModels.getInstance ().addSecurityModel ( usm );
        
        _snmp.listen ();
    }
    
    public Target createTarget ()
    {
        if ( _connectionInformation.getCommunity () != null )
        {
            CommunityTarget target = new CommunityTarget ( _address, new OctetString ( _connectionInformation.getCommunity () ) );
            target.setRetries ( 1 );
            target.setVersion ( SnmpConstants.version2c );
            target.setTimeout ( 5 * 1000 );
            return target;
        }
        return null;
    }
    
    public PDU createPDU ( Target target, int pduType )
    {
        return DefaultPDUFactory.createPDU ( target, pduType );
    }
   
    public ResponseEvent send ( Target target, PDU pdu ) throws IOException
    {
        return _snmp.send ( pdu, target );
    }
    
    public ResponseEvent sendGET ( OID oid ) throws IOException
    {
        Target target = createTarget ();
        PDU pdu = createPDU ( target, PDU.GET );
        pdu.add ( new VariableBinding ( oid ) );
        return send ( target, pdu );
    }
    
    public ResponseEvent sendGETNEXT ( OID oid ) throws IOException
    {
        Target target = createTarget ();
        PDU pdu = createPDU ( target, PDU.GETNEXT );
        pdu.add ( new VariableBinding ( oid ) );
        return send ( target, pdu );
    }
}
