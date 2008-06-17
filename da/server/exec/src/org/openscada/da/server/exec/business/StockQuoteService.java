package org.openscada.da.server.exec.business;

import java.util.Collection;

import org.openscada.da.server.exec.domain.StockQuote;

public interface StockQuoteService
{
    public abstract Collection<StockQuote> getStockQuotes ( Collection<String> symbols );
}
