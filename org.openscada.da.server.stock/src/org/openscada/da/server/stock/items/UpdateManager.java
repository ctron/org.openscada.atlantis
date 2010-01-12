package org.openscada.da.server.stock.items;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.openscada.da.server.stock.business.StockQuoteService;
import org.openscada.da.server.stock.domain.StockQuote;

public class UpdateManager
{
    private Map<String, StockQuoteListener> _registeredSymbols = new HashMap<String, StockQuoteListener> ();

    private StockQuoteService _stockQuoteService = null;
    
    public synchronized void update ()
    {
        if ( !_registeredSymbols.isEmpty () )
        {
            fireEvents ( _stockQuoteService.getStockQuotes ( _registeredSymbols.keySet () ) );
        }
    }

    public synchronized void add ( String symbol, StockQuoteListener listener )
    {
        _registeredSymbols.put ( symbol, listener );
        updateNow ( symbol );
    }

    protected void updateNow ( String symbol )
    {
        fireEvents ( _stockQuoteService.getStockQuotes ( Arrays.asList ( new String[] { symbol } ) ) );
    }

    protected void fireEvents ( Collection<StockQuote> stockQuotes )
    {
        for ( StockQuote stockQuote : stockQuotes )
        {
            StockQuoteListener listener = _registeredSymbols.get ( stockQuote.getSymbol () );
            try
            {
                listener.update ( stockQuote );
            }
            catch ( Throwable e )
            {
            }
        }
    }

    public synchronized void remove ( String symbol )
    {
        _registeredSymbols.remove ( symbol );
    }

    public void setStockQuoteService ( StockQuoteService stockQuoteService )
    {
        _stockQuoteService = stockQuoteService;
    }
}
