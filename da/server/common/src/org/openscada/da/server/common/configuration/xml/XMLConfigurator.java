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

    private Map<String, Factory> _factories = new HashMap<String, Factory> ();
    private Map<String, Template> _templates = new HashMap<String, Template> ();
    private Map<String, Item> _items = new HashMap<String, Item> ();

    public XMLConfigurator ( FactoriesType factoriesPart, ItemTemplatesType itemTemplatesPart, ItemsType itemsPart, BrowserType browserPart )
    {
        super ();
        _factoriesPart = factoriesPart;
        _itemTemplatesPart = itemTemplatesPart;
        _itemsPart = itemsPart;
        _browserPart = browserPart;
    }

    public XMLConfigurator ( HiveDocument hiveDocument ) throws ConfigurationError
    {
        if ( !hiveDocument.validate () )
        {
            throw new ConfigurationError ( "Document is not valid!" );
        }

        _factoriesPart = hiveDocument.getHive ().getFactories ();
        _itemTemplatesPart = hiveDocument.getHive ().getItemTemplates ();
        _itemsPart = hiveDocument.getHive ().getItems ();
        _browserPart = hiveDocument.getHive ().getBrowser ();
    }

    public XMLConfigurator ( InputStream stream ) throws ConfigurationError, XmlException, IOException
    {
        this ( HiveDocument.Factory.parse ( stream ) );
    }

    public XMLConfigurator ( File file ) throws ConfigurationError, XmlException, IOException
    {
        this ( HiveDocument.Factory.parse ( file ) );
    }

    public XMLConfigurator ( Node node ) throws ConfigurationError, XmlException
    {
        this ( HiveDocument.Factory.parse ( node ) );
    }

    /* (non-Javadoc)
     * @see org.openscada.da.server.common.configuration.Configurator#configure()
     */
    public synchronized void configure ( ConfigurableHive hive ) throws ConfigurationError
    {
        _factories.clear ();
        _items.clear ();
        _templates.clear ();

        configureFactories ( hive, _factoriesPart );
        configureTemplates ( hive, _itemTemplatesPart );
        configureItems ( hive, _itemsPart );
        configureBrowser ( hive, _browserPart );
    }

    @SuppressWarnings ( "unchecked" )
    private void configureFactories ( ConfigurableHive hive, FactoriesType factories ) throws ConfigurationError
    {
        if ( factories == null )
        {
            return;
        }

        for ( FactoryType factory : factories.getFactoryList () )
        {
            Class factoryClass;
            try
            {
                factoryClass = Class.forName ( factory.getFactoryClass () );
            }
            catch ( ClassNotFoundException e )
            {
                throw new ConfigurationError ( "Unable to find factory class: " + factory.getFactoryClass (), e );
            }

            Object factoryObject = null;
            try
            {
                for ( Constructor ctor : factoryClass.getConstructors () )
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
            catch ( Exception e )
            {
                throw new ConfigurationError ( "Unable to instantiate object for factory class: " + factory, e );
            }
            if ( ! ( factoryObject instanceof DataItemFactory ) )
            {
                throw new ConfigurationError ( String.format (
                        "Factory class %s does not implement DataItemFactory interface", factory ) );
            }

            if ( factoryObject instanceof ConfigurableFactory )
            {
                ( (ConfigurableFactory)factoryObject ).configure ( factory.newDomNode () );
            }

            hive.addItemFactory ( (DataItemFactory)factoryObject );

            // remember factory for later use
            Factory factory2 = new Factory ();
            factory2.setFactory ( (DataItemFactory)factoryObject );

            _factories.put ( factory.getId (), factory2 );
        }
    }

    private void configureBrowser ( ConfigurableHive hive, BrowserType browser ) throws ConfigurationError
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

        Stack<String> folderStack = new Stack<String> ();
        configureFolder ( hive, (ConfigurableFolder)folder, browser.getFolder (), folderStack );
    }

    private void configureFolder ( ConfigurableHive hive, ConfigurableFolder configurableFolder, FolderType folder, Stack<String> folderStack ) throws ConfigurationError
    {
        for ( FolderEntryType entry : folder.getEntryList () )
        {
            Map<String, Variant> attributes = Helper.convertAttributes ( entry.getAttributes () );
            String name = entry.getName ();
            FolderType subFolder = entry.getFolder ();
            DataItemReferenceType itemReference = entry.getDataItemReference ();

            if ( ( subFolder != null ) && ( itemReference != null ) )
                throw new ConfigurationError ( String.format (
                        "Item %s in folder %s has both folder and item reference set! Only one is allowed!", name,
                        StringHelper.join ( folderStack, "/" ) ) );

            if ( ( subFolder == null ) && ( itemReference == null ) )
                throw new ConfigurationError ( String.format (
                        "Item %s in folder %s has neither folder nor item reference set!", name, StringHelper.join (
                                folderStack, "/" ) ) );

            if ( subFolder != null )
            {
                ConfigurableFolder newSubFolder = new FolderCommon ();
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

    private void configureItemEntry ( ConfigurableHive hive, ConfigurableFolder configurableFolder, DataItemReferenceType itemReference, String name, Map<String, Variant> attributes, Stack<String> folderStack ) throws ConfigurationError
    {

        if ( itemReference.getRef () != null )
        {
            Item item = _items.get ( itemReference.getRef () );

            if ( item == null )
                throw new ConfigurationError (
                        String.format (
                                "Entry %s in folder %s is strict-referencing to item %s which is not configured. Either use weak-ref or configure item",
                                name, StringHelper.join ( folderStack, "/" ), itemReference.getRef () ) );

            // merge local browser attributes over item's browser attributes 
            Map<String, Variant> browserAttributes = new HashMap<String, Variant> ( item.getBrowserAttributes () );
            browserAttributes.putAll ( attributes );

            configurableFolder.add ( name, item.getItem (), browserAttributes );
        }
        else if ( itemReference.getWeakRef () != null )
        {
            DataItemFactoryRequest request = new DataItemFactoryRequest ();
            request.setId ( itemReference.getWeakRef () );
            request.setBrowserAttributes ( attributes );
            DataItem dataItem = hive.retrieveItem ( request );

            if ( dataItem != null )
                configurableFolder.add ( name, dataItem, attributes );
            else
                throw new ConfigurationError ( String.format (
                        "Entry %s in folder %s is weak-referencing to item %s which cannot be found", name,
                        StringHelper.join ( folderStack, "/" ), itemReference.getRef () ) );
        }
    }

    private void configureItems ( ConfigurableHive hive, ItemsType items ) throws ConfigurationError
    {
        if ( items == null )
        {
            return;
        }
        if ( items.getDataItemList () == null )
        {
            return;
        }

        for ( DataItemType dataItem : items.getDataItemList () )
        {
            String id = dataItem.getId ();

            Template template = _templates.get ( dataItem.getTemplate () );
            if ( ( template == null ) && ( dataItem.getTemplate () != null ) )
            {
                throw new ConfigurationError ( String.format ( "Item %s requires template %s which is not configured",
                        id, dataItem.getTemplate () ) );
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

            _items.put ( id, item );
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
    private void instantiateItem ( ConfigurableHive hive, Item item ) throws ConfigurationError
    {
        if ( item.getItem () != null )
        {
            return;
        }

        DataItem dataItem;

        DataItemFactoryRequest request = new DataItemFactoryRequest ();
        request.setId ( item.getId () );
        request.setBrowserAttributes ( item.getBrowserAttributes () );
        request.setItemAttributes ( item.getItemAttributes () );
        request.setItemChain ( FactoryHelper.instantiateChainList ( item.getChainEntries () ) );

        if ( item.getFactory () != null )
        {
            // explicit instatiation
            _log.debug ( String.format ( "Trying to create item %s using provided item factory", item.getId () ) );

            if ( hive.lookupItem ( item.getId () ) != null )
            {
                throw new ConfigurationError (
                        String.format (
                                "Data item %s was configured to be instiated explicitly but already exists in the hive. Unable to create it twice. Remove from configuration, change item id or change to implict creating using hive/factories",
                                item.getId () ) );
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
                throw new ConfigurationError (
                        String.format ( "Unable to retrieve item %s from factory", item.getId () ) );
            }
        }

        item.setItem ( dataItem );
    }

    private void configureTemplates ( ConfigurableHive hive, ItemTemplatesType itemTemplates ) throws ConfigurationError
    {
        if ( itemTemplates == null )
        {
            return;
        }
        if ( itemTemplates.getTemplateList () == null )
        {
            return;
        }

        Map<String, ItemTemplateType> unexpandedTemplates = new HashMap<String, ItemTemplateType> ();

        // Copy all with ID to be able to expand them easily
        for ( ItemTemplateType itemTemplate : itemTemplates.getTemplateList () )
        {
            unexpandedTemplates.put ( itemTemplate.getId (), itemTemplate );
        }

        for ( ItemTemplateType itemTemplate : itemTemplates.getTemplateList () )
        {
            Stack<String> templateStack = new Stack<String> ();
            Template template = getExpandedTemplate ( templateStack, unexpandedTemplates, itemTemplate.getId () );

            // only register with hive if a pattern is set
            if ( template.getPattern () != null )
            {
                FactoryTemplate factoryTemplate = new FactoryTemplate ();
                factoryTemplate.setPattern ( template.getPattern () );
                factoryTemplate.setBrowserAttributes ( template.getBrowserAttributes () );
                factoryTemplate.setItemAttributes ( template.getItemAttributes () );
                factoryTemplate.setChainEntries ( template.getChainEntries () );
                hive.registerTemplate ( factoryTemplate );
            }
        }
    }

    private void overwriteWithLocal ( ItemBase itemBase, DataItemBaseType dataItemBase, String objectType, String id ) throws ConfigurationError
    {

        if ( dataItemBase.getItemFactory () != null )
        {
            Factory factory = _factories.get ( dataItemBase.getItemFactory () );
            if ( factory == null )
            {
                throw new ConfigurationError ( String.format ( "%s %s requires factory %s which is not configured",
                        objectType, id, dataItemBase.getItemFactory () ) );
            }
            itemBase.setFactory ( factory );
        }

        // merge browser attributes
        try
        {
            itemBase.getBrowserAttributes ().putAll ( Helper.convertAttributes ( dataItemBase.getBrowserAttributes () ) );
        }
        catch ( Exception e )
        {
            throw new ConfigurationError ( String.format ( "Failed to merge browser attributes for %s %s", objectType,
                    id ), e );
        }

        // merge item attributes
        try
        {
            itemBase.getItemAttributes ().putAll ( Helper.convertAttributes ( dataItemBase.getItemAttributes () ) );
        }
        catch ( Exception e )
        {
            throw new ConfigurationError (
                    String.format ( "Failed to merge item attributes for %s %s", objectType, id ), e );
        }

        // merge item chains
        if ( dataItemBase.getChain () != null )
        {
            for ( ItemType chainItem : dataItemBase.getChain ().getItemList () )
            {
                Class chainItemClass;
                try
                {
                    chainItemClass = Class.forName ( chainItem.getClass1 () );
                }
                catch ( ClassNotFoundException e )
                {
                    throw new ConfigurationError ( String.format (
                            "Unable to create item element of class %s for item %s", chainItem.getClass1 (), id ), e );
                }

                ChainEntry entry = new ChainEntry ();
                entry.setWhat ( chainItemClass );

                if ( chainItem.getDirection ().toString ().equals ( "in" ) )
                    entry.setWhen ( EnumSet.of ( IODirection.INPUT ) );
                else if ( chainItem.getDirection ().toString ().equals ( "out" ) )
                    entry.setWhen ( EnumSet.of ( IODirection.OUTPUT ) );
                else if ( chainItem.getDirection ().toString ().equals ( "inout" ) )
                    entry.setWhen ( EnumSet.of ( IODirection.INPUT, IODirection.OUTPUT ) );

                itemBase.getChainEntries ().add ( entry );
            }
        }

    }

    private Template getExpandedTemplate ( Stack<String> templateStack, Map<String, ItemTemplateType> unexpandedTemplates, String id ) throws ConfigurationError
    {
        if ( _templates.containsKey ( id ) )
        {
            return _templates.get ( id );
        }

        if ( templateStack.contains ( id ) )
        {
            throw new ConfigurationError ( String.format ( "Infinite template recursion on template %s: path is: %s",
                    id, StringHelper.join ( templateStack, "->" ) ) );
        }

        _log.debug ( "Expanding template: " + id );

        templateStack.push ( id );

        ItemTemplateType itemTemplate = unexpandedTemplates.get ( id );
        if ( itemTemplate == null )
            throw new ConfigurationError ( String.format ( "Template %s is not configured", id ) );

        String extendsId = itemTemplate.getExtends ();
        Template template;
        if ( extendsId != null )
            template = new Template ( getExpandedTemplate ( templateStack, unexpandedTemplates, extendsId ) );
        else
            template = new Template ();

        // set the item pattern
        try
        {
            if ( itemTemplate.getItemPattern () != null )
            {
                Pattern pattern = Pattern.compile ( itemTemplate.getItemPattern () );
                template.setPattern ( pattern );
            }
        }
        catch ( Exception e )
        {
            throw new ConfigurationError ( String.format ( "Template %s has an invalid item pattern: %s", id,
                    itemTemplate.getItemPattern () ) );
        }

        overwriteWithLocal ( template, itemTemplate, "template", id );

        _templates.put ( id, template );
        return template;
    }
}
