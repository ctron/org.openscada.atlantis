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

import java.io.File;
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
import org.openscada.core.Variant;
import org.openscada.da.snmp.configuration.MibsType;
import org.openscada.utils.collection.MapBuilder;
import org.openscada.utils.str.StringHelper;
import org.snmp4j.smi.OID;

public class MIBManager
{
    private static Logger _log = Logger.getLogger ( MIBManager.class );

    private final MibLoader _loader = new MibLoader ();

    private final Collection<Mib> _mibs = new LinkedList<Mib> ();

    public MIBManager ( final MibsType mibs )
    {
        _log.debug ( "Loading MIBs..." );

        if ( mibs.getMibDirList () != null )
        {
            for ( final String dir : mibs.getMibDirList () )
            {
                this._loader.addDir ( new File ( dir ) );
            }
        }

        if ( mibs.getRecursiveMibDirList () != null )
        {
            for ( final String dir : mibs.getRecursiveMibDirList () )
            {
                this._loader.addAllDirs ( new File ( dir ) );
            }
        }

        if ( mibs.getStaticMibNameList () != null )
        {
            for ( final String mib : mibs.getStaticMibNameList () )
            {
                try
                {
                    _log.debug ( "Loading '" + mib + "'" );
                    this._mibs.add ( this._loader.load ( mib ) );
                }
                catch ( final IOException e )
                {
                    _log.warn ( String.format ( "Failed to load mib '%s'", mib ), e );
                }
                catch ( final MibLoaderException e )
                {
                    _log.warn ( String.format ( "Failed to load mib '%s'", mib ), e );
                }
            }
        }
    }

    /**
     * get all loaded mibs
     * @return all loaded mibs
     */
    public Collection<Mib> getAllMIBs ()
    {
        return this._mibs;
    }

    private MibValueSymbol findBestMVS ( final OID oid )
    {
        int bestLen = 0;
        MibValueSymbol bestMVS = null;
        for ( final Mib mib : this._mibs )
        {
            final MibValueSymbol mvs = mib.getSymbolByOid ( oid.toString () );

            if ( mvs == null )
            {
                continue;
            }

            final int len = mvs.getValue ().toString ().length ();
            if ( len > bestLen )
            {
                bestMVS = mvs;
                bestLen = len;
            }
        }
        return bestMVS;
    }

    public void fillAttributes ( final OID oid, final MapBuilder<String, Variant> attributes )
    {
        final MibValueSymbol mvs = findBestMVS ( oid );
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
                final SnmpObjectType snmpObjectType = (SnmpObjectType)mvs.getType ();
                attributes.put ( "unit", new Variant ( snmpObjectType.getUnits () ) );
                if ( snmpObjectType.getStatus () != null )
                {
                    attributes.put ( "snmp.mib.status", new Variant ( snmpObjectType.getStatus ().toString () ) );
                }
            }
        }

        final List<String> symbolic = new LinkedList<String> ();

        int pos = 0;
        MibValueSymbol currentMVS = mvs;
        while ( currentMVS != null )
        {
            symbolic.add ( 0, currentMVS.getName () );
            currentMVS = currentMVS.getParent ();
            pos++;
        }
        final int[] oidValue = oid.getValue ();
        for ( int i = pos; i < oidValue.length; i++ )
        {
            symbolic.add ( String.valueOf ( oidValue[i] ) );
        }

        final String symbolicName = StringHelper.join ( symbolic, "." );
        attributes.put ( "snmp.oid.symbolic", new Variant ( symbolicName ) );

    }
}
