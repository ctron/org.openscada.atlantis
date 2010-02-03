package org.openscada.core.server.common;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.openscada.utils.lang.Immutable;

@Immutable
public class UserInformation
{
    private final String name;

    private final Set<String> roles;

    public UserInformation ( final String name, final Set<String> roles )
    {
        this.name = name;
        this.roles = new HashSet<String> ( roles );
    }

    public UserInformation ( final String name, final String[] roles )
    {
        this.name = name;
        this.roles = Collections.unmodifiableSet ( new HashSet<String> ( Arrays.asList ( roles ) ) );
    }

    public String getName ()
    {
        return this.name;
    }

    public Set<String> getRoles ()
    {
        // is unmodifiable
        return this.roles;
    }

    public boolean hasRole ( final String role )
    {
        return this.roles.contains ( role );
    }

    public boolean hasAllRoles ( final String[] roles )
    {
        return this.roles.containsAll ( Arrays.asList ( roles ) );
    }

    public boolean hasAllRoles ( final Collection<String> roles )
    {
        return this.roles.containsAll ( roles );
    }

    public boolean hasAnyRole ( final String[] roles )
    {
        for ( final String role : roles )
        {
            if ( this.roles.contains ( role ) )
            {
                return true;
            }
        }
        return false;
    }

    public boolean hasAnyRole ( final Collection<String> roles )
    {
        for ( final String role : roles )
        {
            if ( this.roles.contains ( role ) )
            {
                return true;
            }
        }
        return false;
    }

}
