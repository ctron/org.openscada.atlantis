package org.openscada.da.client.test.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import org.apache.log4j.Logger;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.openscada.da.core.data.Variant;


public class BrowserEntry extends Observable implements IPropertySource
{
    private static Logger _log = Logger.getLogger ( BrowserEntry.class );
    
    private String _name = null;
    private Map<String, Variant> _attributes = null;
    private HiveConnection _connection = null;
    private FolderEntry _parent = null;
    
    private enum Properties {
        NAME;
    }
    
    public BrowserEntry ( String name,  Map<String, Variant> attributes, HiveConnection connection, FolderEntry parent )
    {
        _name = name;
        _connection = connection;
        _parent = parent;
        _attributes = attributes;
    }

    public String getName ()
    {
        return _name;
    }

    public FolderEntry getParent ()
    {
        return _parent;
    }

    public HiveConnection getConnection ()
    {
        return _connection;
    }

    public Map<String, Variant> getAttributes ()
    {
        return _attributes;
    }

    
    // IPropertySource
    
    public Object getEditableValue ()
    {
        return _name;
    }

    protected void fillPropertyDescriptors ( List<IPropertyDescriptor> list )
    {
        {
            PropertyDescriptor pd = new PropertyDescriptor ( Properties.NAME, "Name" );
            pd.setCategory ( "Entry Info" );
            list.add ( pd );
        }
        
        for ( Map.Entry<String, Variant> entry : _attributes.entrySet () )
        {
            _log.debug ( "Property: " + entry.getKey() + "=" + entry.getValue() );
            PropertyDescriptor pd = new PropertyDescriptor ( entry.getKey (), entry.getKey() );
            pd.setAlwaysIncompatible ( true );
            pd.setCategory ( "Entry Attributes" );
            
            list.add ( pd );
        }
    }
    
    public IPropertyDescriptor[] getPropertyDescriptors ()
    {
        List<IPropertyDescriptor> list = new ArrayList<IPropertyDescriptor> ();
        
        fillPropertyDescriptors ( list );
        
        return list.toArray ( new IPropertyDescriptor[list.size()] );
    }

    public Object getPropertyValue ( Object id )
    {
        if ( id.equals ( Properties.NAME ))
            return _name;
        
        if ( !(id instanceof String) )
            return null;
        
        String name = (String)id;
        
        return _attributes.get ( name ).asString ( null );
    }

    public boolean isPropertySet ( Object id )
    {
        return false;
    }

    public void resetPropertyValue ( Object id )
    {
        // no op
    }

    public void setPropertyValue ( Object id, Object value )
    {
        // no op
    }
}
