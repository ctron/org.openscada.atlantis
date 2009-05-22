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

package org.openscada.da.client.signalgenerator;

import org.openscada.da.client.signalgenerator.page.GeneratorPage;

public class GeneratorPageInformation
{
    private GeneratorPage generatorPage;

    private String sortKey;

    private String label;

    public GeneratorPage getGeneratorPage ()
    {
        return this.generatorPage;
    }

    public void setGeneratorPage ( final GeneratorPage generatorPage )
    {
        this.generatorPage = generatorPage;
    }

    public String getSortKey ()
    {
        return this.sortKey;
    }

    public void setSortKey ( final String sortKey )
    {
        this.sortKey = sortKey;
    }

    public String getLabel ()
    {
        return this.label;
    }

    public void setLabel ( final String label )
    {
        this.label = label;
    }

}
