package org.openscada.hd;

import org.openscada.utils.statuscodes.CodedException;

public class InvalidItemException extends CodedException
{

    public InvalidItemException ( final String item )
    {
        super ( StatusCodes.INVALID_ITEM, String.format ( "Item '%s' is unknown", item ) );

    }

    /**
     * 
     */
    private static final long serialVersionUID = -2915749558637815564L;

}
