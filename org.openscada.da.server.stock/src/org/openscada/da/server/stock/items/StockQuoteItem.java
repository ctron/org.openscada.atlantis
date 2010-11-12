/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

    public StockQuoteItem ( final String symbol, final UpdateManager updateManager )
    {
        super ( symbol );
        this._symbol = symbol;
        this._updateManager = updateManager;
    }

    @Override
    public void setListener ( final ItemListener listener )
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
        this._updateManager.remove ( this._symbol );
    }

    public void wakeup ()
    {
        _log.warn ( String.format ( "Item %1$s woken up", getInformation ().getName () ) );
        this._updateManager.add ( this._symbol, this );
    }

    public void update ( final StockQuote stockQuote )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
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
