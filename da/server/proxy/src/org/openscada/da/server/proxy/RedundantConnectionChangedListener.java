package org.openscada.da.server.proxy;

import org.openscada.da.client.Connection;

/**
 * simple listener which provides capability to check if a switch
 * between redundant connection has occurred
 * @author Juergen Rose &lt;juergen.rose@inavare.net&gt;
 */
public interface RedundantConnectionChangedListener
{
    public void connectionChanged ( final String idOld, final Connection connectionOld, final String idNew, final Connection connectionNew );
}
