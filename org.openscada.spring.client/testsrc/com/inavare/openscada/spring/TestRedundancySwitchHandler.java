package com.inavare.openscada.spring;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;
import org.openscada.core.NullValueException;
import org.openscada.core.OperationException;
import org.openscada.core.Variant;
import org.openscada.core.client.NoConnectionException;
import org.openscada.spring.client.Connection;
import org.openscada.spring.client.event.ItemEventAdapter;
import org.openscada.spring.client.redundancy.RedundancySwitchHandler;

public class TestRedundancySwitchHandler
{
    private final ThreadLocal<String> value = new ThreadLocal<String> ();

    class MyRedundancySwitchHandler extends RedundancySwitchHandler
    {
        public void doSwitch ()
        {
            switchConnection ();
            this.currentConnection.set ( TestRedundancySwitchHandler.this.value.get () );
        }

        public String getCurrentConnection ()
        {
            return this.currentConnection.get ();
        }

        public void setCurrentConnection ( final String value )
        {
            this.currentConnection.set ( value );
        }

        public Map<String, Boolean> getLastConnectionStates ()
        {
            return this.lastConnectionStates;
        }

        public Map<String, Boolean> getLastMasterFlags ()
        {
            return this.lastMasterFlags;
        }
    }

    class MyConnection extends Connection
    {
        @Override
        public void writeItem ( final String itemName, final Variant value ) throws NoConnectionException, OperationException
        {
            try
            {
                TestRedundancySwitchHandler.this.value.set ( value.asString () );
            }
            catch ( final NullValueException e )
            {
                TestRedundancySwitchHandler.this.value.set ( null );
            }
        }

        @Override
        public void writeAttributes ( final String itemName, final Map<String, Variant> attributes ) throws NoConnectionException, OperationException
        {
            // pass
        }
    }

    class MyItemEventAdapter extends ItemEventAdapter
    {

    }

    @Test
    public void testRedundancy1Con1Master ()
    {
        final MyRedundancySwitchHandler handler = new MyRedundancySwitchHandler ();
        final MyConnection con = new MyConnection ();
        handler.setRedundancySwitcherConnection ( con );
        this.value.set ( "con1" );
        handler.getLastConnectionStates ().put ( "con1", true );
        handler.getLastMasterFlags ().put ( "con1", true );
        handler.doSwitch ();
        assertEquals ( "con1", handler.getCurrentConnection () );
    }

    @Test
    public void testRedundancy2Con1Master () throws Exception
    {
        final MyRedundancySwitchHandler handler = new MyRedundancySwitchHandler ();
        final MyConnection con = new MyConnection ();
        handler.setRedundancySwitcherConnection ( con );
        //        handler.setTimeout ( 200L );
        this.value.set ( "con1" );
        handler.getLastConnectionStates ().put ( "con1", true );
        handler.getLastConnectionStates ().put ( "con2", true );
        handler.getLastMasterFlags ().put ( "con1", false );
        handler.getLastMasterFlags ().put ( "con2", true );
        handler.doSwitch ();
        assertEquals ( "con2", handler.getCurrentConnection () );
        handler.getLastMasterFlags ().put ( "con1", true );
        handler.getLastMasterFlags ().put ( "con2", false );
        handler.doSwitch ();
        //        assertEquals ( "con2", handler.getCurrentConnection () );
        //        Thread.sleep ( 400L );
        //        handler.doSwitch ();
        assertEquals ( "con1", handler.getCurrentConnection () );
    }

    @Test
    public void testRedundancy2ConNoMaster () throws Exception
    {
        final MyRedundancySwitchHandler handler = new MyRedundancySwitchHandler ();
        final MyConnection con = new MyConnection ();
        handler.setRedundancySwitcherConnection ( con );
        //        handler.setTimeout ( 200L );
        this.value.set ( "con2" );
        handler.getLastConnectionStates ().put ( "con1", true );
        handler.getLastConnectionStates ().put ( "con2", true );
        handler.getLastMasterFlags ().put ( "con1", false );
        handler.getLastMasterFlags ().put ( "con2", false );
        handler.doSwitch ();
        assertEquals ( "con2", handler.getCurrentConnection () );
    }

    @Test
    public void testRedundancy3ConTwoMaster () throws Exception
    {
        final MyRedundancySwitchHandler handler = new MyRedundancySwitchHandler ();
        final MyConnection con = new MyConnection ();
        handler.setRedundancySwitcherConnection ( con );
        this.value.set ( "con1" );
        handler.getLastConnectionStates ().put ( "con1", true );
        handler.getLastConnectionStates ().put ( "con2", true );
        handler.getLastConnectionStates ().put ( "con3", true );
        handler.getLastMasterFlags ().put ( "con2", false );
        handler.getLastMasterFlags ().put ( "con3", false );
        handler.doSwitch ();
        assertTrue ( "con2".equals ( handler.getCurrentConnection () ) || "con3".equals ( handler.getCurrentConnection () ) );
    }

    @Test
    public void testRedundancy4ConOneOfflineMaster () throws Exception
    {
        final MyRedundancySwitchHandler handler = new MyRedundancySwitchHandler ();
        final MyConnection con = new MyConnection ();
        handler.setRedundancySwitcherConnection ( con );
        handler.setCurrentConnection ( "con1" );
        this.value.set ( "con1" );
        handler.getLastConnectionStates ().put ( "con1", true );
        handler.getLastConnectionStates ().put ( "con2", false );
        handler.getLastConnectionStates ().put ( "con3", true );
        handler.getLastMasterFlags ().put ( "con1", false );
        handler.getLastMasterFlags ().put ( "con2", true );
        handler.getLastMasterFlags ().put ( "con3", false );
        handler.doSwitch ();
        assertEquals ( "con1", handler.getCurrentConnection () );
    }
}
