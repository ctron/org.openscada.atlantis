/**
 * 
 */
package org.openscada.da.core;

import java.util.HashMap;


public class WriteAttributeResults extends HashMap<String,WriteAttributeResult>
{

    /**
     * 
     */
    private static final long serialVersionUID = 6767947169827708138L;
    
    public boolean isSuccess ()
    {
        for ( WriteAttributeResult writeAttributeResult : values () )
        {
            if ( writeAttributeResult.isError () )
                return false;
        }
        return true;
    }
}