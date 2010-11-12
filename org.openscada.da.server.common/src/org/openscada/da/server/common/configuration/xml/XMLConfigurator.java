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

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.openscada.core.Variant;
import org.openscada.da.core.IODirection;
import org.openscada.da.hive.BrowserType;
import org.openscada.da.hive.FactoriesType;
import org.openscada.da.hive.FactoryType;
import org.openscada.da.hive.FolderEntryType;
import org.openscada.da.hive.FolderType;
import org.openscada.da.hive.HiveDocument;
import org.openscada.da.hive.ItemTemplateType;
import org.openscada.da.hive.ItemTemplatesType;
import org.openscada.da.hive.ItemsType;
import org.openscada.da.hive.dataItem.DataItemBaseType;
import org.openscada.da.hive.dataItem.DataItemReferenceType;
import org.openscada.da.hive.dataItem.DataItemType;
import org.openscada.da.hive.itemChain.ItemType;
import org.openscada.da.server.browser.common.Folder;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.DataItem;
import org.openscada.da.server.common.configuration.ConfigurableFactory;
import org.openscada.da.server.common.configuration.ConfigurableFolder;
import org.openscada.da.server.common.configuration.ConfigurableHive;
import org.openscada.da.server.common.configuration.ConfigurationError;
import org.openscada.da.server.common.configuration.Configurator;
import org.openscada.da.server.common.factory.ChainEntry;
import org.openscada.da.server.common.factory.DataItemFactory;
import org.openscada.da.server.common.factory.DataItemFactoryRequest;
import org.openscada.da.server.common.factory.FactoryHelper;
import org.openscada.da.server.common.factory.FactoryTemplate;
import org.openscada.utils.str.StringHelper;
import org.w3c.dom.Node;

public class XMLConfigurator implements Configurator
{
    private static Logger _log = Logger.getLogger ( XMLConfigurator.class );

    private FactoriesType _factoriesPart = null;

    private ItemTemplatesType _itemTemplatesPart = null;

    private ItemsType _itemsPart = null;

    private BrowserType _browserPart = null;

    private final Map<String, Factory> _factories = new HashMap<String, Factory> ();

    private final Map<String, Template> _templates = new HashMap<String, Template> ();

    private final Map<String, Item> _items = new HashMap<String, Item> ();

    public XMLConfigurator ( final FactoriesType factoriesPart, final ItemTemplatesType itemTemplatesPart, final ItemsType itemsPart, final BrowserType browserPart )
    {
        super ();
        this._factoriesPart = factoriesPart;
        this._itemTemplatesPart = itemTemplatesPart;
        this._itemsPart = itemsPart;
        this._browserPart = browserPart;
    }

