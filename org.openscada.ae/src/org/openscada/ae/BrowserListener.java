package org.openscada.ae;

public interface BrowserListener
{
    public void dataChanged ( BrowserEntry[] addedOrUpdated, String[] removed );
}
