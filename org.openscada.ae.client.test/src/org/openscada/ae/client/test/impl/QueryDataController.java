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

    public QueryDataController ( final QueryDataModel model )
    {
        super ();
        this._model = model;
    }

    public void events ( final EventInformation[] eventInformations )
    {
        final QueryDataModel.UpdateData updateData = this._model.new UpdateData ();

        synchronized ( this._model )
        {
            for ( final EventInformation eventInformation : eventInformations )
            {
                switch ( eventInformation.getAction () )
                {
                case ADDED:
                {
                    final EventData eventData = new EventData ( eventInformation.getEvent (), this._model );
                    addEvent ( eventData );
                    updateData.added.add ( eventData );
                    updateData.removed.remove ( eventData );
                    break;
                }
                case REMOVED:
                {
                    final EventData eventData = new EventData ( eventInformation.getEvent (), this._model );
                    removeEvent ( eventData );
                    updateData.removed.add ( eventData );
                    updateData.added.remove ( eventData );
                    break;
                }
                }
            }
            this._model.notifyUpdates ( updateData );
        }
    }

    protected void removeEvent ( final EventData eventData )
    {
        this._model.removeEvent ( eventData );
    }

    protected void addEvent ( final EventData eventData )
    {
        this._model.addEvent ( eventData );
    }

    public void unsubscribed ( final String arg0 )
    {
        this._model.setUnsubscribed ( arg0 );
    }

}
