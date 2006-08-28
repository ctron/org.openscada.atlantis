/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.openscada.da.core;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AttributesHelper
{
    /**
     * merges the difference attributes into the target
     * <p>
     * returns the real changes performed on <code>target</code> in <code>diff</code>
     * @param target the attributes to merge the difference in
     * @param change the attributes to change
     * @param diff output of real changes that were made
     */
    public static void mergeAttributes ( Map<String,Variant> target, Map<String,Variant> change, Map<String,Variant> diff )
    {
        for ( Map.Entry<String,Variant> entry : change.entrySet() )
        {
            if ( entry.getKey() == null )
                continue;
            
            if ( entry.getValue() == null )
            {
                if ( target.containsKey ( entry.getKey() ))
                {
                    target.remove ( entry.getKey() );
                    if ( diff != null )
                        diff.put ( entry.getKey(), null );
                }
            }
            else if ( entry.getValue().isNull() )
            {
                if ( target.containsKey ( entry.getKey() ) )
                {
                    target.remove(entry.getKey());
                    if ( diff != null )
                        diff.put ( entry.getKey(), null );
                }
            }
            else
            {
                if ( (diff!=null) && !target.containsKey ( entry.getKey() ) )
                    diff.put ( entry.getKey (), entry.getValue () );
                else if ( (diff!=null) && !target.get ( entry.getKey()  ).equals ( entry.getValue() ))
                    diff.put ( entry.getKey (), entry.getValue () );
                
                target.put ( new String(entry.getKey()), new Variant(entry.getValue()) );
            }
        }
    }
    
    /**
     * merges the difference attributes into the target
     * @param target the attributes to merge the difference in
     * @param change the attributes to change
     */
    public static void mergeAttributes ( Map<String,Variant> target, Map<String,Variant> change )
    {
        mergeAttributes ( target, change, null );
    }
    
    /**
     * merges the attribute differences. But respects the initial flag sent by many events.
     * 
     * in the case the difference is flagged initial the target will be cleared first. This
     * is a convenient method to easy the merge.
     * 
     * @param target the attributes to merge the difference in
     * @param diff the difference attributes
     * @param initial initial flag
     */
    public static void mergeAttributes ( Map<String,Variant> target, Map<String,Variant> diff, boolean initial )
    {
        if ( initial )
            target.clear();
        
        mergeAttributes ( target, diff );
    }

    /**
     * update the target attributes to contain only the requested attributes 
     * @param target the attributes to update the difference to
     * @param attributes the new attributes set
     * @param diff the actual difference
     */
    public static void set ( Map<String, Variant> target, Map<String, Variant> attributes, Map<String, Variant> diff )
    {
        Set<String> initialSet = new HashSet<String> ( target.keySet () );
        
        for ( Map.Entry<String, Variant> entry : attributes.entrySet () )
        {
            if ( target.containsKey ( entry.getKey () ) )
            {
                if ( ( entry.getValue () == null ) || entry.getValue ().isNull () )
                {
                    // entry removed
                    target.remove ( entry.getKey () );
                    diff.put ( entry.getKey (), null );
                }
                else
                {
                    if ( !target.get ( entry.getKey () ).equals ( entry.getValue () ) )
                    {
                        // entry changed
                        diff.put ( entry.getKey (), entry.getValue () );
                        target.put ( entry.getKey (), entry.getValue () );
                    }
                }
                initialSet.remove ( entry.getKey () );
            }
            else
            {
                if ( ( entry.getValue () != null ) && (!entry.getValue ().isNull () ) )
                {
                    // entry is new
                    diff.put ( entry.getKey (), entry.getValue () );
                    target.put ( entry.getKey (), entry.getValue () );
                }
            }
        }
        
        for ( String key : initialSet )
        {
            target.remove ( key );
            diff.put ( key, null );
        }
    }
   
}
