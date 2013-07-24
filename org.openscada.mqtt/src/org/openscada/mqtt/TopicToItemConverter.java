package org.openscada.mqtt;

import java.util.regex.Pattern;

public class TopicToItemConverter implements NameConverter
{
    private final Character delimiter;

    private final String prefix;

    private final String writeValueSuffix;

    public TopicToItemConverter ( final Character delimiter, final String prefix, final String writeValueSuffix )
    {
        this.delimiter = delimiter;
        this.prefix = prefix;
        this.writeValueSuffix = writeValueSuffix;

    }

    @Override
    public String convert ( String name, final boolean writable )
    {
        if ( name == null )
        {
            return null;
        }
        if ( this.prefix != null )
        {
            name = name.replaceFirst ( Pattern.quote ( this.prefix ), "" );
        }
        name = name.replace ( '/', this.delimiter );
        if ( writable && ( this.writeValueSuffix != null ) )
        {
            name = name.replace ( Pattern.quote ( this.writeValueSuffix ), "" );
        }
        return name;
    }
}
