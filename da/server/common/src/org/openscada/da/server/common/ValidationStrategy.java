package org.openscada.da.server.common;

/**
 * The default validation strategy defines how item IDs are
 * validated. 
 * @author Jens Reimann
 *
 */
public enum ValidationStrategy
{
    /**
     * Perform a full check through all possible checks.
     */
    FULL_CHECK,
    /**
     * Be permissive and grant everything.
     */
    GRANT_ALL
}
