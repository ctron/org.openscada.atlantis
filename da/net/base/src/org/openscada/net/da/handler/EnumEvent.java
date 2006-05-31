package org.openscada.net.da.handler;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import org.openscada.da.core.DataItemInformation;
import org.openscada.da.core.IODirection;
import org.openscada.da.core.common.DataItemInformationBase;
import org.openscada.net.base.data.Message;
import org.openscada.net.base.data.StringValue;
import org.openscada.utils.lang.Holder;
import org.openscada.utils.str.StringEncoder;

public class EnumEvent
{
    
    public static Message create ( Collection<DataItemInformation> added, Collection<String> removed, boolean initial )
    {
        Message msg = new Message ( Messages.CC_ENUM_EVENT );
        
        if ( initial )
            msg.getValues().put ( "initial", new StringValue ( "" ) );
        
        int i;
        
        i= 0;
        for ( DataItemInformation item : added )
        {
            msg.getValues().put ( "added-" + i, new StringValue ( encode ( item ) ) );
            i++;
        }
        
        i = 0;
        for ( String item : removed )
        {
            msg.getValues().put ( "removed-" + i, new StringValue ( item ) );
            i++;
        }
        return msg;
    }
    
    public static void parse ( Message message, List<DataItemInformation> added, List<String> removed, Holder<Boolean> initial )
    {
        if ( message == null )
            return;
        if ( added == null )
            return;
        if ( removed == null )
            return;
        if ( initial == null )
            return;
        
        initial.value = message.getValues().containsKey("initial");
        
        added.clear();
        removed.clear();
        
        int i;
        
        i = 0;
        while ( message.getValues().containsKey("added-" + i) )
        {
            DataItemInformation info = decode ( message.getValues().get("added-" + i ).toString() );
            if ( info != null )
            {
                added.add ( info );
            }
            i++;
        }
        
        i = 0;
        while ( message.getValues().containsKey("removed-" + i) )
        {
            removed.add ( message.getValues().get("removed-" + i ).toString() );
            i++;
        }
    }
    
    public static String encode ( DataItemInformation info  )
    {
        String str = "";
        str += StringEncoder.encode ( info.getName () );
        str += " ";
        
        int bits = 0;
        if ( info.getIODirection ().contains ( IODirection.INPUT ))
            bits |= 1;
        if ( info.getIODirection ().contains ( IODirection.OUTPUT ))
            bits |= 2;
        
        str += bits;
        
        return str;
    }
    
    public static DataItemInformation decode ( String str )
    {
        String itemName = "";
        EnumSet<IODirection> ioDirection = EnumSet.noneOf ( IODirection.class );
        
        String [] tokens = str.split ( " " );
        
        if ( tokens.length < 2 )
            return null;


        itemName = tokens[0];

        int bits = 0;
        try
        {
            bits = Integer.valueOf ( tokens[1] );
        }
        catch ( NumberFormatException e )
        {}
        if ( (bits & 1) > 0 )
            ioDirection.add ( IODirection.INPUT );
        if ( (bits & 2) > 0 )
            ioDirection.add ( IODirection.OUTPUT );

        
        return new DataItemInformationBase ( itemName, ioDirection );
    }
}
