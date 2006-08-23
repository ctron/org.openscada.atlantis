package org.openscada.da.core.common.chained;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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

public abstract class DataItemOutputChained extends DataItemBaseChained
{

    private List<OutputChainItem> _outputChain = new LinkedList<OutputChainItem> ();
    
    public DataItemOutputChained ( DataItemInformation information )
    {
        super ( information );
    }
    
    public DataItemOutputChained ( String id )
    {
        this ( new DataItemInformationBase ( id, EnumSet.of ( IODirection.INPUT ) ) );
    }

    public Variant getValue () throws InvalidOperationException
    {
        throw new InvalidOperationException ();
    }

    synchronized public void setValue ( Variant value ) throws InvalidOperationException, NullValueException, NotConvertableException
    {
        for ( OutputChainItem item : _outputChain )
        {
            item.writeValue ( value );
        }
        writeValue ( value );
    }

    @Override
    protected Collection<BaseChainItem> getChainItems ()
    {
        return Arrays.asList ( _outputChain.toArray ( new BaseChainItem[0] ) );
    }
    
    synchronized public void addInputChainElement ( OutputChainItem item )
    {
        if ( _outputChain.add ( item ) )
        {
            process ();
        }
    }

    synchronized public void removeInputChainElement ( OutputChainItem item )
    {
        if ( _outputChain.remove ( item ) )
        {
            process ();
        }
    }

    @Override
    protected void process ()
    {
        Map<String, Variant> primaryAttributes = new HashMap<String, Variant> ( _primaryAttributes );
        
        for ( OutputChainItem item : _outputChain )
        {
            item.process ( primaryAttributes );
        }
        
        _secondaryAttributes.set ( primaryAttributes );
    }
    
    @Override
    public void setListener ( ItemListener listener )
    {
        super.setListener ( listener );
        
        if ( listener != null )
        {
            if ( _secondaryAttributes.get ().size () > 0 )
                notifyAttributes ( _secondaryAttributes.get () );
        }
    }
    
    protected abstract void writeValue ( Variant value ) throws NullValueException, NotConvertableException;

}
