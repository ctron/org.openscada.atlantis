package org.openscada.da.client.viewer.model;

import java.util.Collection;

import org.openscada.da.client.viewer.configurator.ConfigurationError;

public interface Container extends DynamicObject
{
    public abstract void add ( DynamicObject object );
    public abstract void remove ( DynamicObject object );
    public abstract Collection<DynamicObject> getObjects ();
    
    public class Export
    {
        public String _object;
        public String _name;
        public String _alias;
        
        public Export ( String object, String name, String alias )
        {
            _object = object;
            _name = name;
            _alias = alias;
        }

        public String getAlias ()
        {
            return _alias;
        }

        public void setAlias ( String alias )
        {
            _alias = alias;
        }

        public String getName ()
        {
            return _name;
        }

        public void setName ( String name )
        {
            _name = name;
        }

        public String getObject ()
        {
            return _object;
        }

        public void setObject ( String object )
        {
            _object = object;
        }
    }
    
    public abstract void addInputExport ( Export export ) throws ConfigurationError;
    public abstract void removeInputExport ( String eportName );
    public abstract Collection<Export> getInputExports ();
    
    public abstract void addOutputExport ( Export export ) throws ConfigurationError;
    public abstract void removeOutputExport ( String exportName );
    public abstract Collection<Export> getOutputExports ();
    
    public abstract void add ( Connector connector );
    public abstract void remove ( Connector connector );
    public abstract Collection<Connector> getConnectors ();

}
