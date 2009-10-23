package org.openscada.ae.ui.connection.commands;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.openscada.ae.ui.connection.data.ConditionStatusBean;
import org.openscada.ui.databinding.AbstractSelectionHandler;
import org.openscada.ui.databinding.AdapterHelper;

public abstract class AbstractMonitorHandler extends AbstractSelectionHandler
{
    protected List<ConditionStatusBean> getMonitors ()
    {
        final IStructuredSelection sel = getSelection ();
        if ( sel == null )
        {
            return new LinkedList<ConditionStatusBean> ();
        }

        final List<ConditionStatusBean> result = new LinkedList<ConditionStatusBean> ();

        final Iterator<?> i = sel.iterator ();
        while ( i.hasNext () )
        {
            final Object o = i.next ();
            final ConditionStatusBean bean = (ConditionStatusBean)AdapterHelper.adapt ( o, ConditionStatusBean.class );
            if ( bean != null )
            {
                result.add ( bean );
            }
        }
        return result;
    }

}