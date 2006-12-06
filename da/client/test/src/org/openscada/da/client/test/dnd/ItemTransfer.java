package org.openscada.da.client.test.dnd;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * Class for serializing gadgets to/from a byte array
 */
public class ItemTransfer extends ByteArrayTransfer
{
    private static ItemTransfer instance = new ItemTransfer ();
    private static final String TYPE_NAME = "openscada-da-item-transfer-format";
    private static final int TYPEID = registerType ( TYPE_NAME );

    /**
     * Returns the singleton gadget transfer instance.
     */
    public static ItemTransfer getInstance()
    {
        return instance;
    }
    
    /**
     * Avoid explicit instantiation
     */
    private ItemTransfer()
    {
    }
    protected Item[] fromByteArray ( byte[] bytes )
    {
        DataInputStream in = new DataInputStream ( new ByteArrayInputStream ( bytes ) );

        try
        {
            /* read number of gadgets */
            int n = in.readInt();
            /* read gadgets */
            Item[] items = new Item[n];
            for ( int i = 0; i < n; i++ )
            {
                Item item = readItem ( null, in );
                if ( item == null )
                {
                    return null;
                }
                items[i] = item;
            }
            return items;
        }
        catch ( IOException e )
        {
            return null;
        }
    }
    /*
     * Method declared on Transfer.
     */
    protected int[] getTypeIds ()
    {
        return new int[] { TYPEID };
    }
    /*
     * Method declared on Transfer.
     */
    protected String[] getTypeNames ()
    {
        return new String[] { TYPE_NAME };
    }
    /*
     * Method declared on Transfer.
     */
    protected void javaToNative ( Object object, TransferData transferData )
    {
        byte[] bytes = toByteArray ( (Item[])object );
        if ( bytes != null )
            super.javaToNative ( bytes, transferData );
    }
    /*
     * Method declared on Transfer.
     */
    protected Object nativeToJava ( TransferData transferData )
    {
        byte[] bytes = (byte[])super.nativeToJava ( transferData );
        return fromByteArray ( bytes );
    }
    /**
     * Reads and returns a single gadget from the given stream.
     */
    private Item readItem ( Item parent, DataInputStream dataIn ) throws IOException
    {
        Item item = new Item ();
        item.setConnectionString ( dataIn.readUTF () );
        item.setId ( dataIn.readUTF () );
        
        return item;
    }
    
    protected byte[] toByteArray ( Item[] items)
    {

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream ();
        DataOutputStream out = new DataOutputStream ( byteOut );

        byte[] bytes = null;

        try {
            /* write number of markers */
            out.writeInt ( items.length );

            /* write markers */
            for (int i = 0; i < items.length; i++) {
                writeItem ( (Item)items[i], out );
            }
            out.close ();
            bytes = byteOut.toByteArray();
        }
        catch ( IOException e )
        {
            //when in doubt send nothing
        }
        return bytes;
    }
    
    /**
     * Writes the given item to the stream.
     */
    private void writeItem ( Item item, DataOutputStream dataOut ) throws IOException
    {
        dataOut.writeUTF ( item.getConnectionString () );
        dataOut.writeUTF ( item.getId () );
    }
}