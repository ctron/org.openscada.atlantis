package org.openscada.da.core.common.chained;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openscada.da.core.DataItemInformation;
import org.openscada.da.core.IODirection;
import org.openscada.da.core.InvalidOperationException;
import org.openscada.da.core.WriteAttributesOperationListener.Result;
import org.openscada.da.core.WriteAttributesOperationListener.Results;
import org.openscada.da.core.common.AttributeManager;
import org.openscada.da.core.common.DataItemBase;
import org.openscada.da.core.common.DataItemInformationBase;
import org.openscada.da.core.data.AttributesHelper;
import org.openscada.da.core.data.NotConvertableException;
import org.openscada.da.core.data.NullValueException;
import org.openscada.da.core.data.Variant;

public class DataItemInputChained extends DataItemBase
{
    private Map<String,Variant> _primaryAttributes = null;
    private AttributeManager _secondaryAttributes = null;
    private Variant _primaryValue = new Variant ();
    private Variant _secondaryValue = new Variant ();
    
    private List<InputChainItem> _inputChain = new LinkedList<InputChainItem> ();
    
    public DataItemInputChained ( DataItemInformation di )
    {
        super ( di );
        
        _primaryAttributes = new HashMap<String, Variant> ();
        _secondaryAttributes = new AttributeManager ( this );
    }
    
    public DataItemInputChained ( String id )
    {
        this ( new DataItemInformationBase ( id, EnumSet.of ( IODirection.INPUT ) ) );
    }
    
    synchronized public Results setAttributes ( Map<String, Variant> attributes )
    {
        Results results = new Results ();

        for ( InputChainItem item : _inputChain )
        {
            Results partialResult = item.setAttributes ( attributes );
            if ( partialResult != null )
            {
                for ( Map.Entry<String, Result> entry : partialResult.entrySet () )
                {
                    if ( entry.getValue ().isError () )
                    {
                        attributes.remove ( entry.getKey () );
                    }
                    results.put ( entry.getKey (), entry.getValue () );
                }
            }
        }
        Map<String, Variant> diff = new HashMap<String, Variant> ();
        AttributesHelper.mergeAttributes ( _primaryAttributes, attributes, diff );
        
        process ();
        return results;
    }
    
    synchronized public void addInputChainElement ( InputChainItem item )
    {
        _inputChain.add ( item );
        process ();
    }

    synchronized public void removeInputChainElement ( InputChainItem item )
    {
        _inputChain.remove ( item );
        process ();
    }
 
    synchronized public void updateValue ( Variant value )
    {
        if ( _primaryValue.equals ( value ) )
            return;
        
        _primaryValue = new Variant ( value );
        process ();
    }
    
    synchronized public void updateAttributes ( Map<String, Variant> attributes )
    {
        Map<String, Variant> diff = new HashMap <String, Variant> ();
        AttributesHelper.mergeAttributes ( _primaryAttributes, attributes, diff );
        
        if ( diff.size () > 0 )
            process ();
    }
    
    private void process ()
    {
        Variant primaryValue = new Variant ( _primaryValue );
        Map<String, Variant> primaryAttributes = new HashMap<String, Variant> ( _primaryAttributes );
        for ( InputChainItem item : _inputChain )
        {
            item.process ( primaryValue, primaryAttributes );
        }
        if ( !_secondaryValue.equals ( primaryValue ) )
        {
            _secondaryValue = primaryValue;
            notifyValue ( _secondaryValue );
        }
        _secondaryAttributes.update ( primaryAttributes );
    }

    public Map<String, Variant> getAttributes ()
    {
        return _secondaryAttributes.get ();
    }

    public Variant getValue () throws InvalidOperationException
    {
        return new Variant ( _secondaryValue );
    }

    public void setValue ( Variant value ) throws InvalidOperationException, NullValueException, NotConvertableException
    {
        throw new InvalidOperationException ();
    }
    
}
