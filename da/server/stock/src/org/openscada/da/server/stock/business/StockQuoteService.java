package org.openscada.da.server.stock.business;

import java.util.Collection;

import org.openscada.da.server.stock.domain.StockQuote;

public interface StockQuoteService
{
    public abstract Collection<StockQuote> getStockQuotes ( Collection<String> symbols );
}
