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

package org.openscada.da.server.stock.items;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.openscada.da.server.stock.business.StockQuoteService;
import org.openscada.da.server.stock.domain.StockQuote;

public class UpdateManager
{
    private final Map<String, StockQuoteListener> _registeredSymbols = new HashMap<String, StockQuoteListener> ();

    private StockQuoteService _stockQuoteService = null;

    public synchronized void update ()
    {
        if ( !this._registeredSymbols.isEmpty () )
        {
            fireEvents ( this._stockQuoteService.getStockQuotes ( this._registeredSymbols.keySet () ) );
        }
    }

    public synchronized void add ( final String symbol, final StockQuoteListener listener )
    {
        this._registeredSymbols.put ( symbol, listener );
        updateNow ( symbol );
    }

    protected void updateNow ( final String symbol )
    {
        fireEvents ( this._stockQuoteService.getStockQuotes ( Arrays.asList ( new String[] { symbol } ) ) );
    }

    protected void fireEvents ( final Collection<StockQuote> stockQuotes )
    {
        for ( final StockQuote stockQuote : stockQuotes )
        {
            final StockQuoteListener listener = this._registeredSymbols.get ( stockQuote.getSymbol () );
            try
            {
                listener.update ( stockQuote );
            }
            catch ( final Throwable e )
            {
            }
        }
    }

    public synchronized void remove ( final String symbol )
    {
        this._registeredSymbols.remove ( symbol );
    }

    public void setStockQuoteService ( final StockQuoteService stockQuoteService )
    {
        this._stockQuoteService = stockQuoteService;
    }
}
