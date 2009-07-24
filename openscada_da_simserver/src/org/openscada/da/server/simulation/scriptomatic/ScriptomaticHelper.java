package org.openscada.da.server.simulation.scriptomatic;

import org.apache.log4j.Logger;
import org.openscada.core.Variant;

public class ScriptomaticHelper
{
    private final static Logger logger = Logger.getLogger ( ScriptomaticHelper.class );

    private final Hive hive;

    public ScriptomaticHelper ( final Hive hive )
    {
        this.hive = hive;
    }

    public ScriptomaticItem getItem ( final String itemId )
    {
        return this.hive.getItem ( itemId );
    }

    public void updateData ( final ScriptomaticItem item, final Variant value )
    {
        logger.warn ( "Updating value: " + value );
        item.updateData ( value, null, null );
    }

    public void updateDataLong ( final ScriptomaticItem item, final Long number )
    {
        updateData ( item, new Variant ( number ) );
    }

    public void updateDataDouble ( final ScriptomaticItem item, final Double number )
    {
        updateData ( item, new Variant ( number ) );
    }
}
