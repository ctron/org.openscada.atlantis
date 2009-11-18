package org.openscada.da.client.dataitem.details.extra.part;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.openscada.da.client.dataitem.details.part.AbstractBaseDetailsPart;

public abstract class AbstractBaseDraw2DDetailsPart extends AbstractBaseDetailsPart
{
    private Canvas canvas;

    public void createPart ( final Composite parent )
    {
        parent.setLayout ( new org.eclipse.swt.layout.GridLayout ( 1, false ) );

        this.canvas = new Canvas ( parent, SWT.NONE );
        this.canvas.setLayoutData ( new org.eclipse.swt.layout.GridData ( SWT.FILL, SWT.FILL, true, true ) );
        final LightweightSystem lws = new LightweightSystem ( this.canvas );

        lws.setContents ( createRoot () );
    }

    @Override
    public void dispose ()
    {
        this.canvas.dispose ();
        this.canvas = null;

        super.dispose ();
    }

    protected abstract IFigure createRoot ();
}
