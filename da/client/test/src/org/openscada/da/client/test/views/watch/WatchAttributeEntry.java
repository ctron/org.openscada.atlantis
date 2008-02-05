/**
 * 
 */
package org.openscada.da.client.test.views.watch;

import org.openscada.core.Variant;

class WatchAttributeEntry implements Comparable<WatchAttributeEntry>
{
    public String name;

    public Variant value;

    public WatchAttributeEntry ( String name, Variant value )
    {
        this.name = name;
        this.value = value;
    }

    public int compareTo ( WatchAttributeEntry o )
    {
        if ( o == null )
        {
            return 1;
        }
        return this.name.compareTo ( o.name );
    }
}