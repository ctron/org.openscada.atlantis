package org.openscada.da.client.signalgenerator;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.openscada.da.client.base.item.DataItemHolder;
import org.openscada.da.client.signalgenerator.page.BooleanGeneratorPage;
import org.openscada.da.client.signalgenerator.page.GeneratorPage;

public class GeneratorView extends ViewPart
{
    public static final String VIEW_ID = "org.openscada.da.client.signalGenerator.GeneratorView";

    private CTabFolder tabFolder;

    private final List<GeneratorPage> pages = new LinkedList<GeneratorPage> ();

    private DataItemHolder item;

    public GeneratorView ()
    {
    }

    @Override
    public void createPartControl ( final Composite parent )
    {
        this.pages.add ( new BooleanGeneratorPage () );

        this.tabFolder = new CTabFolder ( parent, SWT.BOTTOM );

        for ( final GeneratorPage page : this.pages )
        {
            final CTabItem tabItem = new CTabItem ( this.tabFolder, SWT.NONE );
            final Composite tabComposite = new Composite ( this.tabFolder, SWT.NONE );
            tabComposite.setLayout ( new FillLayout () );
            page.createPage ( tabComposite );
            tabItem.setText ( page.getName () );
            tabItem.setControl ( tabComposite );

            if ( this.item != null )
            {
                page.setDataItem ( this.item );
            }
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
        this.item = item;
        if ( item != null )
        {
            setPartName ( String.format ( "Signal generator for: %s", item.getItemId (), item.getConnection ().getConnectionInformation () ) );
        }
        else
        {
            setPartName ( "No item set" );
        }

        for ( final GeneratorPage page : this.pages )
        {
            page.setDataItem ( item );
        }
    }

    @Override
    public void init ( final IViewSite site, final IMemento memento ) throws PartInitException
    {
        super.init ( site, memento );
        setDataItem ( DataItemHolder.loadFrom ( memento ) );
    }

    @Override
    public void saveState ( final IMemento memento )
    {
        super.saveState ( memento );
        if ( this.item != null )
        {
            this.item.saveTo ( memento );
        }
    }
}
