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

package org.eclipse.scada.da.server.stock.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.eclipse.scada.da.server.stock.domain.StockQuote;

public class MockStockQuoteService implements StockQuoteService
{

    public Collection<StockQuote> getStockQuotes ( final Collection<String> symbols )
    {
        final List<StockQuote> result = new ArrayList<StockQuote> ( symbols.size () );

        final Random r = new Random ( System.currentTimeMillis () );

        for ( final String symbol : symbols )
        {
            final StockQuote quote = new StockQuote ();
            final double value = r.nextDouble () * 100.0 - 10.0;
            if ( value < 0 )
            {
                quote.setError ( "Failed to get value" );
            }
            else
            {
                quote.setValue ( value );
            }
            quote.setSymbol ( symbol );
            quote.setTimestamp ( Calendar.getInstance () );

            result.add ( quote );
        }

        return result;
    }

}
