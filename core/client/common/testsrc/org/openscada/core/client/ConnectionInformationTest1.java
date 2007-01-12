package org.openscada.core.client;

import static org.junit.Assert.*;

import org.junit.Test;

public class ConnectionInformationTest1
{
    
    protected void testParserEqual ( String uri, ConnectionInformation expected )
    {
        ConnectionInformation actual = ConnectionInformation.fromURI ( uri );
        assertNotNull ( "Object is null", actual );
        assertEquals ( expected, actual );
    }
    
    @Test
    public void test1 () throws Throwable
    {
        ConnectionInformation expected = new ConnectionInformation ();
        expected.setInterface ( "da" );
        expected.setDriver ( "net" );
        expected.setTarget ( "localhost" );
        expected.setSecondaryTarget ( 1202 );
        
        testParserEqual ( "da:net://localhost:1202", expected );
    }
    
    @Test
    public void test2 () throws Throwable
    {
        ConnectionInformation expected = new ConnectionInformation ();
        expected.setInterface ( "da" );
        expected.getProperties ().put ( "user", "jens" );
        expected.getProperties ().put ( "password", "test" );
        expected.setDriver ( "net" );
        expected.setTarget ( "localhost" );
        expected.setSecondaryTarget ( 1202 );
        
        testParserEqual ( "da:net://jens:test@localhost:1202", expected );
    }
    
    @Test
    public void test3 () throws Throwable
    {
        ConnectionInformation expected = new ConnectionInformation ();
        expected.setInterface ( "da" );
        expected.setDriver ( "net" );
        expected.setTarget ( "localhost" );
        expected.setSecondaryTarget ( 1202 );
        expected.getSubtargets ().add ( "sub1" );
        expected.getSubtargets ().add ( "sub2" );
        
        testParserEqual ( "da:net://localhost:1202/sub1/sub2", expected );
    }
    
    @Test
    public void test4 () throws Throwable
    {
        ConnectionInformation expected = new ConnectionInformation ();
        expected.setInterface ( "da" );
        expected.setDriver ( "net" );
        expected.setTarget ( "localhost" );
        expected.setSecondaryTarget ( 1202 );
        
        testParserEqual ( "da:net://localhost:1202/", expected );
    }
    
    @Test
    public void test5 () throws Throwable
    {
        ConnectionInformation expected = new ConnectionInformation ();
        expected.setInterface ( "da" );
        expected.setDriver ( "net" );
        expected.setTarget ( "localhost" );
        expected.setSecondaryTarget ( 1202 );
        expected.getProperties ().put ( "key", "value" );
        
        testParserEqual ( "da:net://localhost:1202?key=value", expected );
    }
    
    @Test
    public void test6 () throws Throwable
    {
        ConnectionInformation expected = new ConnectionInformation ();
        expected.setInterface ( "da" );
        expected.setDriver ( "net" );
        expected.setTarget ( "localhost" );
        expected.setSecondaryTarget ( 1202 );
        expected.getProperties ().put ( "key1", "value1" );
        expected.getProperties ().put ( "key2", "value2" );
        
        testParserEqual ( "da:net://localhost:1202?key1=value1&key2=value2", expected );
    }
    
    @Test
    public void test6a () throws Throwable
    {
        ConnectionInformation expected = new ConnectionInformation ();
        expected.setInterface ( "da" );
        expected.setDriver ( "net" );
        expected.setTarget ( "localhost" );
        expected.setSecondaryTarget ( 1202 );
        expected.getProperties ().put ( "key1", "value1" );
        expected.getProperties ().put ( "key2", "value2" );
        
        testParserEqual ( "da:net://localhost:1202?key2=value2&key1=value1", expected );
    }
    
    @Test
    public void test6b () throws Throwable
    {
        ConnectionInformation expected = new ConnectionInformation ();
        expected.setInterface ( "da" );
        expected.setDriver ( "net" );
        expected.setTarget ( "localhost" );
        expected.setSecondaryTarget ( 1202 );
        expected.getProperties ().put ( "key1", "value1" );
        expected.getProperties ().put ( "key2", "value2" );
        
        testParserEqual ( "da:net://localhost:1202?key2=value3&key1=value1&key2=value2", expected );
    }
    
