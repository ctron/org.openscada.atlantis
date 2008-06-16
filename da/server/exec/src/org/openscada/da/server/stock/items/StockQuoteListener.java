package org.openscada.da.server.stock.items;

import org.openscada.da.server.stock.domain.StockQuote;

public interface StockQuoteListener
{
    public abstract void update ( StockQuote stockQuote );
}