    public XMLConfigurator ( final HiveDocument hiveDocument ) throws ConfigurationError
    {
        if ( !hiveDocument.validate () )
        {
            throw new ConfigurationError ( "Document is not valid!" );
        }

        this._factoriesPart = hiveDocument.getHive ().getFactories ();
        this._itemTemplatesPart = hiveDocument.getHive ().getItemTemplates ();
        this._itemsPart = hiveDocument.getHive ().getItems ();
        this._browserPart = hiveDocument.getHive ().getBrowser ();
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
    public synchronized void configure ( final ConfigurableHive hive ) throws ConfigurationError
    {
        this._factories.clear ();
        this._items.clear ();
        this._templates.clear ();

        configureFactories ( hive, this._factoriesPart );
        configureTemplates ( hive, this._itemTemplatesPart );
        configureItems ( hive, this._itemsPart );
        configureBrowser ( hive, this._browserPart );
    }

    @SuppressWarnings ( "unchecked" )
    private void configureFactories ( final ConfigurableHive hive, final FactoriesType factories ) throws ConfigurationError
    {
        if ( factories == null )
        {
            return;
        }

        for ( final FactoryType factory : factories.getFactoryList () )
        {
            Class factoryClass;
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
                for ( final Constructor ctor : factoryClass.getConstructors () )
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

            this._factories.put ( factory.getId (), factory2 );
        }
    }

    private void configureBrowser ( final ConfigurableHive hive, final BrowserType browser ) throws ConfigurationError
    {
        if ( browser == null )
        {
            return;
        }

        Folder folder = hive.getRootFolder ();
        if ( folder == null )
        {
            hive.setRootFolder ( folder = new FolderCommon () );
        }

        if ( ! ( folder instanceof ConfigurableFolder ) )
        {
            throw new ConfigurationError ( "Root folder does not implement ConfigurableFolder" );
        }

        final Stack<String> folderStack = new Stack<String> ();
        configureFolder ( hive, (ConfigurableFolder)folder, browser.getFolder (), folderStack );
    }

    private void configureFolder ( final ConfigurableHive hive, final ConfigurableFolder configurableFolder, final FolderType folder, final Stack<String> folderStack ) throws ConfigurationError
    {
        for ( final FolderEntryType entry : folder.getEntryList () )
        {
            final Map<String, Variant> attributes = Helper.convertAttributes ( entry.getAttributes () );
            final String name = entry.getName ();
            final FolderType subFolder = entry.getFolder ();
            final DataItemReferenceType itemReference = entry.getDataItemReference ();

            if ( subFolder != null && itemReference != null )
            {
                throw new ConfigurationError ( String.format ( "Item %s in folder %s has both folder and item reference set! Only one is allowed!", name, StringHelper.join ( folderStack, "/" ) ) );
            }

            if ( subFolder == null && itemReference == null )
            {
                throw new ConfigurationError ( String.format ( "Item %s in folder %s has neither folder nor item reference set!", name, StringHelper.join ( folderStack, "/" ) ) );
            }

            if ( subFolder != null )
            {
                final ConfigurableFolder newSubFolder = new FolderCommon ();
                configurableFolder.add ( name, newSubFolder, attributes );
                folderStack.push ( name );
                configureFolder ( hive, newSubFolder, subFolder, folderStack );
                folderStack.pop ();
            }
            else
            {
                configureItemEntry ( hive, configurableFolder, itemReference, name, attributes, folderStack );
            }
        }
    }

    private void configureItemEntry ( final ConfigurableHive hive, final ConfigurableFolder configurableFolder, final DataItemReferenceType itemReference, final String name, final Map<String, Variant> attributes, final Stack<String> folderStack ) throws ConfigurationError
    {

        if ( itemReference.getRef () != null )
        {
            final Item item = this._items.get ( itemReference.getRef () );

            if ( item == null )
            {
                throw new ConfigurationError ( String.format ( "Entry %s in folder %s is strict-referencing to item %s which is not configured. Either use weak-ref or configure item", name, StringHelper.join ( folderStack, "/" ), itemReference.getRef () ) );
            }

            // merge local browser attributes over item's browser attributes 
            final Map<String, Variant> browserAttributes = new HashMap<String, Variant> ( item.getBrowserAttributes () );
            browserAttributes.putAll ( attributes );

            configurableFolder.add ( name, item.getItem ().getInformation (), browserAttributes );
        }
        else if ( itemReference.getWeakRef () != null )
        {
            final DataItemFactoryRequest request = new DataItemFactoryRequest ();
            request.setId ( itemReference.getWeakRef () );
            request.setBrowserAttributes ( attributes );
            final DataItem dataItem = hive.retrieveItem ( request );

            if ( dataItem != null )
            {
                configurableFolder.add ( name, dataItem.getInformation (), attributes );
            }
            else
            {
                throw new ConfigurationError ( String.format ( "Entry %s in folder %s is weak-referencing to item %s which cannot be found", name, StringHelper.join ( folderStack, "/" ), itemReference.getRef () ) );
            }
        }
    }

    private void configureItems ( final ConfigurableHive hive, final ItemsType items ) throws ConfigurationError
    {
        if ( items == null )
        {
            return;
        }
        if ( items.getDataItemList () == null )
        {
            return;
        }

        for ( final DataItemType dataItem : items.getDataItemList () )
        {
            final String id = dataItem.getId ();

            final Template template = this._templates.get ( dataItem.getTemplate () );
            if ( template == null && dataItem.getTemplate () != null )
            {
                throw new ConfigurationError ( String.format ( "Item %s requires template %s which is not configured", id, dataItem.getTemplate () ) );
            }

            Item item;
            if ( template != null )
            {
                item = new Item ( id, template );
            }
            else
            {
                item = new Item ( id );
            }

            // expand local configuration to object
            overwriteWithLocal ( item, dataItem, "item", id );

            instantiateItem ( hive, item );

            this._items.put ( id, item );
        }
    }

    /**
     * instantiate an item
     * 
     * If the item is already instatiated the method will simply return.
     * 
     * Otherwise, depending if the "factory" attribute is set, it will try two different
     * approaches.
     * 
     * If the "factory" attribute is set it will try to explicitly instatiate
     * the item using the provided factory. In this case the item must not
     * already exsist in the repository. It will be instatiated and added
     * to the repository.
     * 
     * If the "factory" attribute is missing it will simply request the item from the
     * repository. In this case the repository can return an already existing item
     * or create one on the fly (possibly using its factories).
     * 
     * @param item the item to instatiate
     * @throws ConfigurationError any configuration error that prevents the item from being
     * instantiated.
     */
    private void instantiateItem ( final ConfigurableHive hive, final Item item ) throws ConfigurationError
    {
        if ( item.getItem () != null )
        {
            return;
        }

        DataItem dataItem;

        final DataItemFactoryRequest request = new DataItemFactoryRequest ();
        request.setId ( item.getId () );
        request.setBrowserAttributes ( item.getBrowserAttributes () );
        request.setItemAttributes ( item.getItemAttributes () );
        request.setItemChain ( FactoryHelper.instantiateChainList ( hive, item.getChainEntries () ) );

        if ( item.getFactory () != null )
        {
            // explicit instatiation
            _log.debug ( String.format ( "Trying to create item %s using provided item factory", item.getId () ) );

            if ( hive.lookupItem ( item.getId () ) != null )
            {
                throw new ConfigurationError ( String.format ( "Data item %s was configured to be instiated explicitly but already exists in the hive. Unable to create it twice. Remove from configuration, change item id or change to implict creating using hive/factories", item.getId () ) );
            }

            dataItem = item.getFactory ().getFactory ().create ( request );

            hive.registerItem ( dataItem );
        }
        else
        {
            // try using hive::retrieveItem
            _log.debug ( String.format ( "Trying to retrieve the item %s from the hive", item.getId () ) );

            dataItem = hive.retrieveItem ( request );
            if ( dataItem == null )
            {
                throw new ConfigurationError ( String.format ( "Unable to retrieve item %s from factory", item.getId () ) );
            }
        }

        item.setItem ( dataItem );
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
            final Factory factory = this._factories.get ( dataItemBase.getItemFactory () );
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
        if ( this._templates.containsKey ( id ) )
        {
            return this._templates.get ( id );
        }

        if ( templateStack.contains ( id ) )
        {
            throw new ConfigurationError ( String.format ( "Infinite template recursion on template %s: path is: %s", id, StringHelper.join ( templateStack, "->" ) ) );
        }

        _log.debug ( "Expanding template: " + id );

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

        this._templates.put ( id, template );
        return template;
    }
}
