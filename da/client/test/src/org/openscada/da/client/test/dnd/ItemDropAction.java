package org.openscada.da.client.test.dnd;

import org.eclipse.ui.part.IDropActionDelegate;

public class ItemDropAction implements IDropActionDelegate
{

    public boolean run ( Object source, Object target )
    {
        System.out.println ( "Drop action" );
        // TODO Auto-generated method stub
        return false;
    }

}
