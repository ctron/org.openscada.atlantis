package org.openscada.da.client.dataitem.details.extra.part;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.RoundedRectangle;
import org.openscada.core.Variant;

public class InputScaleDetails extends AbstractBaseDraw2DDetailsPart
{

    private RoundedRectangle factorFigure;

    private RoundedRectangle rawFigure;

    private RoundedRectangle valueFigure;

    private Label factorLabel;

    private Label valueLabel;

    private Label rawLabel;

    @Override
    protected IFigure createRoot ()
    {
        final Figure rootFigure = new Figure ();

        rootFigure.setLayoutManager ( new GridLayout ( 3, true ) );
        rootFigure.setBackgroundColor ( ColorConstants.white );

        // cell 1,1
        rootFigure.add ( new Figure () );

        // cell 2,1
        rootFigure.add ( this.factorFigure = new RoundedRectangle (), new GridData ( GridData.CENTER, GridData.CENTER, true, true ) );
        this.factorFigure.setBackgroundColor ( ColorConstants.lightGray );
        this.factorFigure.setForegroundColor ( ColorConstants.black );
        this.factorFigure.setBorder ( new MarginBorder ( 10 ) );
        this.factorFigure.setLayoutManager ( new BorderLayout () );
        this.factorFigure.add ( this.factorLabel = new Label (), BorderLayout.CENTER );
        this.factorFigure.addMouseMotionListener ( new MouseMotionListener.Stub () {
            @Override
            public void mouseEntered ( final MouseEvent me )
            {
                InputScaleDetails.this.factorFigure.setLineWidth ( 2 );
            }

            @Override
            public void mouseExited ( final MouseEvent me )
            {
                InputScaleDetails.this.factorFigure.setLineWidth ( 1 );
            }
        } );

        // cell 3,1
        rootFigure.add ( new Figure () );

        // cell 1,2
        rootFigure.add ( this.rawFigure = new RoundedRectangle (), new GridData ( GridData.CENTER, GridData.CENTER, true, true ) );
        this.rawFigure.setBackgroundColor ( ColorConstants.lightGray );
        this.rawFigure.setForegroundColor ( ColorConstants.black );
        this.rawFigure.setBorder ( new MarginBorder ( 10 ) );
        this.rawFigure.setLayoutManager ( new BorderLayout () );
        this.rawFigure.add ( this.rawLabel = new Label (), BorderLayout.CENTER );

        // cell 2,2
        final RectangleFigure rect = new RectangleFigure ();
        rect.setLayoutManager ( new BorderLayout () );
        rect.add ( new Label ( "*" ), BorderLayout.CENTER );
        rect.setBorder ( new MarginBorder ( 10 ) );
        rect.setBackgroundColor ( ColorConstants.lightGray );
        rect.setForegroundColor ( ColorConstants.black );
        rect.setLineStyle ( Graphics.LINE_SOLID );
        rect.setLineWidth ( 1 );
        rect.setFill ( true );
        rect.setOpaque ( true );
        rootFigure.add ( rect, new GridData ( GridData.CENTER, GridData.CENTER, true, true ) );

        // cell 3,2
        rootFigure.add ( this.valueFigure = new RoundedRectangle (), new GridData ( GridData.CENTER, GridData.CENTER, true, true ) );
        this.valueFigure.setLayoutManager ( new BorderLayout () );
        this.valueFigure.setBackgroundColor ( ColorConstants.lightGray );
        this.valueFigure.setForegroundColor ( ColorConstants.black );
        this.valueFigure.setBorder ( new MarginBorder ( 10 ) );
        this.valueFigure.add ( this.valueLabel = new Label (), BorderLayout.CENTER );

        // add connections
        connect ( rootFigure, this.factorFigure, rect );
        connect ( rootFigure, this.rawFigure, rect );
        connect ( rootFigure, rect, this.valueFigure );

        // hook up entry dialogs
        this.factorFigure.addMouseListener ( new MouseListener.Stub () {
            @Override
            public void mouseDoubleClicked ( final MouseEvent me )
            {
                InputScaleDetails.this.triggerFactorInput ();
            }
        } );

        return rootFigure;
    }

    protected void triggerFactorInput ()
    {
        final Variant factor = new VariantEntryDialog ( this.shell ).getValue ();
        if ( factor != null )
        {
            final Map<String, Variant> attributes = new HashMap<String, Variant> ();
            attributes.put ( "org.openscada.da.scale.input.factor", factor );
            this.item.writeAtrtibutes ( attributes );
        }
    }

    private void connect ( final IFigure figure, final IFigure source, final IFigure target )
    {
        final PolylineConnection c = new PolylineConnection ();
        c.setSourceAnchor ( new ChopboxAnchor ( source ) );
        c.setTargetAnchor ( new ChopboxAnchor ( target ) );

        final PolygonDecoration dec = new PolygonDecoration ();
        dec.setTemplate ( PolygonDecoration.TRIANGLE_TIP );
        dec.setBackgroundColor ( ColorConstants.black );
        c.setTargetDecoration ( dec );

        figure.add ( c );
    }

    @Override
    protected void update ()
    {
        if ( this.value == null )
        {
            return;
        }

        // set the main value
        this.valueLabel.setText ( this.value.getValue ().toString () );

        final Variant factor = this.value.getAttributes ().get ( "org.openscada.da.scale.input.factor" );
        final Variant raw = this.value.getAttributes ().get ( "org.openscada.da.scale.input.raw" );

        // set the factor value if available
        if ( factor != null )
        {
            this.factorLabel.setText ( factor.toString () );
        }
        else
        {
            this.factorLabel.setText ( "" );
        }

        // set the raw value if available
        if ( raw != null )
        {
            this.rawLabel.setText ( raw.toString () );
        }
        else
        {
            this.rawLabel.setText ( "" );
        }
    }

}
