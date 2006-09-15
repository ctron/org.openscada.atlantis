package org.openscada.da.core.common.configuration.test;

import java.io.File;
import java.io.IOException;

import org.apache.xmlbeans.XmlException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openscada.da.hive.HiveDocument;

public class XMLConfiguratorTest
{
    private HiveDocument _document = null;
    private static final String _testFileBase = "test/";
    
    @After
    public void cleanup ()
    {
        _document = null;
    }
    
    public void setup ( File file ) throws XmlException, IOException
    {
        _document = HiveDocument.Factory.parse ( file );
    }
    
    @Test
    public void test1 () throws XmlException, IOException
    {
        setup ( new File ( _testFileBase + "test1.xml" ) );
    }
}
