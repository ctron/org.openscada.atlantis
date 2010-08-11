package org.openscada.spring.client.value;

import org.openscada.da.client.DataItem;
import org.openscada.da.client.DataItemValue;
import org.openscada.spring.client.Connection;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * A value source based on OpenSCADA DA items
 * @author Jens Reimann
 *
 */
public class ItemEventValueSource extends AbstractBaseValueSource implements ValueSource, DisposableBean, InitializingBean
{
    private Connection connection;

    private String itemName;

    private DataItem dataItem;

    public void afterPropertiesSet () throws Exception
    {
        Assert.notNull ( this.connection, "'connection' must not be null" );
        Assert.hasText ( this.itemName, "'itemName' must be set" );

        this.dataItem = new DataItem ( this.itemName, this.connection.getItemManager () );
    }

    public void destroy () throws Exception
    {
        this.dataItem.unregister ();
    }

    public DataItemValue getValue ()
    {
        return this.dataItem.getSnapshotValue ();
    }

    public void setConnection ( final Connection connection )
    {
        this.connection = connection;
    }

    public void setItemName ( final String itemName )
    {
        this.itemName = itemName;
    }

}
