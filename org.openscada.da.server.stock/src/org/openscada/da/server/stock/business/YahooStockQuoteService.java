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

package org.openscada.da.server.stock.business;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscada.da.server.stock.domain.StockQuote;
import org.openscada.utils.str.StringHelper;

public class YahooStockQuoteService implements StockQuoteService
{
    private String _baseUrl = "http://download.finance.yahoo.com/d/quotes.csv?&f=sl1d1t1c1ohgv&e=.csv&s=";

    public Collection<StockQuote> getStockQuotes ( final Collection<String> symbols )
    {
        try
        {
            final URL url = generateURL ( symbols );
            final URLConnection connection = url.openConnection ();
            connection.setDoInput ( true );
            return parseResult ( symbols, connection.getInputStream () );
        }
        catch ( final Throwable e )
        {
            return failAll ( symbols, e );
        }

    }

    private Collection<StockQuote> parseResult ( final Collection<String> symbols, final InputStream inputStream ) throws IOException
    {
        final LineNumberReader reader = new LineNumberReader ( new InputStreamReader ( inputStream, "UTF-8" ) );

        final Map<String, StockQuote> result = new HashMap<String, StockQuote> ();

        // first fill error
        for ( final String symbol : symbols )
        {
            final StockQuote quote = new StockQuote ();
            quote.setSymbol ( symbol );
            quote.setError ( "No update" );
            result.put ( symbol, quote );
        }

        String line;
        while ( ( line = reader.readLine () ) != null )
        {
            final String[] toks = line.split ( "," );

            final StockQuote quote = new StockQuote ();
            try
            {
                final String symbolTok = toks[0];
                quote.setSymbol ( symbolTok.replaceAll ( "(^\"|\"$)", "" ) );

                final String valueTok = toks[1];
                final String dateTok = toks[2].replaceAll ( "(^\"|\"$)", "" );
                final String timeTok = toks[3].replaceAll ( "(^\"|\"$)", "" );

                quote.setValue ( Double.valueOf ( valueTok ) );
                final Calendar c = Calendar.getInstance ();
                c.setTime ( new SimpleDateFormat ( "MM/dd/yyyy h:mma" ).parse ( dateTok + " " + timeTok ) );
                quote.setTimestamp ( c );
            }
            catch ( final Exception e )
            {
                quote.setError ( e.getMessage () );
                quote.setTimestamp ( Calendar.getInstance () );
            }

            if ( quote.getSymbol () != null )
            {
                result.put ( quote.getSymbol (), quote );
            }
        }

        return result.values ();
    }

    private Collection<StockQuote> failAll ( final Collection<String> symbols, final Throwable e )
    {
        final List<StockQuote> result = new ArrayList<StockQuote> ( symbols.size () );
        for ( final String symbol : symbols )
        {
            final StockQuote quote = new StockQuote ();
            quote.setSymbol ( symbol );
            quote.setError ( e.getMessage () );
        }
        return result;
    }

    private URL generateURL ( final Collection<String> symbols ) throws MalformedURLException
    {
        final String queryAdd = StringHelper.join ( symbols, "+" );
        return new URL ( this._baseUrl + queryAdd );
    }

    public void setBaseUrl ( final String baseUrl )
    {
        this._baseUrl = baseUrl;
    }

}
