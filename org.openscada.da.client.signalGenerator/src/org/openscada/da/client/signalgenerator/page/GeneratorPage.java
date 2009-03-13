package org.openscada.da.client.signalgenerator.page;

import org.eclipse.swt.widgets.Composite;
import org.openscada.da.client.base.item.DataItemHolder;

public interface GeneratorPage
{
    public String getName ();

    public void createPage ( Composite parent );

    public void dispose ();

    public void setDataItem ( DataItemHolder item );
}
