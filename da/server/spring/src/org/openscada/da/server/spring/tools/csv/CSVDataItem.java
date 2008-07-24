/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2007 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.spring.tools.csv;

import java.util.EnumSet;

import org.openscada.core.InvalidOperationException;
import org.openscada.core.NotConvertableException;
import org.openscada.core.NullValueException;
import org.openscada.core.Variant;
import org.openscada.da.core.IODirection;
import org.openscada.da.server.common.DataItemInformationBase;
import org.openscada.da.server.common.chain.MemoryItemChained;
import org.openscada.da.server.common.chain.item.LevelAlarmChainItem;
import org.openscada.da.server.common.chain.item.ManualErrorOverrideChainItem;
import org.openscada.da.server.common.chain.item.ManualOverrideChainItem;
import org.openscada.da.server.common.chain.item.SumErrorChainItem;
import org.openscada.da.server.spring.Hive;
import org.openscada.da.server.spring.TestErrorChainItem;

public class CSVDataItem extends MemoryItemChained
{

    protected CSVControllerDataItem _controllerItem;
    
    public CSVDataItem ( Hive hive, String name, EnumSet<IODirection> ioDirection )
    {
        super ( new DataItemInformationBase ( name, ioDirection ) );
        this.addChainElement ( IODirection.INPUT, new TestErrorChainItem () );
        this.addChainElement ( IODirection.INPUT, new ManualOverrideChainItem ( hive ) );
        this.addChainElement ( IODirection.INPUT, new ManualErrorOverrideChainItem () );
        this.addChainElement ( IODirection.INPUT, new LevelAlarmChainItem ( hive ) );
        this.addChainElement ( IODirection.INPUT, new SumErrorChainItem ( hive ) );
    }

    @Override
    public Variant readValue () throws InvalidOperationException
    {
        if ( !isReadable () )
        {
            throw new InvalidOperationException ();
        }
        return super.readValue ();
    }

    @Override
    protected void writeCalculatedValue ( Variant value )
    {
        fireWrite ( value );
        if ( isReadable () )
        {
            super.writeCalculatedValue ( value );
        }
    }

    @Override
    public void writeValue ( Variant value ) throws InvalidOperationException, NullValueException, NotConvertableException
    {
        if ( !isWriteable () )
        {
            throw new InvalidOperationException ();
        }
        super.writeValue ( value );
    }

    private void fireWrite ( Variant value )
    {
        CSVControllerDataItem controllerItem = _controllerItem;
        if ( controllerItem != null )
        {
            controllerItem.handleWrite ( value );
        }
    }

    public void setController ( CSVControllerDataItem controllerItem )
    {
        _controllerItem = controllerItem;
    }

    public boolean isReadable ()
    {
        return this.getInformation ().getIODirection ().contains ( IODirection.INPUT );
    }

    public boolean isWriteable ()
    {
        return this.getInformation ().getIODirection ().contains ( IODirection.OUTPUT );
    }

}
