package org.openscada.da.client.dataitem.details.extra.part;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.Triangle;
import org.eclipse.draw2d.geometry.Dimension;
import org.openscada.core.Variant;

public class LevelPresets extends AbstractBaseDraw2DDetailsPart
{
    private Triangle triHH;

    private Triangle triH;

    private Triangle triL;

    private Triangle triLL;

    private Label presetHH;

    private Label presetH;

    private Label presetL;

    private Label presetLL;

    private Label currentLabel;

    @Override
    protected IFigure createRoot ()
    {
        final Figure rootFigure = new Figure ();

        rootFigure.setLayoutManager ( new BorderLayout () );
        rootFigure.setBackgroundColor ( ColorConstants.white );

        rootFigure.add ( createArrowFigure (), BorderLayout.RIGHT );
        rootFigure.add ( createEntryGrid ( rootFigure ), BorderLayout.CENTER );

        return rootFigure;
    }

    private IFigure createEntryGrid ( final Figure rootFigure )
    {
        final Figure figure = new Figure ();
        figure.setLayoutManager ( new GridLayout ( 1, false ) );

        figure.add ( this.presetHH = new Label ( "" ), new GridData ( GridData.CENTER, GridData.FILL, true, true ) );
        figure.add ( this.presetH = new Label ( "" ), new GridData ( GridData.CENTER, GridData.FILL, true, true ) );

        figure.add ( this.currentLabel = new Label ( "" ), new GridData ( GridData.CENTER, GridData.FILL, true, true ) );

        figure.add ( this.presetL = new Label ( "" ), new GridData ( GridData.CENTER, GridData.FILL, true, true ) );
        figure.add ( this.presetLL = new Label ( "" ), new GridData ( GridData.CENTER, GridData.FILL, true, true ) );

        createConnection ( rootFigure, this.presetHH, this.triHH );
        createConnection ( rootFigure, this.presetH, this.triH );
        createConnection ( rootFigure, this.presetL, this.triL );
        createConnection ( rootFigure, this.presetLL, this.triLL );

        return figure;
    }

    private void createConnection ( final Figure rootFigure, final Label label, final Triangle tri )
    {
        final Connection c = new PolylineConnection ();
        c.setSourceAnchor ( new ChopboxAnchor ( label ) );
        c.setTargetAnchor ( new ChopboxAnchor ( tri ) );
        c.setConnectionRouter ( new BendpointConnectionRouter () );
        rootFigure.add ( c );
    }

    private final static Dimension TRI_DIMENSION = new Dimension ( 50, 50 );

    private IFigure createArrowFigure ()
    {
        final Figure figure = new Figure ();
        final Figure innerFigure = new Figure ();

        figure.setLayoutManager ( new BorderLayout () );
        innerFigure.setLayoutManager ( new BorderLayout () );

        Triangle tri;

        // create HH
        this.triHH = tri = new Triangle ();
        tri.setDirection ( Triangle.NORTH );
        tri.setBackgroundColor ( ColorConstants.black );
        tri.setSize ( TRI_DIMENSION );
        tri.setLineWidth ( 3 );
        attachDoubleClick ( tri, "highhigh" );
        figure.add ( tri, BorderLayout.TOP );

        // create H
        this.triH = tri = new Triangle ();
        tri.setDirection ( Triangle.NORTH );
        tri.setBackgroundColor ( ColorConstants.black );
        tri.setSize ( TRI_DIMENSION );
        tri.setLineWidth ( 3 );
        attachDoubleClick ( tri, "high" );
        innerFigure.add ( tri, BorderLayout.TOP );

        // create L
        this.triL = tri = new Triangle ();
        tri.setDirection ( Triangle.SOUTH );
        tri.setBackgroundColor ( ColorConstants.black );
        tri.setSize ( TRI_DIMENSION );
        tri.setLineWidth ( 3 );
        attachDoubleClick ( tri, "low" );
        innerFigure.add ( tri, BorderLayout.BOTTOM );

        // create LL
        this.triLL = tri = new Triangle ();
        tri.setDirection ( Triangle.SOUTH );
        tri.setBackgroundColor ( ColorConstants.black );
        tri.setSize ( TRI_DIMENSION );
        tri.setLineWidth ( 3 );
        attachDoubleClick ( tri, "lowlow" );
        figure.add ( tri, BorderLayout.BOTTOM );

        figure.add ( innerFigure, BorderLayout.CENTER );

        // create inner
        final PolylineConnection c;
        c = new PolylineConnection ();
        c.setSourceAnchor ( new ChopboxAnchor ( this.triH ) );
        c.setTargetAnchor ( new ChopboxAnchor ( this.triL ) );
        c.setLineWidth ( 5 );

        innerFigure.add ( c );

        return figure;
    }

    private void attachDoubleClick ( final Triangle tri, final String string )
    {
        tri.addMouseListener ( new MouseListener () {

            public void mouseDoubleClicked ( final MouseEvent me )
            {
                LevelPresets.this.triggerAction ( string );
            }

            public void mousePressed ( final MouseEvent me )
            {
                // TODO Auto-generated method stub

            }

            public void mouseReleased ( final MouseEvent me )
            {
                // TODO Auto-generated method stub

            }
        } );
    }

    protected void triggerAction ( final String string )
    {
        try
        {
            final Variant value = new VariantEntryDialog ( this.shell ).getValue ();

            if ( value != null )
            {
                setPreset ( value, string );
            }
        }
        catch ( final Throwable e )
        {
            e.printStackTrace ();
        }
    }

    @Override
    protected void update ()
    {
        if ( this.value == null )
        {
            return;
        }

        setTri ( this.triHH, "highhigh" );
        setTri ( this.triH, "high" );
        setTri ( this.triL, "low" );
        setTri ( this.triLL, "lowlow" );

        setLabel ( this.presetHH, "highhigh" );
        setLabel ( this.presetH, "high" );
        setLabel ( this.presetL, "low" );
        setLabel ( this.presetLL, "lowlow" );

        this.currentLabel.setText ( "" + this.value.getValue () );
    }

    private void setLabel ( final Label preset, final String string )
    {
        final Number num = getPreset ( string );
        if ( num != null )
        {
            preset.setText ( String.format ( "%s", num.toString () ) );
        }
        else
        {
            preset.setText ( "<none>" );
        }
    }

    private void setTri ( final Triangle tri, final String string )
    {
        tri.setOutline ( isActive ( string ) );
        tri.setBackgroundColor ( isAlarm ( string ) ? ColorConstants.red : ColorConstants.lightGray );
    }

    private boolean isActive ( final String string )
    {
        return getPreset ( string ) != null;
    }

    private Number getPreset ( final String string )
    {
        return getNumberAttribute ( String.format ( "org.openscada.da.level.%s.preset", string ), null );
    }

    private boolean isAlarm ( final String string )
    {
        return getBooleanAttribute ( String.format ( "org.openscada.da.level.%s.alarm", string ) );
    }

    @SuppressWarnings ( "unused" )
    private boolean isError ( final String string )
    {
        return getBooleanAttribute ( String.format ( "org.openscada.da.level.%s.error", string ) );
    }

    private void setPreset ( final Variant value, final String string )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();

        attributes.put ( String.format ( "org.openscada.da.level.%s.preset", string ), value );

        this.item.writeAtrtibutes ( attributes );
    }

}
