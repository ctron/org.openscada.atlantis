/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 inavare GmbH (http://inavare.com)
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

package org.openscada.da.server.simulation.filesource;

import java.io.File;

/**
 * base class for building servers from office files. Current
 * implementations are {@link OpenOfficeFile} and {@link ExcelFile}. 
 *
 * @author Juergen Rose &lt;juergen.rose@inavare.net&gt;
 */
public abstract class BaseFile
{
    private final File file;

    private final File js;

    private final HiveBuilder hiveBuilder;

    protected static final int alarm_ll_col = 8;

    protected static final int alarm_l_col = 10;

    protected static final int alarm_h_col = 12;

    protected static final int alarm_hh_col = 14;

    /**
     * @param file file from which to read data item definition
     * @param js additional javascript file with custom functions for simulator processing 
     * @param port start port (each server gets a new portnumber, starting with this)
     * @throws Exception
     */
    public BaseFile ( final File file, final File js, final HiveBuilder hiveBuilder ) throws Exception
    {
        this.file = file;
        this.js = js;
        this.hiveBuilder = hiveBuilder;
    }

    /**
     * creates actual server definitions
     * @throws Exception
     */
    abstract public void configureHive () throws Exception;

    protected File getFile ()
    {
        return this.file;
    }

    protected File getJs ()
    {
        return this.js;
    }

    public HiveBuilder getHiveBuilder ()
    {
        return this.hiveBuilder;
    }
}
