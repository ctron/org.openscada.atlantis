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

package org.openscada.ae.core;

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.core.utils.AttributesHelper;

/**
 * An ID with some attributes (metadata) attached
 * 
 * Attributed identifiers are equal by their ID only!
 * @author Jens Reimann <jens.reimann@inavare.net>
 *
 */
public class AttributedIdentifier
{
    protected String _id = null;
    protected Map<String,Variant> _attributes = null;

    public AttributedIdentifier ( AttributedIdentifier attributedIdentifier )
    {
        super ();
        _id = attributedIdentifier._id;
        _attributes = AttributesHelper.clone ( attributedIdentifier._attributes );
    }
    
    public AttributedIdentifier ( String id, Map<String, Variant> attributes )
    {
        super ();
        _id = id;
        _attributes = AttributesHelper.clone ( attributes );
    }
    
    public AttributedIdentifier ( String id )
    {
        super ();
        _id = id;
        _attributes = new HashMap<String, Variant> ();
    }
    
    public Map<String, Variant> getAttributes ()
    {
        return _attributes;
    }

    public void setAttributes ( Map<String, Variant> attributes )
    {
        _attributes = AttributesHelper.clone ( attributes );
    }

    public String getId ()
    {
        return _id;
    }

    public void setId ( String id )
    {
        _id = id;
    }

    @Override
    public int hashCode ()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ( ( _id == null ) ? 0 : _id.hashCode () );
        return result;
    }

    @Override
    public boolean equals ( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass () != obj.getClass () )
            return false;
        final AttributedIdentifier other = (AttributedIdentifier)obj;
        if ( _id == null )
        {
            if ( other._id != null )
                return false;
        }
        else
            if ( !_id.equals ( other._id ) )
                return false;
        return true;
    }

}