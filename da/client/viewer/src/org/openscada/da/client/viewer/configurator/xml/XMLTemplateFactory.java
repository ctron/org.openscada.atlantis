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

package org.openscada.da.client.viewer.configurator.xml;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.openscada.da.client.viewer.configurator.ConfigurationError;
import org.openscada.da.client.viewer.model.DynamicObject;
import org.openscada.da.client.viewer.model.ObjectFactory;
import org.openscada.da.viewer.template.TemplateDocument;
import org.openscada.da.viewer.template.TemplateType;
import org.w3c.dom.Node;

public class XMLTemplateFactory implements ObjectFactory, XMLConfigurable
{
    @SuppressWarnings("unused")
    private static Logger _log = Logger.getLogger ( XMLTemplateFactory.class );
    
    private TemplateType _template = null;
    private XMLConfigurationContext _context = null;

    public DynamicObject create ( String id ) throws ConfigurationError
    {
        XMLContainerContext ctx = new XMLContainerContext ( _context );
        return createTemplate ( id, ctx );
    }

    private DynamicObject createTemplate ( String id, XMLContainerContext ctx ) throws ConfigurationError
    {
        return XMLConfigurator.createContainer ( ctx, id, _template );
    }

    public void configure ( XMLConfigurationContext ctx, Node node ) throws ConfigurationError
    {
        _context = ctx;
        try
        {
            TemplateDocument document = TemplateDocument.Factory.parse ( node );
            _template = document.getTemplate ();
        }
        catch ( XmlException e )
        {
           throw new ConfigurationError ( "failed to parse template factory xml", e );
        }
    }

}
