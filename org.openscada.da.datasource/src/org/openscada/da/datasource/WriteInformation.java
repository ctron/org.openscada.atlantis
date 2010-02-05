package org.openscada.da.datasource;

import org.openscada.sec.UserInformation;
import org.openscada.utils.lang.Immutable;

/**
 * This class holds additional information for write requests
 * @author Jens Reimann
 * @since 0.15.0
 */
@Immutable
public class WriteInformation
{
    private final UserInformation userInformation;

    public WriteInformation ( final UserInformation userInformation )
    {
        this.userInformation = userInformation;
    }

    /**
     * Return the user information for this write request or <code>null</code>
     * if there is no user information available
     * @return the user information
     */
    public UserInformation getUserInformation ()
    {
        return this.userInformation;
    }
}
