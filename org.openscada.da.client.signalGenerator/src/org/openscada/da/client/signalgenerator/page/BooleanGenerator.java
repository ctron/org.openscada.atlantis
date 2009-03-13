package org.openscada.da.client.signalgenerator.page;

import org.eclipse.swt.widgets.Display;
import org.openscada.core.Variant;
import org.openscada.da.client.WriteOperationCallback;
import org.openscada.da.client.base.item.DataItemHolder;

public class BooleanGenerator
{
    public enum State
    {
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

    private final DataItemHolder item;

    public BooleanGenerator ( final Display display, final DataItemHolder item )
    {
        this.display = display;
        this.item = item;
    }

    public void start ()
    {
        if ( this.running )
        {
            return;
        }
        this.lastTick = System.currentTimeMillis ();
        this.currentState = State.START_DELAY;
        this.running = true;
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
        if ( !this.running )
        {
            this.running = false;
            return;
        }
    }

    public void dispose ()
    {
        stop ();
        this.display = null;
    }

    protected void tick ()
    {
        final long now = System.currentTimeMillis ();
        switch ( this.currentState )
        {
        case START_DELAY:
            if ( now - this.lastTick > this.startDelay )
            {
                this.currentState = State.END_DELAY;
                this.lastTick = now;
                writeValue ( true );
            }
            break;
        case END_DELAY:
            if ( now - this.lastTick > this.endDelay )
            {
                this.currentState = State.START_DELAY;
                this.lastTick = now;
                if ( this.iterations > 0 )
                {
                    this.iterations--;
                }
                writeValue ( false );
            }
            break;
        }

        if ( this.iterations == 0 )
        {
            this.running = false;
        }
    }

    public int getStartDelay ()
    {
        return this.startDelay;
    }

    public void setStartDelay ( final int startDelay )
    {
        this.startDelay = startDelay;
    }

    public int getEndDelay ()
    {
        return this.endDelay;
    }

    public void setEndDelay ( final int endDelay )
    {
        this.endDelay = endDelay;
    }

    public int getIterations ()
    {
        return this.iterations;
    }

    public void setIterations ( final int iterations )
    {
        this.iterations = iterations;
    }

    public void writeValue ( final boolean value )
    {
        this.item.getConnection ().write ( this.item.getItemId (), new Variant ( value ), new WriteOperationCallback () {

            public void complete ()
            {
            }

            public void error ( final Throwable e )
            {
            }

            public void failed ( final String error )
            {
            }
        } );
    }

}
