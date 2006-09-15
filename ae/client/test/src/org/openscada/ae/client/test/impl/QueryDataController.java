/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006 inavare GmbH (http://inavare.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscada.ae.client.test.impl;

import org.openscada.ae.core.EventInformation;
import org.openscada.ae.core.Listener;

public class QueryDataController implements Listener
{
    
    private QueryDataModel _model = null;
    
    public QueryDataController ( QueryDataModel model )
    {
        super ();
        _model = model;
    }

    public void events ( EventInformation[] eventInformations )
    {
        QueryDataModel.UpdateData updateData = _model.new UpdateData ();
        
        synchronized ( _model )
        {
            for ( EventInformation eventInformation : eventInformations )
            {
                switch ( eventInformation.getAction () )
                {
                case EventInformation.ACTION_ADDED:
                    {
                        EventData eventData = new EventData ( eventInformation.getEvent (), _model );
                        addEvent ( eventData );
                        updateData.added.add ( eventData );
                        updateData.removed.remove ( eventData );
                        break;
                    }
                case EventInformation.ACTION_REMOVED:
                    {
                        EventData eventData = new EventData ( eventInformation.getEvent (), _model );
                        removeEvent ( eventData );
                        updateData.removed.add ( eventData );
                        updateData.added.remove ( eventData );
                        break;
                    }
                }
            }
            _model.notifyUpdates ( updateData );
        }
    }
    
    protected void removeEvent ( EventData eventData )
    {
        _model.removeEvent ( eventData );
    }

    protected void addEvent ( EventData eventData )
    {
        _model.addEvent ( eventData );
    }

    public void unsubscribed ( String arg0 )
    {
        _model.setUnsubscribed ( arg0 );
    }
    
   
}
