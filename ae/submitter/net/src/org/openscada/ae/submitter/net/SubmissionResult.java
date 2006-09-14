package org.openscada.ae.submitter.net;

public class SubmissionResult
{
    private Exception _error = null;
    
    public Exception getError ()
    {
        return _error;
    }
    
    public boolean isSuccess ()
    {
        return _error == null;
    }
    
    synchronized public void complete ()
    {
        notifyAll ();
    }
    
    synchronized public void fail ( Exception error )
    {
        _error = error;
        notifyAll ();
    }
}
