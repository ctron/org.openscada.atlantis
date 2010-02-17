package org.openscada.spring.client.state;

import org.apache.log4j.Logger;
import org.openscada.da.client.DataItemValue;
import org.openscada.spring.client.DataItem;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * A state source based on a boolean data item
 * @author Jens Reimann
 * @since 0.12.0
 */
public class DataItemSource implements StateSource, InitializingBean
{
    private static Logger logger = Logger.getLogger ( DataItemSource.class );

    private DataItem dataItem;

    private boolean invert = false;

    public void setInvert ( final boolean invert )
    {
        this.invert = invert;
    }

    public void setDataItem ( final DataItem dataItem )
    {
        this.dataItem = dataItem;
    }

    public void afterPropertiesSet () throws Exception
    {
        Assert.notNull ( this.dataItem, "'dataItem' must not be null" );
    }

    public Boolean getCurrentState ()
    {
        final DataItemValue value = this.dataItem.getSnapshotValue ();

        logger.debug ( "Got current state value: " + value );

        if ( value == null )
        {
            return null;
        }
        if ( !value.isConnected () )
        {
            return null;
        }
        if ( value.isError () )
        {
            return null;
        }
        if ( value.isAlarm () )
        {
            return false;
        }

        return this.invert ? !value.getValue ().asBoolean () : value.getValue ().asBoolean ();
    }
}
