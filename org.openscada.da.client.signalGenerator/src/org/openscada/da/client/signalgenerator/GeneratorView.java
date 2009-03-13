package org.openscada.da.client.signalgenerator;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.openscada.da.client.base.item.DataItemHolder;
import org.openscada.da.client.signalgenerator.page.BooleanGeneratorPage;
import org.openscada.da.client.signalgenerator.page.GeneratorPage;

public class GeneratorView extends ViewPart
{
    public static final String VIEW_ID = "org.openscada.da.client.signalGenerator.GeneratorView";

    private CTabFolder tabFolder;

    private final List<GeneratorPage> pages = new LinkedList<GeneratorPage> ();

    public GeneratorView ()
    {
        this.pages.add ( new BooleanGeneratorPage () );
    }

    @Override
    public void createPartControl ( final Composite parent )
    {
        this.tabFolder = new CTabFolder ( parent, SWT.BOTTOM );

        for ( final GeneratorPage page : this.pages )
        {
            final CTabItem tabItem = new CTabItem ( this.tabFolder, SWT.NONE );
            final Composite tabComposite = new Composite ( this.tabFolder, SWT.NONE );
            tabComposite.setLayout ( new FillLayout () );
            page.createPage ( tabComposite );
            tabItem.setText ( page.getName () );
            tabItem.setControl ( tabComposite );
        }

        this.tabFolder.setSelection ( 0 );
    }

    @Override
    public void dispose ()
    {
        for ( final GeneratorPage page : this.pages )
        {
            page.dispose ();
        }
        super.dispose ();
    }

    @Override
    public void setFocus ()
    {
        this.tabFolder.setFocus ();
    }

    public void setDataItem ( final DataItemHolder item )
    {
        for ( final GeneratorPage page : this.pages )
        {
            page.setDataItem ( item );
        }
    }

}
