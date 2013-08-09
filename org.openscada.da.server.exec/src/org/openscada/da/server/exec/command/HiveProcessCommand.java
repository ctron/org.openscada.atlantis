/*
 * This file is part of the OpenSCADA project
 * 
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 * Copyright (C) 2013 Jens Reimann (ctron@dentrassi.de)
 *
 * OpenSCADA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3
 * only, as published by the Free Software Foundation.
 *
 * OpenSCADA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License version 3 for more details
 * (a copy is included in the LICENSE file that accompanied this code).
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with OpenSCADA. If not, see
 * <http://opensource.org/licenses/lgpl-3.0.html> for a copy of the LGPLv3 License.
 */

package org.openscada.da.server.exec.command;

import java.util.HashMap;
import java.util.Map;

import org.openscada.core.Variant;
import org.openscada.core.VariantEditor;
import org.openscada.da.server.browser.common.FolderCommon;
import org.openscada.da.server.common.AttributeMode;
import org.openscada.da.server.common.chain.DataItemInputChained;
import org.openscada.da.server.common.item.factory.FolderItemFactory;
import org.openscada.da.server.exec.Hive;
import org.openscada.da.server.exec.splitter.SplitSplitter;

public class HiveProcessCommand extends AbstractContinuousCommand
{

    private final VariantEditor variantEditor = new VariantEditor ();

    private FolderItemFactory processItemFactory;

    private final Map<String, DataItemInputChained> items = new HashMap<String, DataItemInputChained> ();

    public HiveProcessCommand ( final String id, final ProcessConfiguration processConfiguration, final int restartDelay, final int maxInputBuffer )
    {
        super ( id, processConfiguration, restartDelay, maxInputBuffer, new SplitSplitter ( System.getProperty ( "line.separator" ) ) );
    }

    @Override
    public void start ( final Hive hive, final FolderCommon parentFolder )
    {
        super.start ( hive, parentFolder );
        this.processItemFactory = this.itemFactory.createSubFolderFactory ( "values" );
    }

    @Override
    public void stop ()
    {
        disposeItems ();
        super.stop ();
    }

    @Override
    protected void handleStdLine ( final String line )
    {
        final String[] toks = split ( line );
        if ( toks.length < 1 )
        {
            return;
        }

        if ( "DATA-CHANGED".equals ( toks[0] ) )
        {
            final String itemId = toks[1];
            final String value = toks[2];
            final DataItemInputChained item = this.items.get ( itemId );
            if ( item == null )
            {
                return;
            }

            Variant variant = null;
            this.variantEditor.setAsText ( value );
            variant = (Variant)this.variantEditor.getValue ();

            // get the attributes
            final Map<String, Variant> attributes = new HashMap<String, Variant> ();
            for ( int i = 3; i < toks.length; i++ )
            {
                final String attribute = toks[i];
                final String[] attr = attribute.split ( "=", 2 );
                if ( attr.length > 1 )
                {
                    this.variantEditor.setAsText ( attr[1] );
                    attributes.put ( attr[0], (Variant)this.variantEditor.getValue () );
                }
                else
                {
                    attributes.put ( attr[0], null );
                }

            }
            item.updateData ( variant, attributes, AttributeMode.SET );
        }
        else if ( "REGISTER".equals ( toks[0] ) )
        {
            final String itemId = toks[1];
            if ( !this.items.containsKey ( itemId ) )
            {
                final DataItemInputChained item = this.processItemFactory.createInput ( itemId, null );
                this.items.put ( itemId, item );
            }
        }
        else if ( "UNREGISTER".equals ( toks[0] ) )
        {
            final String itemId = toks[1];
            final DataItemInputChained item = this.items.get ( itemId );
            if ( item != null )
            {
                this.processItemFactory.disposeItem ( item );
            }
        }
    }

    private String[] split ( final String line )
    {
        return line.split ( "/" );
    }

    @Override
    protected void processFailed ( final Throwable e )
    {
        this.processItemFactory.disposeAllItems ();
        this.items.clear ();
        super.processFailed ( e );
    }

    private void disposeItems ()
    {
        if ( this.processItemFactory != null )
        {
            this.processItemFactory.dispose ();
            this.processItemFactory = null;
        }
        this.items.clear ();
    }

}
