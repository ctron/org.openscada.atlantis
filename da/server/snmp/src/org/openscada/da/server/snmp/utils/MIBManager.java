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

package org.openscada.da.server.snmp.utils;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.percederberg.mibble.Mib;
import net.percederberg.mibble.MibLoader;
import net.percederberg.mibble.MibLoaderException;
import net.percederberg.mibble.MibValueSymbol;
import net.percederberg.mibble.snmp.SnmpObjectType;

import org.apache.log4j.Logger;
import org.openscada.da.core.Variant;
import org.openscada.utils.collection.MapBuilder;
import org.openscada.utils.str.StringHelper;
import org.snmp4j.smi.OID;

public class MIBManager
{
    private static Logger _log = Logger.getLogger ( MIBManager.class );
    
    private static final MIBManager instance = new MIBManager ();
    public static MIBManager getInstance ()
    {
        return instance;
    }
    
    private MibLoader _loader = new MibLoader ();
    private Collection<Mib> _mibs = new LinkedList<Mib> ();
    
    public MIBManager ()
    {
        _log.debug ( "Loading MIBs..." );
        
        for ( String mib : getMIBs () )
        {
            try
            {
                _log.debug ( "Loading '" + mib + "'" );
                _mibs.add ( _loader.load ( mib ) );
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
            catch ( MibLoaderException e )
            {
                e.printStackTrace();
            }
        }
    }
    
    private String [] getMIBs ()
    {
        return new String [] { "SNMPv2-MIB", "IP-MIB", "TCP-MIB" };
    }
    
    public Collection<Mib> getAllMIBs ()
    {
        return _mibs;
    }
    
    private MibValueSymbol findBestMVS ( OID oid )
    {
        int bestLen = 0;
        MibValueSymbol bestMVS  = null;
        for ( Mib mib : _mibs )
        {
            MibValueSymbol mvs = mib.getSymbolByOid ( oid.toString () );
            
            if ( mvs == null )
                continue;
            
            int len = mvs.getValue ().toString ().length ();
            if ( len > bestLen )
            {
                bestMVS = mvs;
            }
        }
        return bestMVS;
    }
    
    public void fillAttributes ( OID oid, MapBuilder<String, Variant> attributes )
    {
        MibValueSymbol mvs = findBestMVS ( oid );
        if ( mvs == null )
        {
            attributes.put ( "snmp.oid.symbolic", new Variant ( oid.toString () ) );
            return;
        }

        if ( mvs != null )
        {
            attributes.put ( "snmp.mib.description", new Variant ( mvs.toString () ) );
            if ( mvs.getType () instanceof SnmpObjectType )
            {
                SnmpObjectType snmpObjectType = (SnmpObjectType)mvs.getType ();
                attributes.put ( "unit", new Variant ( snmpObjectType.getUnits () ) );
                if ( snmpObjectType.getStatus () != null )
                    attributes.put ( "snmp.mib.status", new Variant ( snmpObjectType.getStatus ().toString () ) );
            }
        }
        
        List<String> symbolic = new LinkedList<String> ();
        
        int pos = 0;
        MibValueSymbol currentMVS = mvs;
        while ( currentMVS != null )
        {
            symbolic.add ( 0, currentMVS.getName () );
            currentMVS = currentMVS.getParent ();
            pos++;
        }
        int [] oidValue = oid.getValue ();
        for ( int i = pos; i < oidValue.length; i++ )
        {
            symbolic.add ( String.valueOf ( oidValue[i] ) );
        }
        
        String symbolicName = StringHelper.join ( symbolic, "." );
        attributes.put ( "snmp.oid.symbolic", new Variant ( symbolicName ) );
        
    }
}
