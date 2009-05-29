/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2009 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
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
