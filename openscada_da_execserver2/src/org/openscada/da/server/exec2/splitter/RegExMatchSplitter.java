package org.openscada.da.server.exec2.splitter;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class RegExMatchSplitter implements Splitter
{
    private static Logger logger = Logger.getLogger ( RegExMatchSplitter.class );

    private final Pattern pattern;

    public RegExMatchSplitter ( final Pattern pattern )
    {
        this.pattern = pattern;
    }

    public SplitResult split ( final String inputBuffer )
    {
        SplitResult result = new SplitResult ();

        ArrayList<String> list = new ArrayList<String> ();

        boolean hadMatch = false;
        Matcher m = this.pattern.matcher ( inputBuffer );
        logger.debug ( "Matcher: " + m );

        while ( m.find () )
        {
            hadMatch = true;
            list.add ( m.group () );
        }

        if ( hadMatch )
        {
            result.setLines ( list.toArray ( new String[0] ) );
            result.setRemainingBuffer ( inputBuffer.substring ( m.end () ) );
            return result;
        }
        else
        {
            return null;
        }
    }
}
