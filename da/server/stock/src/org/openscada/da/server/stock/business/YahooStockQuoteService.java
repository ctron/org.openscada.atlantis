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
    
    public Collection<StockQuote> getStockQuotes ( Collection<String> symbols )
    {
        try
        {
            URL url = generateURL ( symbols );
            URLConnection connection = url.openConnection ();
            connection.setDoInput ( true );
            return parseResult ( symbols, connection.getInputStream () );
        }
        catch ( Throwable e )
        {
            return failAll ( symbols, e );
        }

    }

    private Collection<StockQuote> parseResult ( Collection<String> symbols, InputStream inputStream ) throws IOException
    {
        LineNumberReader reader = new LineNumberReader ( new InputStreamReader ( inputStream, "UTF-8" ) );

        Map<String, StockQuote> result = new HashMap<String, StockQuote> ();

        // first fill error
        for ( String symbol : symbols )
        {
            StockQuote quote = new StockQuote ();
            quote.setSymbol ( symbol );
            quote.setError ( "No update" );
            result.put ( symbol, quote );
        }

        String line;
        while ( ( line = reader.readLine () ) != null )
        {
            String[] toks = line.split ( "," );

            StockQuote quote = new StockQuote ();
            try
            {
                String symbolTok = toks[0];
                quote.setSymbol ( symbolTok.replaceAll ( "(^\"|\"$)", "" ) );
                
                String valueTok = toks[1];
                String dateTok = toks[2].replaceAll ( "(^\"|\"$)", "" );
                String timeTok = toks[3].replaceAll ( "(^\"|\"$)", "" );
                
                quote.setValue ( Double.valueOf ( valueTok ) );
                Calendar c = Calendar.getInstance ();
                c.setTime ( new SimpleDateFormat ("MM/dd/yyyy h:mma").parse ( dateTok + " " + timeTok ) );
                quote.setTimestamp ( c );
            }
            catch ( Exception e )
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

    private Collection<StockQuote> failAll ( Collection<String> symbols, Throwable e )
    {
        List<StockQuote> result = new ArrayList<StockQuote> ( symbols.size () );
        for ( String symbol : symbols )
        {
            StockQuote quote = new StockQuote ();
            quote.setSymbol ( symbol );
            quote.setError ( e.getMessage () );
        }
        return result;
    }

    private URL generateURL ( Collection<String> symbols ) throws MalformedURLException
    {
        String queryAdd = StringHelper.join ( symbols, "+" );
        return new URL ( _baseUrl + queryAdd );
    }

    public void setBaseUrl ( String baseUrl )
    {
        _baseUrl = baseUrl;
    }

}
