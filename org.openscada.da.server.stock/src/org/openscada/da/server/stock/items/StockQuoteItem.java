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

package org.openscada.da.server.stock.items;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openscada.core.Variant;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.DataItemInputCommon;
import org.openscada.da.server.common.ItemListener;
import org.openscada.da.server.stock.domain.StockQuote;

public class StockQuoteItem extends DataItemInputCommon implements StockQuoteListener
{
    private static Logger _log = Logger.getLogger ( StockQuoteItem.class );
    
    private String _symbol = null;
    private UpdateManager _updateManager = null;
    
    public StockQuoteItem ( String symbol, UpdateManager updateManager )
    {
        super ( symbol );
        _symbol = symbol;
        _updateManager = updateManager;
    }
    
    @Override
    public void setListener ( ItemListener listener )
    {
        super.setListener ( listener );
        if ( listener != null )
        {
            wakeup ();
        }
        else
        {
            suspend ();
        }
    }

    public void suspend ()
    {
       _log.warn ( String.format ( "Item %1$s suspended", getInformation ().getName () ) );
       _updateManager.remove ( _symbol );
    }

    public void wakeup ()
    {
        _log.warn ( String.format ( "Item %1$s woken up", getInformation ().getName () ) );
        _updateManager.add ( _symbol, this );
    }

    public void update ( StockQuote stockQuote )
    {
        Map<String, Variant> attributes = new HashMap<String, Variant> ();
        Variant value = new Variant ();
        
        if ( stockQuote.getValue () != null )
        {
            value = new Variant ( stockQuote.getValue () );
            attributes.put ( "stock.error", null );
        }
        else
        {
            attributes.put ( "stock.error", new Variant ( stockQuote.getError () ) );
        }
        attributes.put ( "timestamp", new Variant ( stockQuote.getTimestamp ().getTimeInMillis () ) );
        
        updateData ( value, attributes, AttributeMode.UPDATE );
    }

}
