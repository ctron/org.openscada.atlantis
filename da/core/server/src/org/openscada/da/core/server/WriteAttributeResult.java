/**
 * 
 */
package org.openscada.da.core.server;

public class WriteAttributeResult
{
    private Throwable _error = null;
    
    public WriteAttributeResult ()
    {
    }
    
    public WriteAttributeResult ( Throwable error )
    {
        _error = error;
    }
    
    public Throwable getError ()
    {
        return _error;
    }

    public void setError ( Throwable error )
    {
        _error = error;
    }
    
    public boolean isError ()
    {
        return _error != null;
    }
    
    public boolean isSuccess ()
    {
        return _error == null;
    }
}