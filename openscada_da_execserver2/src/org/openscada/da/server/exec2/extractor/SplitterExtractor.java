package org.openscada.da.server.exec2.extractor;

import java.util.List;
import java.util.StringTokenizer;

import org.openscada.da.server.exec2.command.ExecutionResult;

public class SplitterExtractor extends AbstractArrayExtractor
{

    private final String pattern;

    public SplitterExtractor ( final String id, final String pattern, final List<FieldMapping> fields )
    {
        super ( id, fields );
        this.pattern = pattern;
    }

    @Override
    protected String[] getFields ( final ExecutionResult result )
    {
        final StringTokenizer tok = new StringTokenizer ( result.getOutput (), this.pattern );
        final String[] fields = new String[tok.countTokens ()];

        int i = 0;
        while ( tok.hasMoreElements () )
        {
            fields[i] = tok.nextToken ();
            i++;
        }

        return fields;
    }

}
