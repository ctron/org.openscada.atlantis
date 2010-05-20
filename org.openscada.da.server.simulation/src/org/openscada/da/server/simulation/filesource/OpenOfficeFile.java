/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.da.server.simulation.filesource;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom.Namespace;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

/**
 * <p>Reads item definitions from given OpenOffice file.</p>
 * <p>the given file has to have following structure:</p>
 * <ul>
 *   <li><strong>Driver</strong> - (A/1) - name of driver, optional with portnumber, separated by colon</li>
 *   <li><strong>Data Item</strong> - (B/2) - name of the actual data item</li>
 *   <li><strong>Data Type</strong> - (C/3) - only documentation</li>
 *   <li><strong>Unit</strong> - (D/4) - only documentation</li>
 *   <li><strong>Description</strong> - (C/5) - only documentation</li>
 *   <li><strong>Timestamp</strong> - (D/6) - only documentation</li>
 *   <li><strong>Error</strong> - (E/7) - only documentation</li>
 *   <li><strong>Alarm</strong> - (F/8) - only documentation</li>
 *   <li><strong>LL</strong> - (G/9) - only documentation</li>  
 *   <li><strong>LL-Type</strong> - (H/10) - only documentation</li> 
 *   <li><strong>L</strong> - (I/11) - only documentation</li>
 *   <li><strong>L-Type</strong> - (J/12) - only documentation</li>  
 *   <li><strong>H</strong> - (K/13) - only documentation</li>
 *   <li><strong>H-Type</strong> - (L/14) - only documentation</li>
 *   <li><strong>HH</strong> - (M/15) - only documentation</li>
 *   <li><strong>HH-Type</strong> - (M/16) - only documentation</li> 
 *   <li><strong>Boolean</strong> - (N/17) - only documentation</li>
 *   <li><strong>Boolean-Type</strong> - (O/18) - only documentation</li>
 *   <li><strong>Function</strong> - (P/19) - </li>
 * </ul>
 * <p>The simulator expects to find the cell B2 to have the content
 * <strong>Data Item</strong>, or else this file will be considered
 * as invalid.</p>
 * <p>The actual DataItem Definitions are supposed to start in line 3.
 * Lines with empty names are not considered.</p>
 * 
 * @author Juergen Rose &lt;juergen.rose@inavare.net&gt;
 * @see BaseFile
 */
public class OpenOfficeFile extends BaseFile
{

    private static Logger logger = Logger.getLogger ( OpenOfficeFile.class );

    /**
     * @see org.openscada.da.server.simulator.BaseFile#BaseFile(File, File, Integer)
     */
    public OpenOfficeFile ( final File file, final File js, final HiveBuilder hiveBuilder ) throws Exception
    {
        super ( file, js, hiveBuilder );
    }

    /**
     * @see org.openscada.da.server.simulator.BaseFile#buildServers()
     */
    @Override
    public void configureHive () throws Exception
    {
        final SpreadSheet spreadSheet = SpreadSheet.createFromFile ( getFile () );
        final Sheet sheet = spreadSheet.getSheet ( 0 );
        final Namespace ns = sheet.getElement ().getNamespace ( "table" );
        final String sheetName = sheet.getElement ().getAttributeValue ( "name", ns );
        if ( !"Data Item".equalsIgnoreCase ( (String)sheet.getValueAt ( "B2" ) ) )
        {
            logger.warn ( "Sheet " + sheetName + " doesn't contain valid items" );
            return;
        }
        final int rows = sheet.getRowCount ();
        final int startRow = 2;
        for ( int y = startRow; y < rows; y++ )
        {
            String serverName = (String)sheet.getValueAt ( 0, y );
            final String itemName = (String)sheet.getValueAt ( 1, y );
            if ( serverName.contains ( ":" ) )
            {
                final String[] n = serverName.split ( ":" );
                serverName = n[0];
            }
            if ( !getHiveBuilder ().getName ().equals ( serverName ) )
            {
                continue;
            }
            if ( itemName.trim ().length () == 0 )
            {
                continue;
            }
            final String script = (String)sheet.getValueAt ( 18, y );
            ItemDefinition idef = null;
            if ( script == null || script.trim ().length () == 0 )
            {
                idef = getHiveBuilder ().addInputOutputItem ( itemName );
            }
            else
            {
                idef = getHiveBuilder ().addInputOutputItem ( itemName, script );
            }
            // Unit
            final String unit = (String)sheet.getValueAt ( 3, y );
            if ( unit != null )
            {
                idef.addAttr ( "unit", unit );
            }
            // Description
            final String description = (String)sheet.getValueAt ( 4, y );
            if ( description != null )
            {
                idef.addAttr ( "description", description );
            }
            // Error
            final String error = (String)sheet.getValueAt ( 6, y );
            if ( "x".equalsIgnoreCase ( error ) )
            {
                idef.addAttr ( "error", false );
            }
            // Alarm
            final String alarm = (String)sheet.getValueAt ( 7, y );
            if ( "x".equalsIgnoreCase ( alarm ) )
            {
                idef.addAttr ( "alarm", false );
            }
            // LL
            try
            {
                final Number alarmLL = (Number)sheet.getValueAt ( alarm_ll_col, y );
                idef.addAttr ( "LL", alarmLL );
            }
            catch ( final ClassCastException e )
            {
            }
            // L
            try
            {
                final Number alarmL = (Number)sheet.getValueAt ( alarm_l_col, y );
                idef.addAttr ( "L", alarmL );
            }
            catch ( final ClassCastException e )
            {
            }
            // H
            try
            {
                final Number alarmH = (Number)sheet.getValueAt ( alarm_h_col, y );
                idef.addAttr ( "H", alarmH );
            }
            catch ( final ClassCastException e )
            {
            }
            // HH
            try
            {
                final Number alarmHH = (Number)sheet.getValueAt ( alarm_hh_col, y );
                idef.addAttr ( "HH", alarmHH );
            }
            catch ( final ClassCastException e )
            {
            }
        }
    }

    public static Map<String, Integer> listDrivers ( final File file, final Integer defaultPort ) throws Exception
    {
        final Map<String, Integer> result = new HashMap<String, Integer> ();
        final SpreadSheet spreadSheet = SpreadSheet.createFromFile ( file );
        final Sheet sheet = spreadSheet.getSheet ( 0 );
        final Namespace ns = sheet.getElement ().getNamespace ( "table" );
        final String sheetName = sheet.getElement ().getAttributeValue ( "name", ns );
        if ( !"Data Item".equalsIgnoreCase ( (String)sheet.getValueAt ( "B2" ) ) )
        {
            logger.warn ( "Sheet " + sheetName + " doesn't contain valid items" );
            return result;
        }
        final int rows = sheet.getRowCount ();
        final int startRow = 2;
        Integer maxPort = null;
        for ( int y = startRow; y < rows; y++ )
        {
            Integer currentPort = null;
            String serverName = (String)sheet.getValueAt ( 0, y );
            final String itemName = (String)sheet.getValueAt ( 1, y );
            if ( serverName.contains ( ":" ) )
            {
                final String[] n = serverName.split ( ":" );
                serverName = n[0];
                currentPort = Integer.valueOf ( n[1] );
            }
            if ( itemName.trim ().length () == 0 )
            {
                continue;
            }
            if ( !result.keySet ().contains ( serverName ) )
            {
                if ( currentPort == null )
                {
                    if ( maxPort == null )
                    {
                        currentPort = defaultPort;
                    }
                    else
                    {
                        currentPort = maxPort + 1;
                    }
                    maxPort = currentPort;
                }
                result.put ( serverName, currentPort );
            }
        }
        return result;
    }
}
