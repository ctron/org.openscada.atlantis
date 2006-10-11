package org.openscada.da.client.viewer.model.impl.containers;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openscada.da.client.viewer.configurator.ConfigurationError;
import org.openscada.da.client.viewer.model.Connector;
import org.openscada.da.client.viewer.model.Container;
import org.openscada.da.client.viewer.model.DynamicObject;
import org.openscada.da.client.viewer.model.InputDefinition;
import org.openscada.da.client.viewer.model.OutputDefinition;
import org.openscada.da.client.viewer.model.impl.BaseDynamicObject;
import org.openscada.da.client.viewer.model.impl.InputExport;
import org.openscada.da.client.viewer.model.impl.OutputExport;

public class HiddenContainer extends BaseDynamicObject implements Container
{
    private Map<String, DynamicObject> _objects = new HashMap<String, DynamicObject>();
    private List<Connector> _connectors = new LinkedList<Connector> ();
    
    public HiddenContainer ( String id )
    {
        super ( id );
    }

    public void add ( DynamicObject object )
    {
        _objects.put ( object.getId (), object );
    }
    
    public void remove ( DynamicObject object )
    {
        _objects.remove ( object.getId () );
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

    public Collection<DynamicObject> getObjects ()
    {
        return _objects.values ();
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
