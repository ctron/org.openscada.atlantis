/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.eclipse.scada.ca.console;

import org.eclipse.scada.ca.FreezableConfigurationAdministrator;

public class FreezableConsole
{
    private FreezableConfigurationAdministrator admin;

    public void setAdmin ( final FreezableConfigurationAdministrator admin )
    {
        this.admin = admin;
    }

    public void freeze () throws Exception
    {
        System.out.print ( "Freezing..." );
        System.out.flush ();

        this.admin.freeze ();

        System.out.println ( "done!" );
    }

    public void thaw () throws Exception
    {
        System.out.print ( "Thawing..." );
        System.out.flush ();

        this.admin.thaw ();

        System.out.println ( "done!" );
    }
}
