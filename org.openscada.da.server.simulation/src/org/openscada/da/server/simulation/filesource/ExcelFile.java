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

import jxl.Cell;
import jxl.CellType;
import jxl.LabelCell;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;

import org.apache.log4j.Logger;

/**
 * <p>Reads item definitions from given Excel file.</p>
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
public class ExcelFile extends BaseFile
{
    private static Logger logger = Logger.getLogger ( ExcelFile.class );

    /**
     * @see org.openscada.da.server.simulator.BaseFile#BaseFile(File, File, Integer)
     */
    public ExcelFile ( final File file, final File js, final HiveBuilder hiveBuilder ) throws Exception
    {
        super ( file, js, hiveBuilder );
    }

    /**
     * @see org.openscada.da.server.simulator.BaseFile#buildServers()
     */
    @Override
    public void configureHive () throws Exception
    {
        final Workbook wb = Workbook.getWorkbook ( getFile () );
        final Sheet sheet = wb.getSheet ( 0 );
        logger.info ( sheet.getName () );
        if ( !"Data Item".equalsIgnoreCase ( sheet.getCell ( "B2" ).getContents () ) )
        {
            logger.warn ( "Sheet " + sheet.getName () + " doesn't contain valid items" );
            return;
        }
        final int rows = sheet.getRows ();
        final int startRow = 2;
        for ( int y = startRow; y < rows; y++ )
        {
            String serverName = sheet.getCell ( 0, y ).getContents ();
            final String itemName = sheet.getCell ( 1, y ).getContents ();
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
            final String script = sheet.getCell ( 18, y ).getContents ();
            ItemDefinition idef = null;
            if ( script == null || script.trim ().length () == 0 )
            {
                idef = getHiveBuilder ().addInputOutputItem ( itemName );
            }
            else
            {
                idef = getHiveBuilder ().addInputOutputItem ( itemName, script );
            }
            Cell cell = null;
            // Unit
            cell = sheet.getCell ( 3, y );
            if ( cell != null && cell.getType () == CellType.LABEL )
            {
                idef.addAttr ( "unit", ( (LabelCell)cell ).getString () );
            }
            // Description
            cell = sheet.getCell ( 4, y );
            if ( cell != null && cell.getType () == CellType.LABEL )
            {
                idef.addAttr ( "description", ( (LabelCell)cell ).getString () );
            }
            // Error
            final String error = sheet.getCell ( 6, y ).getContents ();
            if ( "x".equalsIgnoreCase ( error ) )
            {
                idef.addAttr ( "error", false );
            }
            // Alarm
            final String alarm = sheet.getCell ( 7, y ).getContents ();
            if ( "x".equalsIgnoreCase ( alarm ) )
            {
                idef.addAttr ( "alarm", false );
            }
            // LL
            cell = sheet.getCell ( alarm_ll_col, y );
            if ( cell != null && cell.getType () == CellType.NUMBER )
            {
                idef.addAttr ( "LL", ( (NumberCell)cell ).getValue () );
            }
            // L
            cell = sheet.getCell ( alarm_l_col, y );
            if ( cell != null && cell.getType () == CellType.NUMBER )
            {
                idef.addAttr ( "L", ( (NumberCell)cell ).getValue () );
            }
            // H
            cell = sheet.getCell ( alarm_h_col, y );
            if ( cell != null && cell.getType () == CellType.NUMBER )
            {
                idef.addAttr ( "H", ( (NumberCell)cell ).getValue () );
            }
            // HH
            cell = sheet.getCell ( alarm_hh_col, y );
            if ( cell != null && cell.getType () == CellType.NUMBER )
            {
                idef.addAttr ( "HH", ( (NumberCell)cell ).getValue () );
            }
        }
    }

    public static Map<String, Integer> listDrivers ( final File file, final Integer defaultPort ) throws Exception
    {
        final Map<String, Integer> result = new HashMap<String, Integer> ();

        final Workbook wb = Workbook.getWorkbook ( file );
        final Sheet sheet = wb.getSheet ( 0 );
        logger.info ( sheet.getName () );
        if ( !"Data Item".equalsIgnoreCase ( sheet.getCell ( "B2" ).getContents () ) )
        {
            logger.warn ( "Sheet " + sheet.getName () + " doesn't contain valid items" );
            return result;
        }
        final int rows = sheet.getRows ();
        final int startRow = 2;
        Integer maxPort = null;
        for ( int y = startRow; y < rows; y++ )
        {
            Integer currentPort = null;
            String serverName = sheet.getCell ( 0, y ).getContents ();
            final String itemName = sheet.getCell ( 1, y ).getContents ();
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
