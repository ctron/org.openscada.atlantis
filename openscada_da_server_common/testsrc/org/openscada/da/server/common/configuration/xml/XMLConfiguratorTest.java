package org.openscada.da.server.common.configuration.xml;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.openscada.common.AttributeType;
import org.openscada.da.hive.HiveDocument;
import org.openscada.da.hive.dataItem.DataItemType;

public class XMLConfiguratorTest
{
    private HiveDocument _document = null;

    private static final String _testFileBase = "test/";

    private final Collection<?> _errors = new LinkedList<Object> ();

    private boolean _valid = false;

    @After
    public void cleanup ()
    {
        this._document = null;
    }

    public void setup ( final File file ) throws XmlException, IOException
    {
        final XmlOptions xmlOptions = new XmlOptions ();
        xmlOptions.setValidateOnSet ();
        xmlOptions.setErrorListener ( this._errors );
        this._document = HiveDocument.Factory.parse ( file, xmlOptions );

        this._valid = this._document.validate ( xmlOptions );
        for ( final Object o : this._errors )
        {
            System.err.println ( o.toString () );
        }
    }

    @Test
    public void test1 () throws XmlException, IOException
    {
        setup ( new File ( _testFileBase + "test1.xml" ) );

        Assert.assertNotNull ( "Document is null", this._document );
        Assert.assertEquals ( "Document is not valid", true, this._valid );
        Assert.assertNotNull ( "Hive is null", this._document.getHive () );

        Assert.assertNull ( "Browser is not null", this._document.getHive ().getBrowser () );
    }

    @Test
    public void test2 () throws XmlException, IOException
    {
        setup ( new File ( _testFileBase + "test2.xml" ) );

        Assert.assertNotNull ( "Document is null", this._document );
        Assert.assertEquals ( "Document is not valid", true, this._valid );
        Assert.assertNotNull ( "Hive is null", this._document.getHive () );

        Assert.assertNotNull ( "Browser is null", this._document.getHive ().getBrowser () );
        Assert.assertNotNull ( "Items are null", this._document.getHive ().getItems () );
        Assert.assertNotNull ( "Templates are null", this._document.getHive ().getItemTemplates () );
    }

    @Test
    public void test3 () throws XmlException, IOException
    {
        setup ( new File ( _testFileBase + "test3.xml" ) );

        Assert.assertNotNull ( "Document is null", this._document );
        Assert.assertEquals ( "Document is not valid", true, this._valid );
        Assert.assertNotNull ( "Hive is null", this._document.getHive () );

        Assert.assertNotNull ( "Browser is null", this._document.getHive ().getBrowser () );
        Assert.assertNotNull ( "Items are null", this._document.getHive ().getItems () );
        Assert.assertNotNull ( "Templates are null", this._document.getHive ().getItemTemplates () );

        final List<DataItemType> items = this._document.getHive ().getItems ().getDataItemList ();
        Assert.assertNotNull ( "Item list is null", items );
        Assert.assertEquals ( "Must contain one item", 1, items.size () );
        Assert.assertEquals ( "Item name must be 'item1'", "item1", items.get ( 0 ).getId () );
        Assert.assertNotNull ( "Attribute are null", items.get ( 0 ).getItemAttributes () );
        final List<AttributeType> attributes = items.get ( 0 ).getItemAttributes ().getAttributeList ();
        Assert.assertNotNull ( "Attributes list is null", attributes );
        Assert.assertEquals ( "Attributes size does not match", 2, attributes.size () );

        AttributeType attr;

        attr = attributes.get ( 0 );
        Assert.assertNotNull ( "Attribute is null", attr );
        Assert.assertEquals ( "Name must match", "attr1", attr.getName () );
        Assert.assertNotNull ( "String value must not be null", attr.getString () );
        Assert.assertEquals ( "String value must match", "Hello World", attr.getString () );
        Assert.assertNull ( "Void type must be null", attr.getNull () );
        Assert.assertNull ( "Boolean type must be null", attr.getBoolean () );

        attr = attributes.get ( 1 );
        Assert.assertNotNull ( "Attribute is null", attr );
        Assert.assertEquals ( "Name must match", "attr2", attr.getName () );
        Assert.assertNull ( "String value must be null", attr.getString () );
        Assert.assertNull ( "Void type must be null", attr.getNull () );
        Assert.assertNotNull ( "Boolean type must not be null", attr.getBoolean () );
        Assert.assertEquals ( "Value must match", true, attr.getBoolean ().getBooleanValue () );

    }

    @Test
    public void test4 () throws XmlException, IOException
    {
        setup ( new File ( _testFileBase + "test4.xml_invalid" ) );

        Assert.assertEquals ( "Document is valid", false, this._valid );
    }
}
