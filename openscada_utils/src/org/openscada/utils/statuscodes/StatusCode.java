/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

package org.openscada.utils.statuscodes;

public class StatusCode
{
    private String moduleCode;

    private String subModuleCode;

    private long code;

    private SeverityLevel severity;

    public StatusCode ( String module, String subModule, long code, SeverityLevel severity )
    {
        moduleCode = module;
        subModuleCode = subModule;
        this.code = code;
        this.severity = severity;
    }

    public String getModuleCode ()
    {
        return moduleCode;
    }

    public String getSubModuleCode ()
    {
        return subModuleCode;
    }

    public long getNumberCode ()
    {
        return code;
    }

    public SeverityLevel getSeverity ()
    {
        return severity;
    }

    public String toString ()
    {
        String statusCode = String.format ( "%s-%s-%08X", moduleCode, subModuleCode, code );
        return statusCode;
    }

    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) ( code ^ ( code >>> 32 ) );
        result = prime * result + ( ( moduleCode == null ) ? 0 : moduleCode.hashCode () );
        result = prime * result + ( ( subModuleCode == null ) ? 0 : subModuleCode.hashCode () );
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
        final StatusCode other = (StatusCode)obj;
        if ( code != other.code )
            return false;
        if ( moduleCode == null )
        {
            if ( other.moduleCode != null )
                return false;
        }
        else if ( !moduleCode.equals ( other.moduleCode ) )
            return false;
        if ( subModuleCode == null )
        {
            if ( other.subModuleCode != null )
                return false;
        }
        else if ( !subModuleCode.equals ( other.subModuleCode ) )
            return false;
        return true;
    }

}
