package org.openscada.da.client.test.generator;

import org.eclipse.swt.widgets.Composite;
import org.openscada.da.client.Connection;

public interface IGeneratorPage
{
    public String getName ();

    public void createPage ( Composite parent );

    public void setDataItem ( Connection connection, String itemId );

    public void dispose ();
}
