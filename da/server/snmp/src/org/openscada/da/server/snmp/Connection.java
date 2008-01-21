/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

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
        this._connectionInformation = new ConnectionInformation ( connectionInformation );
    }

    public void start () throws IOException
    {
        this._address = GenericAddress.parse ( this._connectionInformation.getAddress () );

        this._transport = new DefaultUdpTransportMapping ();
        this._snmp = new Snmp ( this._transport );

        //MPv3 mpv3 = (MPv3)_snmp.getMessageProcessingModel ( MessageProcessingModel.MPv3 );
        USM usm = new USM ( SecurityProtocols.getInstance (), new OctetString ( MPv3.createLocalEngineID () ), 0 );
        SecurityModels.getInstance ().addSecurityModel ( usm );

        this._snmp.listen ();
    }

    public void stop () throws IOException
    {
        if ( this._snmp != null )
        {
            this._snmp.close ();
            this._snmp = null;
        }
    }

    public Target createTarget ()
    {
        switch ( this._connectionInformation.getVersion () )
        {
        case V1:
            if ( this._connectionInformation.getCommunity () != null )
            {
                CommunityTarget target = new CommunityTarget ( this._address, new OctetString ( this._connectionInformation.getCommunity () ) );
                target.setRetries ( 1 );
                target.setVersion ( SnmpConstants.version1 );
                target.setTimeout ( 5 * 1000 );
                return target;
            }
            break;
        case V2C:
            if ( this._connectionInformation.getCommunity () != null )
            {
                CommunityTarget target = new CommunityTarget ( this._address, new OctetString ( this._connectionInformation.getCommunity () ) );
                target.setRetries ( 1 );
                target.setVersion ( SnmpConstants.version2c );
                target.setTimeout ( 5 * 1000 );
                return target;
            }
            break;
        }

        return null;
    }

    public PDU createPDU ( Target target, int pduType )
    {
        return DefaultPDUFactory.createPDU ( target, pduType );
    }

    public ResponseEvent send ( Target target, PDU pdu ) throws IOException
    {
        return this._snmp.send ( pdu, target );
    }

    public ResponseEvent sendGET ( OID oid ) throws IOException
    {
        Target target = this.createTarget ();
        PDU pdu = this.createPDU ( target, PDU.GET );
        pdu.add ( new VariableBinding ( oid ) );
        return this.send ( target, pdu );
    }

    public ResponseEvent sendGETNEXT ( OID oid ) throws IOException
    {
        Target target = this.createTarget ();
        PDU pdu = this.createPDU ( target, PDU.GETNEXT );
        pdu.add ( new VariableBinding ( oid ) );
        return this.send ( target, pdu );
    }
}
