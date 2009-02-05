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

package org.openscada.utils.timing;

import org.openscada.utils.ProjectCode;
import org.openscada.utils.statuscodes.SeverityLevel;
import org.openscada.utils.statuscodes.StatusCode;

public class StatusCodes
{
    public static final StatusCode ALREADY_BOUND = new StatusCode ( ProjectCode.CODE, "TIM", 1, SeverityLevel.ERROR );

    public static final StatusCode NOT_BOUND = new StatusCode ( ProjectCode.CODE, "TIM", 2, SeverityLevel.ERROR );

    public static final StatusCode WRONG_THREAD = new StatusCode ( ProjectCode.CODE, "TIM", 3, SeverityLevel.ERROR );
}
