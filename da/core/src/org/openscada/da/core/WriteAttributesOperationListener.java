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

import java.util.HashMap;

public interface WriteAttributesOperationListener
{
    public class Result
    {
        private Throwable _error = null;
        
        public Result ()
        {
        }
        
        public Result ( Throwable error )
        {
            _error = error;
        }
        
        public Throwable getError ()
        {
            return _error;
        }

        public void setError ( Throwable error )
        {
            _error = error;
        }
        
        public boolean isError ()
        {
            return _error != null;
        }
        
        public boolean isSuccess ()
        {
            return _error == null;
        }
    }
    
    public class Results extends HashMap<String,Result>
    {

        /**
         * 
         */
        private static final long serialVersionUID = 6767947169827708138L;
        
        public boolean isSuccess ()
        {
            for ( Result result : values () )
            {
                if ( result.isError () )
                    return false;
            }
            return true;
        }
    }
    
    void complete ( Results results );
}
