package org.openscada.da.client.test.generator;

import org.eclipse.swt.widgets.Display;
import org.openscada.core.Variant;
import org.openscada.da.client.Connection;
import org.openscada.da.client.WriteOperationCallback;

public class BooleanGenerator
{
    public enum State {
        START_DELAY,
        END_DELAY,
    }
    private Display display;

    private int startDelay = 1000;

    private int endDelay = 1000;

    private int iterations = -1;

    private boolean running = false;
    
    private long lastTick = 0;
    
    private State currentState;

    private Connection connection;

    private String itemId;

    public BooleanGenerator ( Display display, Connection connection, String itemId )
    {
        this.display = display;
        this.connection = connection;
        this.itemId = itemId;
    }

    public void start ()
    {
        if ( running )
        {
            return;
        }
        lastTick = System.currentTimeMillis ();
        currentState = State.START_DELAY;
        running = true;
        triggerRun ();
    }

    private void triggerRun ()
    {
        this.display.timerExec ( 100, new Runnable () {

            public void run ()
            {
                if ( BooleanGenerator.this.running )
                {
                    BooleanGenerator.this.tick ();
                    BooleanGenerator.this.triggerRun ();
                }
            }
        } );
    }

    public void stop ()
    {
        if ( !running )
        {
            running = false;
            return;
        }
    }

    public void dispose ()
    {
        stop ();
        display = null;
    }
    
    protected void tick ()
    {
        long now = System.currentTimeMillis ();
        switch ( currentState )
        {
        case START_DELAY:
            if ( now - lastTick > startDelay )
            {
                currentState = State.END_DELAY;
                lastTick = now; 
                writeValue ( true );
            }
            break;
        case END_DELAY:
            if ( now - lastTick > endDelay )
            {
                currentState = State.START_DELAY;
                lastTick = now;
                if ( iterations > 0 )
                {
                    iterations--;
                }
                writeValue ( false );
            }
            break;
        }
        
        if ( iterations == 0 )
        {
            running = false;
        }
    }

    public int getStartDelay ()
    {
        return startDelay;
    }

    public void setStartDelay ( int startDelay )
    {
        this.startDelay = startDelay;
    }

    public int getEndDelay ()
    {
        return endDelay;
    }

    public void setEndDelay ( int endDelay )
    {
        this.endDelay = endDelay;
    }

    public int getIterations ()
    {
        return iterations;
    }

    public void setIterations ( int iterations )
    {
        this.iterations = iterations;
    }
    
    public void writeValue ( boolean value )
    {
        this.connection.write ( this.itemId, new Variant ( value ), new WriteOperationCallback () {

            public void complete ()
            {
            }

            public void error ( Throwable e )
            {
            }

            public void failed ( String error )
            {
            }} );
    }

}
