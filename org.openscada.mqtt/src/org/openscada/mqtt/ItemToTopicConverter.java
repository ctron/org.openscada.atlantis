package org.openscada.mqtt;

public class ItemToTopicConverter implements NameConverter
{
    private final Character delimiter;

    private final String prefix;

    private final String writeValueSuffix;

    public ItemToTopicConverter ( final Character delimiter, final String prefix, final String writeValueSuffix )
    {
        this.delimiter = delimiter;
        this.prefix = prefix;
        this.writeValueSuffix = writeValueSuffix;
    }

    @Override
    public String convert ( final String name, final boolean writable )
    {
        if ( name == null )
        {
            return null;
        }
        return ( this.prefix == null ? "" : this.prefix ) + name.replace ( this.delimiter, '/' ) + ( writable && ( this.writeValueSuffix != null ) ? this.writeValueSuffix : "" );
    }

}
