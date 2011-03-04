/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2011 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

import org.openscada.core.Variant;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.DataItemInputCommon;
import org.openscada.da.server.common.ItemListener;
import org.openscada.da.server.stock.domain.StockQuote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StockQuoteItem extends DataItemInputCommon implements StockQuoteListener
{

    private final static Logger logger = LoggerFactory.getLogger ( StockQuoteItem.class );

    private String symbol = null;

    private UpdateManager updateManager = null;

    public StockQuoteItem ( final String symbol, final UpdateManager updateManager )
    {
        super ( symbol );
        this.symbol = symbol;
        this.updateManager = updateManager;
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
        logger.warn ( String.format ( "Item %1$s suspended", getInformation ().getName () ) );
        this.updateManager.remove ( this.symbol );
    }

    public void wakeup ()
    {
        logger.warn ( String.format ( "Item %1$s woken up", getInformation ().getName () ) );
        this.updateManager.add ( this.symbol, this );
    }

    @Override
    public void update ( final StockQuote stockQuote )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
        Variant value = Variant.NULL;

        if ( stockQuote.getValue () != null )
        {
            value = new Variant ( stockQuote.getValue () );
            attributes.put ( "stock.error", null );
        }
        else
        {
            attributes.put ( "stock.error", Variant.valueOf ( stockQuote.getError () ) );
        }
        attributes.put ( "timestamp", Variant.valueOf ( stockQuote.getTimestamp ().getTimeInMillis () ) );

        updateData ( value, attributes, AttributeMode.UPDATE );
    }

}
