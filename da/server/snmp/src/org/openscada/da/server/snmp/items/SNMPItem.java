package org.openscada.da.server.snmp.items;

import java.util.EnumSet;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openscada.da.core.IODirection;
import org.openscada.da.core.InvalidOperationException;
import org.openscada.da.core.common.AttributeManager;
import org.openscada.da.core.common.DataItemBase;
import org.openscada.da.core.common.DataItemInformationBase;
import org.openscada.da.core.data.NotConvertableException;
import org.openscada.da.core.data.NullValueException;
import org.openscada.da.core.data.Variant;
import org.openscada.da.server.snmp.SNMPNode;
import org.openscada.da.server.snmp.utils.SNMPBulkReader;
import org.snmp4j.PDU;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.Null;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;

public class SNMPItem extends DataItemBase implements Runnable
{
    private static Logger _log = Logger.getLogger ( SNMPItem.class );
    private AttributeManager _attributes = null;
    
    private SNMPNode _node = null;
    private OID _oid = null;
    
    private Variant _value = new Variant ();
    
    public SNMPItem ( SNMPNode node, String id, OID oid )
    {
        super ( new DataItemInformationBase ( id, EnumSet.of ( IODirection.INPUT, IODirection.OUTPUT ) ) );

        _log.debug ( "new snmp item" );
        
        _node = node;
        _oid = oid;
        
        _attributes = new AttributeManager ( this );
    }
    
    public void start ()
    {
        //_node.getScheduler ().addJob ( this, 1000, true );
        _node.getBulkReader ().add ( this );
    }
    
    public void stop ()
    {
        //_node.getScheduler ().removeJob ( this );
        _node.getBulkReader ().remove ( this );
        
        updateValue ( new Variant () );
    }
    
    synchronized private void updateValue ( Variant value )
    {
        _log.debug ( "Value update: " + value.asString ( "<null>" ) );
        if ( !_value.equals ( value ) )
        {
            _value = value;
            notifyValue ( _value );
        }
    }

    public Map<String, Variant> getAttributes ()
    {
        return _attributes.get ();  
    }

    public Variant getValue () throws InvalidOperationException
    {
        // sending cache value
        return _value;
    }

    public void setAttributes ( Map<String, Variant> attributes )
    {
        // no op
    }

    public void setValue ( Variant value ) throws InvalidOperationException, NullValueException, NotConvertableException
    {
        // no op
    }

    public void run ()
    {
        _log.debug ( "Updating value" );
        
        try
        {
            read ();
        }
        catch ( Exception e )
        {
           readFailed ( e );
        }
        
        _log.debug ( "Update complete" );
    }
    
    private Variant convertValue ( VariableBinding vb ) throws Exception
    {
        if ( vb == null )
        {
            throw new Exception ( "Empty reply" );
        }
        
        if ( vb.isException () )
        {
            throw new Exception ( vb.toString () );
        }
        
        Variable v = vb.getVariable ();
        _log.debug ( "Variable is: " + v );
        if ( v instanceof Null )
            return new Variant ();
        else
            return new Variant ( v.toString () );
    }
    
    public void readFailed ( Throwable t )
    {
        setError ( t.getMessage () );
    }
    
    public void readComplete ( VariableBinding vb )
    {
        try
        {
            updateValue ( convertValue ( vb ) );
            clearError ();
        }
        catch ( Exception e )
        {
            _log.error ( "Item read failed: ", e );
            setError ( e );
        }
    }
    
    private void read () throws Exception
    {
        ResponseEvent response = _node.getConnection ().sendGET ( _oid );
        
        if ( response == null )
        {
            throw new Exception ( "No response" );
        }
        
        PDU reply = response.getResponse ();
        if ( reply == null )
        {
            throw new Exception ( "No reply" );
        }
        
        readComplete ( reply.get ( 0 ) );
    }
    
    private void clearError ()
    {
        setError ( (String)null );
    }
    
    private void setError ( Throwable t )
    {
        if ( t.getMessage () == null )
            setError ( t.toString () );
        else
            setError ( t.getMessage () );
    }
    
    private void setError ( String text )
    {
        _log.info ( "Setting error: " + text );
        
        if ( text == null )
        {
            _attributes.update ( "error", null );
        }
        else
        {
            _attributes.update ( "error", new Variant ( text ) );
        }
    }

    public OID getOID ()
    {
        return _oid;
    }

}
