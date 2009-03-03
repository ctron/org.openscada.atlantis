package org.openscada.da.server.simulation.component.modules;

public class ItemAlreadyRegisteredException extends RuntimeException
{

    /**
     * 
     */
    private static final long serialVersionUID = -8771067823875105860L;

    public ItemAlreadyRegisteredException ( final String itemTag )
    {
        super ( String.format ( "Item '%s' is already registered", itemTag ) );
    }
}
