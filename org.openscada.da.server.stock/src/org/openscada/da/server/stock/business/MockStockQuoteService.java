package org.openscada.da.server.stock.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.openscada.da.server.stock.domain.StockQuote;

public class MockStockQuoteService implements StockQuoteService
{

    public Collection<StockQuote> getStockQuotes ( Collection<String> symbols )
    {
        List<StockQuote> result = new ArrayList<StockQuote> ( symbols.size () );

        Random r = new Random ( System.currentTimeMillis () );

        for ( String symbol : symbols )
        {
            StockQuote quote = new StockQuote ();
            double value = ( r.nextDouble () * 100.0 ) - 10.0;
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