    @Test
    public void test7 () throws Throwable
    {
        ConnectionInformation expected = new ConnectionInformation ();
        expected.setInterface ( "da" );
        expected.setDriver ( "net" );
        expected.getProperties ().put ( "user", "jens" );
        expected.getProperties ().put ( "password", "test" );
        expected.setTarget ( "localhost" );
        expected.getSubtargets ().add ( "sub1" );
        expected.getSubtargets ().add ( "sub2" );
        expected.setSecondaryTarget ( 1202 );
        expected.getProperties ().put ( "key1", "value1" );
        expected.getProperties ().put ( "key2", "value2" );
        
        testParserEqual ( "da:net://jens:test@localhost:1202/sub1/sub2?key1=value1&key2=value2", expected );
    }
    
    @Test
    public void test8 () throws Throwable
    {
        ConnectionInformation expected = new ConnectionInformation ();
        expected.setInterface ( "da" );
        expected.setDriver ( "net" );
        expected.getProperties ().put ( "user", ":" );
        expected.getProperties ().put ( "password", ":" );
        expected.setTarget ( "localhost" );
        expected.getSubtargets ().add ( ":" );
        expected.getSubtargets ().add ( ":" );
        expected.setSecondaryTarget ( 1202 );
        expected.getProperties ().put ( ":", ":" );
        
        testParserEqual ( "da:net://%3A:%3A@localhost:1202/%3A/%3A?%3A=%3A", expected );
    }
    
    @Test
    public void test9 () throws Throwable
    {
        ConnectionInformation expected = new ConnectionInformation ();
        expected.setInterface ( "da" );
        expected.setDriver ( "net" );
        expected.getProperties ().put ( "user", "&" );
        expected.getProperties ().put ( "password", "&" );
        expected.setTarget ( "localhost" );
        expected.getSubtargets ().add ( "&" );
        expected.getSubtargets ().add ( "&" );
        expected.setSecondaryTarget ( 1202 );
        expected.getProperties ().put ( "&", "&" );
        
        testParserEqual ( "da:net://%26:%26@localhost:1202/%26/%26?%26=%26", expected );
    }
    
    @Test
    public void test10 () throws Throwable
    {
        ConnectionInformation expected = new ConnectionInformation ();
        expected.setInterface ( "da" );
        expected.setDriver ( "net" );
        expected.getProperties ().put ( "user", "%" );
        expected.getProperties ().put ( "password", "%" );
        expected.setTarget ( "localhost" );
        expected.getSubtargets ().add ( "%" );
        expected.getSubtargets ().add ( "%" );
        expected.setSecondaryTarget ( 1202 );
        expected.getProperties ().put ( "%", "%" );
        
        testParserEqual ( "da:net://%25:%25@localhost:1202/%25/%25?%25=%25", expected );
    }
    
    @Test
    public void test11 () throws Throwable
    {
        ConnectionInformation expected = new ConnectionInformation ();
        expected.setInterface ( "da" );
        expected.setDriver ( "net" );
        expected.getProperties ().put ( "user", "%" );
        expected.getProperties ().put ( "password", "%" );
        expected.setTarget ( "localhost" );
        expected.getSubtargets ().add ( "%" );
        expected.getSubtargets ().add ( "%" );
        expected.setSecondaryTarget ( 1202 );
        expected.getProperties ().put ( "%", "%" );
        
        testParserEqual ( "da:net://%25:%25@localhost:1202/%25/%25?%25=%25", expected );
    }
    
    @Test
    public void test12 () throws Throwable
    {
        ConnectionInformation o = new ConnectionInformation ();
        o.setInterface ( "da" );
        o.setDriver ( "net" );
        o.getProperties ().put ( "user", "jens" );
        o.getProperties ().put ( "password", "test" );
        o.setTarget ( "localhost" );
        o.setSecondaryTarget ( 1202 );
        o.getSubtargets ().add ( "sub1" );
        o.getSubtargets ().add ( "sub2" );
        o.getProperties ().put ( "key", "value" );
        assertEquals ( o.toString (), "da:net://jens:test@localhost:1202/sub1/sub2?key=value" );
    }
}
