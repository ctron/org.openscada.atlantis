/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2008 inavare GmbH (http://inavare.com)
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

package org.openscada.da.client.dataitem.details.extra;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.openscada.core.NotConvertableException;
import org.openscada.core.Variant;
import org.openscada.da.client.WriteAttributeOperationCallback;
import org.openscada.da.client.dataitem.details.part.AbstractBaseDetailsPart;
import org.openscada.da.core.WriteAttributeResults;
import org.openscada.rcp.da.client.browser.ValueType;

/**
 * A detail view for the manual override value, setting and getting the status
 * @author Jens Reimann
 *
 */
public class ManualOverride extends AbstractBaseDetailsPart
{

    private Canvas canvas;

    private Label pvValue;

    private Label mvValue;

    private Label rvValue;

    private RoundedRectangle rvRect;

    private RoundedRectangle mvRect;

    private RoundedRectangle pvRect;

    private PolylineConnection p2rConnection;

    private PolylineConnection m2rConnection;

    private Text manualValueText;

    private Combo manualValueCombo;

    @Override
    public void createPart ( Composite parent )
    {
        super.createPart ( parent );
        parent.setLayout ( new org.eclipse.swt.layout.GridLayout ( 1, false ) );

        this.canvas = new Canvas ( parent, SWT.NONE );
        this.canvas.setLayoutData ( new org.eclipse.swt.layout.GridData ( SWT.FILL, SWT.FILL, true, true ) );
        LightweightSystem lws = new LightweightSystem ( canvas );

        createManualValueComposite ( parent );

        lws.setContents ( createRoot () );
    }

    private void createManualValueComposite ( Composite parent )
    {
        Composite comp = new Composite ( parent, SWT.NONE );
        comp.setLayoutData ( new org.eclipse.swt.layout.GridData ( SWT.FILL, SWT.BEGINNING, true, false ) );

        comp.setLayout ( new org.eclipse.swt.layout.RowLayout ( SWT.HORIZONTAL ) );

        org.eclipse.swt.widgets.Label label = new org.eclipse.swt.widgets.Label ( comp, SWT.NONE );
        label.setText ( "" );
        label.setAlignment ( Label.MIDDLE );

        manualValueText = new Text ( comp, SWT.BORDER );

        manualValueCombo = new Combo ( comp, SWT.READ_ONLY );

        for ( ValueType vt : ValueType.values () )
        {
            manualValueCombo.add ( vt.label () );
        }
        manualValueCombo.select ( ValueType.STRING.ordinal () );

        Button setButton = new Button ( comp, SWT.BORDER );
        setButton.setText ( "Set" );
        setButton.addSelectionListener ( new SelectionAdapter () {
            @Override
            public void widgetSelected ( SelectionEvent e )
            {
                switchToManual ();
            }
        } );

        Button clearButton = new Button ( comp, SWT.BORDER );
        clearButton.setText ( "Clear" );
        clearButton.addSelectionListener ( new SelectionAdapter () {
            @Override
            public void widgetSelected ( SelectionEvent e )
            {
                switchToProcess ();
            }
        } );
    }

    private IFigure createRoot ()
    {
        Figure rootFigure = new Figure ();
        rootFigure.setLayoutManager ( new GridLayout ( 2, true ) );
        rootFigure.setBackgroundColor ( ColorConstants.white );

        Figure pvFigure = createPV ();
        Figure mvFigure = createMV ();
        Figure rvFigure = createRV ();

        rootFigure.add ( pvFigure, new GridData ( GridData.CENTER, GridData.CENTER, true, true, 1, 1 ) );
        rootFigure.add ( rvFigure, new GridData ( GridData.CENTER, GridData.CENTER, true, true, 1, 2 ) );
        rootFigure.add ( mvFigure, new GridData ( GridData.CENTER, GridData.CENTER, true, true, 1, 1 ) );

        rootFigure.add ( this.p2rConnection = createP2R () );
        rootFigure.add ( this.m2rConnection = createM2R () );

        return rootFigure;
    }

    private PolylineConnection createM2R ()
    {
        PolylineConnection c = new PolylineConnection ();
        ChopboxAnchor sourceAnchor = new ChopboxAnchor ( this.mvRect );
        ChopboxAnchor targetAnchor = new ChopboxAnchor ( this.rvRect );
        c.setSourceAnchor ( sourceAnchor );
        c.setTargetAnchor ( targetAnchor );
        return c;
    }

    private PolylineConnection createP2R ()
    {
        PolylineConnection c = new PolylineConnection ();
        ChopboxAnchor sourceAnchor = new ChopboxAnchor ( this.pvRect );
        ChopboxAnchor targetAnchor = new ChopboxAnchor ( this.rvRect );
        c.setSourceAnchor ( sourceAnchor );
        c.setTargetAnchor ( targetAnchor );
        return c;
    }

    private Figure createRV ()
    {
        Figure rvFigure = new Figure ();
        rvFigure.setLayoutManager ( new BorderLayout () );
        Label label = new Label ( "Result Value" );
        label.setBorder ( new MarginBorder ( 10 ) );
        rvFigure.add ( label, BorderLayout.RIGHT );

        rvRect = new RoundedRectangle ();
        rvRect.setLayoutManager ( new BorderLayout () );
        this.rvValue = new Label ();
        this.rvRect.setBackgroundColor ( ColorConstants.lightGray );
        this.rvValue.setBorder ( new MarginBorder ( 10 ) );
        rvRect.add ( rvValue, BorderLayout.CENTER );

        rvFigure.add ( rvRect, BorderLayout.CENTER );
        return rvFigure;
    }

    private Figure createMV ()
    {
        Figure mvFigure = new Figure ();
        mvFigure.setLayoutManager ( new BorderLayout () );
        Label label = new Label ( "Manual Value" );
        label.setBorder ( new MarginBorder ( 10 ) );
        mvFigure.add ( label, BorderLayout.LEFT );

        mvRect = new RoundedRectangle ();
        mvRect.setLayoutManager ( new BorderLayout () );
        this.mvValue = new Label ();
        this.mvValue.setBorder ( new MarginBorder ( 10 ) );
        this.mvRect.setBackgroundColor ( ColorConstants.lightGray );
        mvRect.add ( mvValue, BorderLayout.CENTER );

        mvRect.addMouseListener ( new MouseListener () {

            public void mouseDoubleClicked ( MouseEvent me )
            {
                ManualOverride.this.switchToManual ();
            }

            public void mousePressed ( MouseEvent me )
            {
                // TODO Auto-generated method stub

            }

            public void mouseReleased ( MouseEvent me )
            {
                // TODO Auto-generated method stub

            }
        } );

        mvFigure.add ( mvRect, BorderLayout.CENTER );
        return mvFigure;
    }

    protected void switchToManual ()
    {
        Map<String, Variant> attributes = new HashMap<String, Variant> ();

        Variant value = null;
        try
        {
            value = getManualSetValue ();
        }
        catch ( NotConvertableException e1 )
        {
            // FIXME: warn
        }

        attributes.put ( "org.openscada.da.manual.value", value );
        this.connection.writeAttributes ( this.item.getItemId (), attributes, new WriteAttributeOperationCallback () {

            public void complete ( WriteAttributeResults result )
            {

            }

            public void error ( Throwable e )
            {
            }

            public void failed ( String error )
            {
            }
        } );
    }

    private Figure createPV ()
    {
        Figure pvFigure = new Figure ();
        pvFigure.setLayoutManager ( new BorderLayout () );
        Label label = new Label ( "Process Value" );
        label.setBorder ( new MarginBorder ( 10 ) );
        pvFigure.add ( label, BorderLayout.LEFT );

        pvRect = new RoundedRectangle ();
        pvRect.setLayoutManager ( new BorderLayout () );
        this.pvValue = new Label ();
        this.pvValue.setBorder ( new MarginBorder ( 10 ) );
        this.pvRect.setBackgroundColor ( ColorConstants.lightGray );
        pvRect.add ( pvValue, BorderLayout.CENTER );

        pvFigure.add ( pvRect, BorderLayout.CENTER );

        pvRect.addMouseListener ( new MouseListener () {

            public void mouseDoubleClicked ( MouseEvent me )
            {
                ManualOverride.this.switchToProcess ();
            }

            public void mousePressed ( MouseEvent me )
            {
            }

            public void mouseReleased ( MouseEvent me )
            {
            }
        } );

        return pvFigure;
    }

    protected void switchToProcess ()
    {
        Map<String, Variant> attributes = new HashMap<String, Variant> ();

        attributes.put ( "org.openscada.da.manual.value", new Variant () );
        this.connection.writeAttributes ( this.item.getItemId (), attributes, new WriteAttributeOperationCallback () {

            public void complete ( WriteAttributeResults result )
            {

            }

            public void error ( Throwable e )
            {
            }

            public void failed ( String error )
            {
            }
        } );
    }

    private Variant getManualSetValue () throws NotConvertableException
    {
        Variant value = new Variant ();

        int idx = this.manualValueCombo.getSelectionIndex ();
        for ( ValueType vt : ValueType.values () )
        {
            if ( vt.ordinal () == idx )
            {
                value = vt.convertTo ( this.manualValueText.getText () );
            }
        }

        return value;
    }

    @Override
    public void dispose ()
    {
        this.canvas.dispose ();
        super.dispose ();
    }

    @Override
    protected void update ()
    {
        // set result value
        this.rvValue.setText ( this.item.getValue ().toString () );
        if ( isUnsafe () )
        {
            this.rvRect.setBackgroundColor ( ColorConstants.yellow );
        }
        else if ( isAlarm () )
        {
            this.rvRect.setBackgroundColor ( ColorConstants.red );
        }
        else if ( isManual () )
        {
            this.rvRect.setBackgroundColor ( ColorConstants.cyan );
        }
        else
        {
            this.rvRect.setBackgroundColor ( ColorConstants.lightGray );
        }

        // set manual value
        Variant manualValue = this.item.getAttributes ().get ( "org.openscada.da.manual.value" );
        Variant processValue = this.item.getAttributes ().get ( "org.openscada.da.manual.value.original" );
        Variant processError = this.item.getAttributes ().get ( "org.openscada.da.manual.error.original" );
        if ( processError == null )
        {
            processError = new Variant ( false );
        }

        if ( manualValue != null )
        {
            this.mvValue.setText ( manualValue.toString () );
        }
        else
        {
            this.mvValue.setText ( "<none>" );
        }

        if ( isManual () )
        {
            this.p2rConnection.setLineStyle ( Graphics.LINE_DASH );
            this.m2rConnection.setLineStyle ( Graphics.LINE_SOLID );

            // set process value
            if ( processValue != null )
            {
                this.pvValue.setText ( processValue.toString () );
            }
            else
            {
                this.pvValue.setText ( "<none>" );
            }

            this.pvRect.setBackgroundColor ( processError.asBoolean () ? ColorConstants.red : ColorConstants.lightGray );
        }
        else
        {
            this.p2rConnection.setLineStyle ( Graphics.LINE_SOLID );
            this.m2rConnection.setLineStyle ( Graphics.LINE_DASH );

            this.pvValue.setText ( this.item.getValue ().toString () );
        }
    }
}
