package org.openscada.da.core;

public interface WriteOperationListener
{
    void success ();
    void failure ( String errorMessage );
}
