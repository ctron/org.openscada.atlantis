/**
 * 
 */
package org.openscada.io.lcl;

import org.apache.log4j.Logger;
import org.openscada.io.lcl.data.Response;

public class DumpStateListener implements Client.RequestStateListener
{
    private static Logger _log = Logger.getLogger ( DumpStateListener.class );

    public boolean isResponse ( Response response )
    {
        return response.getCode () == 1;
    }

    public void stateChanged ( State state, Response response )
    {
        switch ( state )
        {
        case SENT:
            _log.info ( "Request sent" );
            break;
        case RESPONSE:
            _log.info ( "Request-Response: " + response.getCode () + "/" + response.getData () );
            break;
        case ERROR:
            _log.info ( "Request error" );
            break;
        }
    }
}