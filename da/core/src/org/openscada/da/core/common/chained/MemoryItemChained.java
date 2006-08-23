package org.openscada.da.core.common.chained;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openscada.da.core.DataItemInformation;
import org.openscada.da.core.IODirection;
import org.openscada.da.core.InvalidOperationException;
import org.openscada.da.core.common.DataItemInformationBase;
import org.openscada.da.core.common.ItemListener;
import org.openscada.da.core.data.NotConvertableException;
import org.openscada.da.core.data.NullValueException;
import org.openscada.da.core.data.Variant;

public class MemoryItemChained extends DataItemBaseChained
{
    private List<BaseChainItem> _chain = new LinkedList<BaseChainItem> ();
    
    private Variant _primaryValue = new Variant ();
    private Variant _secondaryValue = new Variant ();
    
    public MemoryItemChained ( DataItemInformation di )
    {
        super ( di );
    }
    
    public MemoryItemChained ( String id )
    {
        this ( new DataItemInformationBase ( id, EnumSet.of ( IODirection.INPUT, IODirection.OUTPUT ) ) );
    }

    @Override
    protected Collection<BaseChainItem> getChainItems ()
    {
        return new ArrayList<BaseChainItem> ( _chain );
    }

    @Override
    protected void process ()
    {
        Variant primaryValue = new Variant ( _primaryValue );
        Map<String, Variant> primaryAttributes = new HashMap<String, Variant> ( _primaryAttributes );
        
        for ( BaseChainItem item : _chain )
        {
            if ( item instanceof InputChainItem )
                ((InputChainItem)item).process ( primaryValue, primaryAttributes );
            else if ( item instanceof OutputChainItem )
                ((OutputChainItem)item).process ( primaryAttributes );
        }
        
        if ( !_secondaryValue.equals ( primaryValue ) )
        {
            _secondaryValue = new Variant ( primaryValue );
            notifyValue ( _secondaryValue );
        }
        _secondaryAttributes.set ( primaryAttributes );
    }

    public Variant getValue () throws InvalidOperationException
    {
        return new Variant ( _secondaryValue );
    }

    synchronized public void setValue ( Variant value ) throws InvalidOperationException, NullValueException, NotConvertableException
    {
        for ( BaseChainItem item : _chain )
        {
            if ( item instanceof OutputChainItem )
                ((OutputChainItem)item).writeValue ( value );
        }
        writeValue ( value );
    }

    private void writeValue ( Variant value )
    {
        if ( _primaryValue.equals ( value ) )
            return;
        
        _primaryValue = new Variant ( value );
        notifyValue ( _primaryValue );
    }
    
    synchronized public void addChainElement ( BaseChainItem item )
    {
        if ( _chain.add ( item ) )
        {
            process ();
        }
    }

    synchronized public void removeChainElement ( BaseChainItem item )
    {
        if ( _chain.remove ( item ) )
        {
            process ();
        }
    }
    
    @Override
    public void setListener ( ItemListener listener )
    {
        super.setListener ( listener );
        if ( listener != null )
        {
            if ( !_secondaryValue.isNull () )
                notifyValue ( _secondaryValue );
            if ( _secondaryAttributes.get ().size () > 0 )
                notifyAttributes ( _secondaryAttributes.get () );
        }
    }

}
