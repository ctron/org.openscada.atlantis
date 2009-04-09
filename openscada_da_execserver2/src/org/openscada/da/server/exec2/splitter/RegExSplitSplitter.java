package org.openscada.da.server.exec2.splitter;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class RegExSplitSplitter implements Splitter
{

    private final Pattern pattern;

    public RegExSplitSplitter ( final Pattern pattern )
    {
        this.pattern = pattern;
    }

    public SplitResult split ( final String inputBuffer )
    {
        SplitResult result = new SplitResult ();

        List<String> list = Arrays.asList ( this.pattern.split ( inputBuffer ) );

        if ( list.size () >= 2 )
        {
            String last = list.remove ( list.size () );
            result.setLines ( list.toArray ( new String[0] ) );
            result.setRemainingBuffer ( last );
        }
        else
        {
            result.setRemainingBuffer ( inputBuffer );
        }

        return result;
    }
}
