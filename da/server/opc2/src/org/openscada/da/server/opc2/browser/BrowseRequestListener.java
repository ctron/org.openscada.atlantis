package org.openscada.da.server.opc2.browser;

public interface BrowseRequestListener
{
    public void browseComplete ( BrowseResult result );

    public void browseError ( Throwable error );
}
