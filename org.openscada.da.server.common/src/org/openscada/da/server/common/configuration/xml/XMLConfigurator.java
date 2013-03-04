/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2012 TH4 SYSTEMS GmbH (http://th4-systems.com)
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

package org.openscada.da.server.common.configuration.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Pattern;

import org.apache.xmlbeans.XmlException;
import org.openscada.da.data.IODirection;
import org.openscada.da.hive.FactoriesType;
import org.openscada.da.hive.FactoryType;
import org.openscada.da.hive.HiveDocument;
import org.openscada.da.hive.ItemTemplateType;
import org.openscada.da.hive.ItemTemplatesType;
import org.openscada.da.hive.dataItem.DataItemBaseType;
import org.openscada.da.hive.itemChain.ItemType;
import org.openscada.da.server.common.configuration.ConfigurableFactory;
import org.openscada.da.server.common.configuration.ConfigurableHive;
import org.openscada.da.server.common.configuration.ConfigurationError;
import org.openscada.da.server.common.configuration.Configurator;
import org.openscada.da.server.common.factory.ChainEntry;
import org.openscada.da.server.common.factory.DataItemFactory;
import org.openscada.da.server.common.factory.FactoryTemplate;
import org.openscada.utils.str.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

public class XMLConfigurator implements Configurator
{
    private final static Logger logger = LoggerFactory.getLogger ( XMLConfigurator.class );

    private final FactoriesType factoriesPart;

    private final ItemTemplatesType itemTemplatesPart;

    private final Map<String, Factory> factories = new HashMap<String, Factory> ();

    private final Map<String, Template> templates = new HashMap<String, Template> ();

    public XMLConfigurator ( final FactoriesType factoriesPart, final ItemTemplatesType itemTemplatesPart )
    {
        super ();
        this.factoriesPart = factoriesPart;
        this.itemTemplatesPart = itemTemplatesPart;

    }

    public XMLConfigurator ( final HiveDocument hiveDocument ) throws ConfigurationError
    {
        if ( !hiveDocument.validate () )
        {
            throw new ConfigurationError ( "Document is not valid!" );
        }

        this.factoriesPart = hiveDocument.getHive ().getFactories ();
        this.itemTemplatesPart = hiveDocument.getHive ().getItemTemplates ();

    }

    public XMLConfigurator ( final InputStream stream ) throws ConfigurationError, XmlException, IOException
    {
        this ( HiveDocument.Factory.parse ( stream ) );
    }

    public XMLConfigurator ( final File file ) throws ConfigurationError, XmlException, IOException
    {
        this ( HiveDocument.Factory.parse ( file ) );
    }

    public XMLConfigurator ( final Node node ) throws ConfigurationError, XmlException
    {
        this ( HiveDocument.Factory.parse ( node ) );
    }

    /* (non-Javadoc)
     * @see org.openscada.da.server.common.configuration.Configurator#configure()
     */
    @Override
    public synchronized void configure ( final ConfigurableHive hive ) throws ConfigurationError
    {
        this.factories.clear ();
        this.templates.clear ();

        configureFactories ( hive, this.factoriesPart );
        configureTemplates ( hive, this.itemTemplatesPart );

    }

    private void configureFactories ( final ConfigurableHive hive, final FactoriesType factories ) throws ConfigurationError
    {
        if ( factories == null )
        {
            return;
        }

        for ( final FactoryType factory : factories.getFactoryList () )
        {
            Class<?> factoryClass;
            try
            {
                factoryClass = Class.forName ( factory.getFactoryClass () );
            }
            catch ( final ClassNotFoundException e )
            {
                throw new ConfigurationError ( "Unable to find factory class: " + factory.getFactoryClass (), e );
            }

            Object factoryObject = null;
            try
            {
                for ( final Constructor<?> ctor : factoryClass.getConstructors () )
                {
                    if ( ctor.getParameterTypes ().length == 1 )
                    {
                        if ( ctor.getParameterTypes ()[0].isAssignableFrom ( hive.getClass () ) )
                        {
                            factoryObject = ctor.newInstance ( new Object[] { hive } );
                            break;
                        }
                    }
                }
                if ( factoryObject == null )
                {
                    factoryObject = factoryClass.newInstance ();
                }
            }
            catch ( final Exception e )
            {
                throw new ConfigurationError ( "Unable to instantiate object for factory class: " + factory, e );
            }
            if ( ! ( factoryObject instanceof DataItemFactory ) )
            {
                throw new ConfigurationError ( String.format ( "Factory class %s does not implement DataItemFactory interface", factory ) );
            }

            if ( factoryObject instanceof ConfigurableFactory )
            {
                ( (ConfigurableFactory)factoryObject ).configure ( factory.newDomNode () );
            }

            hive.addItemFactory ( (DataItemFactory)factoryObject );

            // remember factory for later use
            final Factory factory2 = new Factory ();
            factory2.setFactory ( (DataItemFactory)factoryObject );

            this.factories.put ( factory.getId (), factory2 );
        }
    }

    private void configureTemplates ( final ConfigurableHive hive, final ItemTemplatesType itemTemplates ) throws ConfigurationError
    {
        if ( itemTemplates == null )
        {
            return;
        }
        if ( itemTemplates.getTemplateList () == null )
        {
            return;
        }

        final Map<String, ItemTemplateType> unexpandedTemplates = new HashMap<String, ItemTemplateType> ();

        // Copy all with ID to be able to expand them easily
        for ( final ItemTemplateType itemTemplate : itemTemplates.getTemplateList () )
        {
            unexpandedTemplates.put ( itemTemplate.getId (), itemTemplate );
        }

        for ( final ItemTemplateType itemTemplate : itemTemplates.getTemplateList () )
        {
            final Stack<String> templateStack = new Stack<String> ();
            final Template template = getExpandedTemplate ( templateStack, unexpandedTemplates, itemTemplate.getId () );

            // only register with hive if a pattern is set
            if ( template.getPattern () != null )
            {
                final FactoryTemplate factoryTemplate = new FactoryTemplate ();
                factoryTemplate.setPattern ( template.getPattern () );
                factoryTemplate.setBrowserAttributes ( template.getBrowserAttributes () );
                factoryTemplate.setItemAttributes ( template.getItemAttributes () );
                factoryTemplate.setChainEntries ( template.getChainEntries () );
                hive.registerTemplate ( factoryTemplate );
            }
        }
    }

    private void overwriteWithLocal ( final ItemBase itemBase, final DataItemBaseType dataItemBase, final String objectType, final String id ) throws ConfigurationError
    {

        if ( dataItemBase.getItemFactory () != null )
        {
            final Factory factory = this.factories.get ( dataItemBase.getItemFactory () );
            if ( factory == null )
            {
                throw new ConfigurationError ( String.format ( "%s %s requires factory %s which is not configured", objectType, id, dataItemBase.getItemFactory () ) );
            }
            itemBase.setFactory ( factory );
        }

        // merge browser attributes
        try
        {
            itemBase.getBrowserAttributes ().putAll ( Helper.convertAttributes ( dataItemBase.getBrowserAttributes () ) );
        }
        catch ( final Exception e )
        {
            throw new ConfigurationError ( String.format ( "Failed to merge browser attributes for %s %s", objectType, id ), e );
        }

        // merge item attributes
        try
        {
            itemBase.getItemAttributes ().putAll ( Helper.convertAttributes ( dataItemBase.getItemAttributes () ) );
        }
        catch ( final Exception e )
        {
            throw new ConfigurationError ( String.format ( "Failed to merge item attributes for %s %s", objectType, id ), e );
        }

        // merge item chains
        if ( dataItemBase.getChain () != null )
        {
            for ( final ItemType chainItem : dataItemBase.getChain ().getItemList () )
            {
                Class<?> chainItemClass;
                try
                {
                    chainItemClass = Class.forName ( chainItem.getClass1 () );
                }
                catch ( final ClassNotFoundException e )
                {
                    throw new ConfigurationError ( String.format ( "Unable to create item element of class %s for item %s", chainItem.getClass1 (), id ), e );
                }

                final ChainEntry entry = new ChainEntry ();
                entry.setWhat ( chainItemClass );

                if ( chainItem.getDirection ().toString ().equals ( "in" ) )
                {
                    entry.setWhen ( EnumSet.of ( IODirection.INPUT ) );
                }
                else if ( chainItem.getDirection ().toString ().equals ( "out" ) )
                {
                    entry.setWhen ( EnumSet.of ( IODirection.OUTPUT ) );
                }
                else if ( chainItem.getDirection ().toString ().equals ( "inout" ) )
                {
                    entry.setWhen ( EnumSet.of ( IODirection.INPUT, IODirection.OUTPUT ) );
                }

                if ( chainItem.getLocation () == null )
                {
                    itemBase.getChainEntries ().add ( entry );
                }
                else if ( chainItem.getLocation ().equals ( ItemType.Location.APPEND ) )
                {
                    itemBase.getChainEntries ().add ( entry );
                }
                else if ( chainItem.getLocation ().equals ( ItemType.Location.PREPEND ) )
                {
                    itemBase.getChainEntries ().add ( 0, entry );
                }

            }
        }

    }

    private Template getExpandedTemplate ( final Stack<String> templateStack, final Map<String, ItemTemplateType> unexpandedTemplates, final String id ) throws ConfigurationError
    {
        if ( this.templates.containsKey ( id ) )
        {
            return this.templates.get ( id );
        }

        if ( templateStack.contains ( id ) )
        {
            throw new ConfigurationError ( String.format ( "Infinite template recursion on template %s: path is: %s", id, StringHelper.join ( templateStack, "->" ) ) );
        }

        logger.debug ( "Expanding template: {}", id );

        templateStack.push ( id );

        final ItemTemplateType itemTemplate = unexpandedTemplates.get ( id );
        if ( itemTemplate == null )
        {
            throw new ConfigurationError ( String.format ( "Template %s is not configured", id ) );
        }

        final String extendsId = itemTemplate.getExtends ();
        Template template;
        if ( extendsId != null )
        {
            template = new Template ( getExpandedTemplate ( templateStack, unexpandedTemplates, extendsId ) );
        }
        else
        {
            template = new Template ();
        }

        // set the item pattern
        try
        {
            if ( itemTemplate.getItemPattern () != null )
            {
                final Pattern pattern = Pattern.compile ( itemTemplate.getItemPattern () );
                template.setPattern ( pattern );
            }
        }
        catch ( final Exception e )
        {
            throw new ConfigurationError ( String.format ( "Template %s has an invalid item pattern: %s", id, itemTemplate.getItemPattern () ) );
        }

        overwriteWithLocal ( template, itemTemplate, "template", id );

        this.templates.put ( id, template );
        return template;
    }
}
