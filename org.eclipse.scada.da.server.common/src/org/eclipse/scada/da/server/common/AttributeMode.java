/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.eclipse.scada.da.server.common;

/**
 * An enum which defines how attributes are provided
 * @author Jens Reimann
 *
 */
public enum AttributeMode
{
    /**
     * All attributes are set. This is a full set of all attributes and not a difference
     * set. All previously known attributes have to be cleared out and only the new set
     * must be used.
     */
    SET,
    /**
     * Only changed attributes are provided. This set of attributes contains only the
     * changed ones which need to be merged with the already exisiting attributes.
     */
    UPDATE
}
