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

package org.openscada.da.client.viewer.model.types;

public class Color
{
    private int _red = 0;
    private int _green = 0;
    private int _blue = 0;

    public Color ()
    {
    }

    public Color ( int red, int green, int blue )
    {
        _red = red;
        _green = green;
        _blue = blue;
    }

    public int getBlue ()
    {
        return _blue;
    }

    public void setBlue ( int blue )
    {
        _blue = blue;
    }

    public int getGreen ()
    {
        return _green;
    }

    public void setGreen ( int green )
    {
        _green = green;
    }

    public int getRed ()
    {
        return _red;
    }

    public void setRed ( int red )
    {
        _red = red;
    }
    
    @Override
    public String toString ()
    {
        return String.format ( "#%02x%02x%02x", _red, _green, _blue );
    }
}
