package org.openscada.da.server.common.impl.stats;

public class CounterValue implements Tickable
{
    private long _total = 0;
    private long _lastTick = 0;
    private long _lastTimestamp = 0;
    private CounterOutput _output;
    
    public synchronized void add ( long value )
    {
        _total += value;
        _lastTick += Math.abs ( value );
    }
    
    public synchronized void tick ()
    {
        // get now
        long ts = System.currentTimeMillis ();
        
        // get the difference ( in seconds )
        long diff = ( ts - _lastTimestamp ) / 1000;
        _lastTimestamp = ts;
        
        // just in case
        if ( diff == 0 )
        {
            diff = 1;
        }
        
        // calculate the average
        double avg = _lastTick / ((double)diff);
        _output.setTickValue ( avg, _total );
        
        _lastTick = 0;
    }

    public void setOutput ( CounterOutput output )
    {
        _output = output;
    }
}
