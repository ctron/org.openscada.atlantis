package org.openscada.da.server.test.items;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openscada.da.core.IODirection;
import org.openscada.da.core.InvalidOperationException;
import org.openscada.da.core.common.DataItemInformationBase;
import org.openscada.da.core.common.chained.AttributeBinder;
import org.openscada.da.core.common.chained.DataItemInputChained;
import org.openscada.da.core.common.chained.InputChainItem;
import org.openscada.da.core.common.chained.InputChainItemCommon;
import org.openscada.da.core.common.chained.StringBinder;
import org.openscada.da.core.data.NotConvertableException;
import org.openscada.da.core.data.NullValueException;
import org.openscada.da.core.data.Variant;

public class MemoryChainedItem extends DataItemInputChained
{
    
    private class AddClassAttributeBinder implements AttributeBinder
    {
        private MemoryChainedItem _item = null;
        
        public AddClassAttributeBinder ( MemoryChainedItem item )
        {
            super ();
            _item = item;
        }
        
        public void bind ( Variant value ) throws Exception
        {
           if ( value != null )
               if ( !value.isNull () )
                   _item.addInputChainElement ( value.asString () );
        }

        public Variant getAttributeValue ()
        {
            return null;
        }
        
    }
    
    private class RemoveClassAttributeBinder implements AttributeBinder
    {
        private MemoryChainedItem _item = null;
        
        public RemoveClassAttributeBinder ( MemoryChainedItem item )
        {
            super ();
            _item = item;
        }
        
        public void bind ( Variant value ) throws Exception
        {
            if ( value != null )
                if ( !value.isNull () )
                    _item.removeInputChainElement ( value.asString () );
        }

        public Variant getAttributeValue ()
        {
            return null;
        }
        
    }
    
    private class InjectInputChainItem extends InputChainItemCommon
    {
        private MemoryChainedItem _item = null;
        
        public InjectInputChainItem ( MemoryChainedItem item )
        {
            _item = item;
            
            addBinder ( "org.openscada.da.test.inputchain.add", new AddClassAttributeBinder ( item ) );
            addBinder ( "org.openscada.da.test.inputchain.remove", new RemoveClassAttributeBinder ( item ) );
            setReservedAttributes ( "org.openscada.da.test.inputchain.value" );
        }
        
        public void process ( Variant value, Map<String, Variant> attributes )
        {
            int i = 0;
            StringBuilder str = new StringBuilder ();
            for ( InputChainItem item : _item.getInputChainItems () )
            {
                if ( i > 0 )
                    str.append ( ", " );
                str.append ( item.getClass ().getCanonicalName () );
                i++;
            }
            attributes.put ( "org.openscada.da.test.inputchain.value", new Variant ( str.toString () ) );
        }
        
    }
    
    private List<InputChainItem> _inputChainItems = new LinkedList<InputChainItem> ();

    public MemoryChainedItem ( String id )
    {
        super ( new DataItemInformationBase ( id, EnumSet.of ( IODirection.INPUT, IODirection.OUTPUT ) ) );
        addInputChainElement ( new InjectInputChainItem ( this ) );
    }
    
    @Override
    public void setValue ( Variant value ) throws InvalidOperationException, NullValueException, NotConvertableException
    {
        updateValue ( value );
    }
    
    public void addInputChainElement ( String className ) throws Exception
    {
        Class itemClass = Class.forName ( className );
        Object o = itemClass.newInstance ();
        if ( _inputChainItems.add ( (InputChainItem)o ) )
        {
            addInputChainElement ( (InputChainItem )o );
        }
    }
    
    public void removeInputChainElement ( String className ) throws Exception
    {
        for ( InputChainItem item : _inputChainItems )
        {
            if ( item.getClass ().getCanonicalName ().equals ( className ) )
            {
                removeInputChainElement ( item );
                _inputChainItems.remove ( item );
                return;
            }
        }
        throw new Exception ( "Item not found" );
    }
    
    public List<InputChainItem> getInputChainItems ()
    {
        return _inputChainItems;
    }

}
