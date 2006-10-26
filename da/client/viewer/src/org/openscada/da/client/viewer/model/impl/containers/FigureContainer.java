package org.openscada.da.client.viewer.model.impl.containers;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.XYLayout;
import org.openscada.da.client.viewer.configurator.ConfigurationError;
import org.openscada.da.client.viewer.model.Connector;
import org.openscada.da.client.viewer.model.Container;
import org.openscada.da.client.viewer.model.DynamicObject;
import org.openscada.da.client.viewer.model.DynamicUIObject;
import org.openscada.da.client.viewer.model.InputDefinition;
import org.openscada.da.client.viewer.model.OutputDefinition;
import org.openscada.da.client.viewer.model.impl.InputExport;
import org.openscada.da.client.viewer.model.impl.OutputExport;
import org.openscada.da.client.viewer.model.impl.figures.BaseFigure;

public class FigureContainer extends BaseFigure implements Container
{
    private Figure _figure = null;
    private List<Connector> _connectors = new LinkedList<Connector> ();
    private Map<String, DynamicObject> _objects = new HashMap<String, DynamicObject> ();
    
    public FigureContainer ( String id )
    {
        super ( id );
    }
    
    public IFigure getFigure ()
    {
        _figure = new Figure ();
        _figure.setLayoutManager ( new XYLayout () );
        update ();
        
        for ( DynamicObject object : _objects.values () )
        {
            if ( object instanceof DynamicUIObject )
            {
                _figure.add ( ((DynamicUIObject)object).getFigure () );
            }
        }
        
        return _figure;
    }

    public void dispose ()
    {
        // dispose connections
        for ( Connector connector : _connectors )
        {
            connector.dispose ();
        }
        
        // dispose objects
        for ( DynamicObject object : _objects.values () )
        {
            object.dispose ();
        }
        _objects.clear ();
        
        // dispose gui stuff
        if ( _figure != null )
        {
            _figure.removeAll ();
            _figure = null;
        }
    }

    public void add ( DynamicObject object )
    {
        _objects.put ( object.getId (), object );
        if ( _figure != null )
        {
            if ( object instanceof DynamicUIObject )
            {
                _figure.add ( ((DynamicUIObject)object).getFigure () );
            }
        }
    }

    public void remove ( DynamicObject object )
    {
        _objects.remove ( object.getId () );
        if ( _figure != null )
        {
            if ( object instanceof DynamicUIObject )
            {
                _figure.remove ( ((DynamicUIObject)object).getFigure () );
            }
        }
    }
    
    public Collection<DynamicObject> getObjects ()
    {
        return Collections.unmodifiableCollection ( _objects.values () );
    }
    
    protected void update ()
    {
        if ( _figure == null )
            return;
        
        updateFigure ( _figure );
    }

    public void addInputExport ( Export export ) throws ConfigurationError
    {
        DynamicObject object = _objects.get ( export.getObject () );
        if ( object == null )
            throw new ConfigurationError ( String.format ( "Unable to export input since object %s is unknown", export.getObject () ) );
         
        InputDefinition inputDef = object.getInputByName ( export.getName () );
        if ( inputDef == null )
            throw new ConfigurationError ( String.format ( "Unable to export input since input %s of object %s is unknown", export.getName (), export.getObject () ) );
        
        addInput ( new InputExport ( inputDef, export.getAlias () ) );
    }

    public void addOutputExport ( Export export ) throws ConfigurationError
    {
        DynamicObject object = _objects.get ( export.getObject () );
        if ( object == null )
            throw new ConfigurationError ( String.format ( "Unable to export output since object %s is unknown", export.getObject () ) );
         
        OutputDefinition outputDef = object.getOutputByName ( export.getName () );
        if ( outputDef == null )
            throw new ConfigurationError ( String.format ( "Unable to export input since output %s of object %s is unknown", export.getName (), export.getObject () ) );
        
        addOutput ( new OutputExport ( outputDef, export.getAlias () ) );
    }

    public void add ( Connector connector )
    {
        _connectors.add ( connector );
    }

    public Collection<Connector> getConnectors ()
    {
        return _connectors;
    }

    public void remove ( Connector connector )
    {
        _connectors.remove ( connector );
    }

}
