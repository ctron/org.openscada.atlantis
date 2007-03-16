package org.openscada.da.server.simulation.modules;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.openscada.core.Variant;
import org.openscada.da.server.common.DataItemCommand;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.simulation.Hive;
import org.openscada.utils.collection.MapBuilder;

public class SimpleScale extends BaseModule
{
    private static Logger _log = Logger.getLogger ( SimpleScale.class );
    
    private Thread _thread = null;
    
    private int _minDelay = 2 * 1000;
    private int _maxDelay = 10 * 1000;
    
    private int _minWeight = 10000;
    private int _maxWeight = 30000;
    
    private double _errorRatio = 0.10;

    private DataItemInputChained _valueInput;

    private DataItemInputChained _errorInput;

    private DataItemInputChained _activeInput;
    
    private static Random _random = new Random ();

    public SimpleScale ( Hive hive, String id )
    {
        super ( hive, "scale." + id );

        Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( "tag", new Variant ( "scale." + id ) );

        DataItemCommand startCommand = getOutput ( "start", attributes );
        startCommand.addListener ( new DataItemCommand.Listener () {

            public void command ( Variant value )
            {
                startWeight ();
            }
        } );
        
        _valueInput = getInput ( "value", attributes );
        _valueInput.setFilterNoChange ( false );
        _errorInput = getInput ( "error", attributes );
        _activeInput = getInput ( "active", new MapBuilder<String, Variant> ( attributes ).put ( "description", new Variant ( "An indicator if a weight process is active. True means: active, false: not active" ) ).getMap () );
        _activeInput.updateValue ( new Variant ( false ) );
    }

    protected synchronized void startWeight ()
    {
        if ( _thread != null )
        {
            return;
        }
        _thread = new Thread ( new Runnable () {

            public void run ()
            {
                performWeight ();
            }
        } );
        _thread.start ();
    }

    protected void performWeight ()
    {
        int delay = _minDelay + _random.nextInt ( _maxDelay - _minDelay );
        _log.debug ( String.format ( "Weight delay: %d", delay ) );
        
        _activeInput.updateValue ( new Variant ( true ) );
        _activeInput.updateAttributes ( new MapBuilder<String, Variant> ().put ( "sim.scale.last-delay", new Variant ( delay ) ).getMap () );
        
        try
        {
            Thread.sleep ( delay );
        }
        catch ( InterruptedException e )
        {
        }
        
        boolean error = _random.nextDouble () < _errorRatio;
        
        if ( error )
        {
            int errorCode = _random.nextInt ( 255 );
            finishWithError ( errorCode );
        }
        else
        {
            int weight = _minWeight + _random.nextInt ( _maxWeight - _minWeight );
            finishWeight ( weight );    
        }
        
        _activeInput.updateValue ( new Variant ( false ) );
        
        _thread = null;
    }
    
    protected void finishWeight ( int value )
    {
        _valueInput.updateValue ( new Variant ( value ) );
        _errorInput.updateValue ( new Variant () );
    }
    
    protected void finishWithError ( int errorCode )
    {
        _valueInput.updateValue ( new Variant () );
        _errorInput.updateValue ( new Variant ( errorCode ) );
    }

}
