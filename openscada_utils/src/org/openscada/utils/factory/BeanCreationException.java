package org.openscada.utils.factory;

import org.openscada.utils.statuscodes.CodedException;

public class BeanCreationException extends CodedException
{

    public BeanCreationException ()
    {
        super ( StatusCodes.BEAN_CREATION_ERROR );
    }

    public BeanCreationException ( String message )
    {
        super ( StatusCodes.BEAN_CREATION_ERROR, message );
    }
    
    public BeanCreationException ( Throwable error )
    {
        super ( StatusCodes.BEAN_CREATION_ERROR, error );
    }
    
    /**
     * 
     */
    private static final long serialVersionUID = -7953315772984670546L;

}
