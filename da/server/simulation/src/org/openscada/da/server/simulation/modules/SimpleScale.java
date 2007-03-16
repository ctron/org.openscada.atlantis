package org.openscada.da.server.simulation.modules;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.openscada.core.Variant;
import org.openscada.da.server.common.DataItemCommand;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.simulation.Hive;
import org.openscada.utils.timing.Scheduler;

public class SimpleScale extends BaseModule
{
    private static Logger _log = Logger.getLogger ( SimpleScale.class );
    
    private Thread _thread = null;
    
    private int _minDelay = 2 * 1000;
    private int _maxDelay = 10 * 1000;
    
    private int _minWeight = 10000;
    private int _maxWeight = 30000;

    private DataItemInputChained _valueInput;
    
    private static Random _random = new Random ();

    public SimpleScale ( Hive hive, String id )
    {
        super ( hive, "scale." + id );

        Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( "tag", new Variant ( "mov." + id ) );

        DataItemCommand startCommand = getOutput ( "start", attributes );
        startCommand.addListener ( new DataItemCommand.Listener () {

            public void command ( Variant value )
            {
                startWeight ();
            }
        } );
        
        _valueInput = getInput ( "value", attributes );
        _valueInput.setFilterNoChange ( false );
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
        int delay = _random.nextInt ( _maxDelay - _minDelay );
        _log.debug ( String.format ( "Weight delay: %d", delay ) );
        
        try
        {
            Thread.sleep ( delay );
        }
        catch ( InterruptedException e )
        {
        }
        
        int weight = _random.nextInt ( _maxWeight - _minWeight );
        _valueInput.updateValue ( new Variant ( weight ) );
        
        _thread = null;
    }

}
