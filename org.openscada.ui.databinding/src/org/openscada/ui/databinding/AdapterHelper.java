package org.openscada.ui.databinding;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;

public class AdapterHelper
{
    /**
     * Adapt an object to the requested target class if possible
     * <p>
     * The following order is tried:
     * <ul>
     * <li>instanceof</li>
     * <li>via {@link IAdaptable}</li>
     * <li>via {@link IAdapterManager}
     * </li>
     * </p>
     * @param target the object to convert 
     * @param adapterClass the target class
     * @return an instance of the target class or <code>null</code> if the object cannot be adapted to the target class
     */
    public static Object adapt ( final Object target, final Class<?> adapterClass )
    {
        if ( adapterClass.isInstance ( target ) )
        {
            return target;
        }
        if ( target instanceof IAdaptable )
        {
            return ( (IAdaptable)target ).getAdapter ( adapterClass );
        }
        return Platform.getAdapterManager ().getAdapter ( target, adapterClass );
    }
}
