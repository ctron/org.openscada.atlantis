package org.openscada.da.server.exec2.extractor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openscada.da.server.exec2.command.ExecutionResult;

public class RegExExtractor extends AbstractArrayExtractor
{

    private final Pattern pattern;

    private final boolean requireFullMatch;

    public RegExExtractor ( final String id, final Pattern pattern, final boolean requireFullMatch, final List<FieldMapping> groups )
    {
        super ( id, groups );
        this.pattern = pattern;
        this.requireFullMatch = requireFullMatch;
    }

    @Override
    protected String[] getFields ( final ExecutionResult result )
    {
        final Matcher m = this.pattern.matcher ( result.getOutput () );
        if ( this.requireFullMatch )
        {
            if ( !m.matches () )
            {
                throw new RuntimeException ( "Failed to match input" );
            }
            return convertToResult ( m );
        }
        else
        {
            if ( !m.find () )
            {
                throw new RuntimeException ( "Failed to match input" );
            }
            return convertToResult ( m );
        }
    }

    private String[] convertToResult ( final Matcher m )
    {
        final String[] result = new String[m.groupCount () + 1];
        for ( int i = 0; i < result.length; i++ )
        {
            result[i] = m.group ( i );
        }
        return result;
    }

}
