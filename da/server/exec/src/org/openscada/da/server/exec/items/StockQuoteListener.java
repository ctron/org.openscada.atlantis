package org.openscada.da.server.exec.items;

import org.openscada.da.server.exec.domain.StockQuote;

public interface StockQuoteListener
{
    public abstract void update ( StockQuote stockQuote );
}
