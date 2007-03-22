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

    public PercentBar ( String id )
    {
        super ( id );
        addInput ( new PropertyInput ( this, "min" ) );
        addInput ( new PropertyInput ( this, "max" ) );
        addInput ( new PropertyInput ( this, "value" ) );
    }

    @Override
    protected void update ()
    {
        if ( _innerRect != null )
        {
            updateFigure ();
        }
    }

    protected Double getPercent ()
    {
        if ( ( _min - _max ) == 0 )
        {
            return null;
        }

        try
        {
            return ( _value.asDouble () - _min ) / Math.abs ( _min - _max );
        }
        catch ( Exception e )
        {
            return null;
        }
    }

    protected Integer getPosition ()
    {
        Double percent = getPercent ();
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
            return _value.asDouble () <= _min;
        }
        catch ( Exception e )
        {
            return false;
        }
    }

    protected boolean isMax ()
    {
        try
        {
            return _value.asDouble () >= _max;
        }
        catch ( Exception e )
        {
            return false;
        }
    }

    protected void updateFigure ()
    {
        super.updateFigure ( _outerRect );

        Rectangle bounds = new Rectangle ();
        bounds.x = 0;
        bounds.y = 0;
        bounds.height = -1;

        Integer pos = getPosition ();
        if ( pos != null )
        {
            bounds.width = pos;
            _fillRect.setBackgroundColor ( _valueColor );
            _valueLabel.setText ( String.format ( "%.2f%% (%s)", getPercent () * 100.0, _value ) );
        }
        else
        {
            bounds.width = _outerRect.getBounds ().width;
            _fillRect.setBackgroundColor ( _invalidColor );
            _valueLabel.setText ( "Invalid value: " + _value );
        }
        _fillRect.setBounds ( bounds );
        _fillRect.revalidate ();

        _minRect.setVisible ( isMin () );
        _maxRect.setVisible ( isMax () );
        
        _infoLabel.setText ( _label );
    }

    public void createFigure ( Canvas canvas, IFigure parent )
    {
        if ( _innerRect == null )
        {
            _canvas = canvas;
            
            _outerRect = new Panel ();
            _outerRect.setLayoutManager ( new BorderLayout () );
            
            _innerRect = new RectangleFigure ();
            _innerRect.setLayoutManager ( new BorderLayout () );
            
            _fillRect = new RectangleFigure ();
            _fillRect.setFill ( true );
            _innerRect.add ( _fillRect );
            _innerRect.setConstraint ( _fillRect, BorderLayout.LEFT );

            _infoLabel = new Label ();
            _outerRect.add ( _infoLabel );
            _outerRect.setConstraint ( _infoLabel, BorderLayout.TOP );
            
            _valueLabel = new Label ();
            _outerRect.add ( _valueLabel );
            _outerRect.setConstraint ( _valueLabel, BorderLayout.BOTTOM );
            
            _outerRect.add ( _innerRect );
            parent.add ( _outerRect );

            _maxRect = new RectangleFigure ();
            _maxRect.setVisible ( false );
            _maxRect.setFill ( true );
            _maxRect.setBackgroundColor ( ColorConstants.red );
            _maxRect.setLineWidth ( 0 );
            _maxRect.setOutline ( false );
            _maxRect.setBounds ( new Rectangle ( 0, 0, 2, -1 ) );
            _outerRect.add ( _maxRect );
            
            _minRect = new RectangleFigure ();
            _minRect.setVisible ( false );
            _minRect.setFill ( true );
            _minRect.setBackgroundColor ( ColorConstants.red );
            _minRect.setLineWidth ( 0 );
            _minRect.setOutline ( false );
            _minRect.setBounds ( new Rectangle ( 0, 0, 2, -1 ) );
            _outerRect.add ( _minRect );

            // set layout constraints
            _outerRect.setConstraint ( _innerRect, BorderLayout.CENTER );
            _outerRect.setConstraint ( _minRect, BorderLayout.LEFT );
            _outerRect.setConstraint ( _maxRect, BorderLayout.RIGHT );
            
            // create resources
            setInvalidColor ( null );
            setInvalidColor ( null );

            update ();
        }
    }

    @Override
    public void dispose ()
    {
        if ( _innerRect != null )
        {
            _innerRect.getParent ().remove ( _innerRect );
        }
        if ( _invalidColor != null )
        {
            _invalidColor.dispose ();
            _invalidColor = null;
        }
        if ( _valueColor != null )
        {
            _valueColor.dispose ();
            _valueColor = null;
        }
        super.dispose ();
    }

    public double getMax ()
    {
        return _max;
    }

    public void setMax ( double max )
    {
        _max = max;
        update ();
    }

    public double getMin ()
    {
        return _min;
    }

    public void setMin ( double min )
    {
        _min = min;
        update ();
    }

    public Variant getValue ()
    {
        return _value;
    }

    public void setValue ( Variant value )
    {
        _value = new Variant ( value );
        _log.debug ( "new value: " + _value );
        update ();
    }
    
    public void setLabel ( String label )
    {
        _label = label;
    }
    
    public void setValueColor ( org.openscada.da.client.viewer.model.types.Color valueColor )
    {
        if ( _valueColor != null )
        {
            _valueColor.dispose ();
        }
        
        if ( valueColor == null )
            _valueColor = new Color ( Display.getCurrent (), new RGB ( 0, 0, 255 ) );
        else
            _valueColor = new Color ( Display.getCurrent (), Helper.colorToRGB ( valueColor ) );
        update ();
    }
    
    public void setInvalidColor ( org.openscada.da.client.viewer.model.types.Color invalidColor )
    {
        if ( _invalidColor != null )
        {
            _invalidColor.dispose ();
        }
        
        if ( invalidColor == null )
            _invalidColor = new Color ( Display.getCurrent (), new RGB ( 255, 0, 255 ) );
        else
            _invalidColor = new Color ( Display.getCurrent (), Helper.colorToRGB ( invalidColor ) );
        update ();
    }

}
