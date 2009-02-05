package org.openscada.da.client.viewer.model.impl.widgets;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.openscada.core.Variant;
import org.openscada.da.client.viewer.model.impl.Helper;
import org.openscada.da.client.viewer.model.impl.PropertyInput;
import org.openscada.da.client.viewer.model.impl.figures.BoundsFigure;

public class PercentBar extends BoundsFigure
{
    private static Logger _log = Logger.getLogger ( PercentBar.class );

    @SuppressWarnings ( "unused" )
    private Canvas _canvas = null;

    private Figure _innerRect = null;

    private RectangleFigure _fillRect = null;

    private double _min = 0;

    private double _max = 1;

    private Variant _value = new Variant ();

    private Color _invalidColor = null;

    private Color _valueColor = null;

    private Label _valueLabel;

    private RectangleFigure _maxRect = null;

    private RectangleFigure _minRect = null;

    private Panel _outerRect;

    private String _label = null;

    private Label _infoLabel;

    public PercentBar ( final String id )
    {
        super ( id );
        addInput ( new PropertyInput ( this, "min" ) );
        addInput ( new PropertyInput ( this, "max" ) );
        addInput ( new PropertyInput ( this, "value" ) );
    }

    @Override
    protected void update ()
    {
        if ( this._innerRect != null )
        {
            updateFigure ();
        }
    }

    protected Double getPercent ()
    {
        if ( this._min - this._max == 0 )
        {
            return null;
        }

        try
        {
            return ( this._value.asDouble () - this._min ) / Math.abs ( this._min - this._max );
        }
        catch ( final Exception e )
        {
            return null;
        }
    }

    protected Integer getPosition ()
    {
        final Double percent = getPercent ();
        if ( percent == null )
        {
            return null;
        }
        return (int) ( getBounds ().width * percent );
    }

    protected boolean isMin ()
    {
        try
        {
            return this._value.asDouble () <= this._min;
        }
        catch ( final Exception e )
        {
            return false;
        }
    }

    protected boolean isMax ()
    {
        try
        {
            return this._value.asDouble () >= this._max;
        }
        catch ( final Exception e )
        {
            return false;
        }
    }

    protected void updateFigure ()
    {
        super.updateFigure ( this._outerRect );

        final Rectangle bounds = new Rectangle ();
        bounds.x = 0;
        bounds.y = 0;
        bounds.height = -1;

        final Integer pos = getPosition ();
        if ( pos != null )
        {
            bounds.width = pos;
            this._fillRect.setBackgroundColor ( this._valueColor );
            this._valueLabel.setText ( String.format ( "%.2f%% (%s)", getPercent () * 100.0, this._value ) );
        }
        else
        {
            bounds.width = this._outerRect.getBounds ().width;
            this._fillRect.setBackgroundColor ( this._invalidColor );
            this._valueLabel.setText ( "Invalid value: " + this._value );
        }
        this._fillRect.setBounds ( bounds );
        this._fillRect.revalidate ();

        this._minRect.setVisible ( isMin () );
        this._maxRect.setVisible ( isMax () );

        this._infoLabel.setText ( this._label );
    }

    public void createFigure ( final Canvas canvas, final IFigure parent )
    {
        if ( this._innerRect == null )
        {
            this._canvas = canvas;

            this._outerRect = new Panel ();
            this._outerRect.setLayoutManager ( new BorderLayout () );

            this._innerRect = new RectangleFigure ();
            this._innerRect.setLayoutManager ( new BorderLayout () );

            this._fillRect = new RectangleFigure ();
            this._fillRect.setFill ( true );
            this._innerRect.add ( this._fillRect );
            this._innerRect.setConstraint ( this._fillRect, BorderLayout.LEFT );

            this._infoLabel = new Label ();
            this._outerRect.add ( this._infoLabel );
            this._outerRect.setConstraint ( this._infoLabel, BorderLayout.TOP );

            this._valueLabel = new Label ();
            this._outerRect.add ( this._valueLabel );
            this._outerRect.setConstraint ( this._valueLabel, BorderLayout.BOTTOM );

            this._outerRect.add ( this._innerRect );
            parent.add ( this._outerRect );

            this._maxRect = new RectangleFigure ();
            this._maxRect.setVisible ( false );
            this._maxRect.setFill ( true );
            this._maxRect.setBackgroundColor ( ColorConstants.red );
            this._maxRect.setLineWidth ( 0 );
            this._maxRect.setOutline ( false );
            this._maxRect.setBounds ( new Rectangle ( 0, 0, 2, -1 ) );
            this._outerRect.add ( this._maxRect );

            this._minRect = new RectangleFigure ();
            this._minRect.setVisible ( false );
            this._minRect.setFill ( true );
            this._minRect.setBackgroundColor ( ColorConstants.red );
            this._minRect.setLineWidth ( 0 );
            this._minRect.setOutline ( false );
            this._minRect.setBounds ( new Rectangle ( 0, 0, 2, -1 ) );
            this._outerRect.add ( this._minRect );

            // set layout constraints
            this._outerRect.setConstraint ( this._innerRect, BorderLayout.CENTER );
            this._outerRect.setConstraint ( this._minRect, BorderLayout.LEFT );
            this._outerRect.setConstraint ( this._maxRect, BorderLayout.RIGHT );

            // create resources
            setInvalidColor ( null );
            setInvalidColor ( null );

            update ();
        }
    }

    @Override
    public void dispose ()
    {
        if ( this._innerRect != null )
        {
            this._innerRect.getParent ().remove ( this._innerRect );
        }
        if ( this._invalidColor != null )
        {
            this._invalidColor.dispose ();
            this._invalidColor = null;
        }
        if ( this._valueColor != null )
        {
            this._valueColor.dispose ();
            this._valueColor = null;
        }
        super.dispose ();
    }

    public double getMax ()
    {
        return this._max;
    }

    public void setMax ( final double max )
    {
        this._max = max;
        update ();
    }

    public double getMin ()
    {
        return this._min;
    }

    public void setMin ( final double min )
    {
        this._min = min;
        update ();
    }

    public Variant getValue ()
    {
        return this._value;
    }

    public void setValue ( final Variant value )
    {
        this._value = new Variant ( value );
        _log.debug ( "new value: " + this._value );
        update ();
    }

    public void setLabel ( final String label )
    {
        this._label = label;
    }

    public void setValueColor ( final org.openscada.da.client.viewer.model.types.Color valueColor )
    {
        if ( this._valueColor != null )
        {
            this._valueColor.dispose ();
        }

        if ( valueColor == null )
        {
            this._valueColor = new Color ( Display.getCurrent (), new RGB ( 0, 0, 255 ) );
        }
        else
        {
            this._valueColor = new Color ( Display.getCurrent (), Helper.colorToRGB ( valueColor ) );
        }
        update ();
    }

    public void setInvalidColor ( final org.openscada.da.client.viewer.model.types.Color invalidColor )
    {
        if ( this._invalidColor != null )
        {
            this._invalidColor.dispose ();
        }

        if ( invalidColor == null )
        {
            this._invalidColor = new Color ( Display.getCurrent (), new RGB ( 255, 0, 255 ) );
        }
        else
        {
            this._invalidColor = new Color ( Display.getCurrent (), Helper.colorToRGB ( invalidColor ) );
        }
        update ();
    }

}
