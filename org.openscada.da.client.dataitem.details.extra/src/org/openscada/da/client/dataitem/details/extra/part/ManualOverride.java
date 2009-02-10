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

package org.openscada.da.client.dataitem.details.extra.part;

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
import org.eclipse.draw2d.PolygonDecoration;
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
import org.openscada.da.base.browser.ValueType;
import org.openscada.da.client.WriteAttributeOperationCallback;
import org.openscada.da.client.dataitem.details.part.AbstractBaseDetailsPart;
import org.openscada.da.core.WriteAttributeResults;

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
    public void createPart ( final Composite parent )
    {
        super.createPart ( parent );
        parent.setLayout ( new org.eclipse.swt.layout.GridLayout ( 1, false ) );

        this.canvas = new Canvas ( parent, SWT.NONE );
        this.canvas.setLayoutData ( new org.eclipse.swt.layout.GridData ( SWT.FILL, SWT.FILL, true, true ) );
        final LightweightSystem lws = new LightweightSystem ( this.canvas );

        createManualValueComposite ( parent );

        lws.setContents ( createRoot () );
    }

    private void createManualValueComposite ( final Composite parent )
    {
        final Composite comp = new Composite ( parent, SWT.NONE );
        comp.setLayoutData ( new org.eclipse.swt.layout.GridData ( SWT.FILL, SWT.BEGINNING, true, false ) );

        comp.setLayout ( new org.eclipse.swt.layout.RowLayout ( SWT.HORIZONTAL ) );

        final org.eclipse.swt.widgets.Label label = new org.eclipse.swt.widgets.Label ( comp, SWT.NONE );
        label.setText ( "" );
        label.setAlignment ( Label.MIDDLE );

        this.manualValueText = new Text ( comp, SWT.BORDER );

        this.manualValueCombo = new Combo ( comp, SWT.READ_ONLY );

        for ( final ValueType vt : ValueType.values () )
        {
            this.manualValueCombo.add ( vt.label () );
        }
        this.manualValueCombo.select ( ValueType.STRING.ordinal () );

        final Button setButton = new Button ( comp, SWT.BORDER );
        setButton.setText ( "Set" );
        setButton.addSelectionListener ( new SelectionAdapter () {
            @Override
            public void widgetSelected ( final SelectionEvent e )
            {
                switchToManual ();
            }
        } );

        final Button clearButton = new Button ( comp, SWT.BORDER );
        clearButton.setText ( "Clear" );
        clearButton.addSelectionListener ( new SelectionAdapter () {
            @Override
            public void widgetSelected ( final SelectionEvent e )
            {
                switchToProcess ();
            }
        } );
    }

    private IFigure createRoot ()
    {
        final Figure rootFigure = new Figure ();
        rootFigure.setLayoutManager ( new GridLayout ( 2, true ) );
        rootFigure.setBackgroundColor ( ColorConstants.white );

        final Figure pvFigure = createPV ();
        final Figure mvFigure = createMV ();
        final Figure rvFigure = createRV ();

        rootFigure.add ( pvFigure, new GridData ( GridData.CENTER, GridData.CENTER, true, true, 1, 1 ) );
        rootFigure.add ( rvFigure, new GridData ( GridData.CENTER, GridData.CENTER, true, true, 1, 2 ) );
        rootFigure.add ( mvFigure, new GridData ( GridData.CENTER, GridData.CENTER, true, true, 1, 1 ) );

        rootFigure.add ( this.p2rConnection = createP2R () );
        rootFigure.add ( this.m2rConnection = createM2R () );

        return rootFigure;
    }

    private PolylineConnection createM2R ()
    {
        final PolylineConnection c = new PolylineConnection ();
        final ChopboxAnchor sourceAnchor = new ChopboxAnchor ( this.mvRect );
        final ChopboxAnchor targetAnchor = new ChopboxAnchor ( this.rvRect );
        c.setSourceAnchor ( sourceAnchor );
        c.setTargetAnchor ( targetAnchor );

        return c;
    }

    private PolylineConnection createP2R ()
    {
        final PolylineConnection c = new PolylineConnection ();
        final ChopboxAnchor sourceAnchor = new ChopboxAnchor ( this.pvRect );
        final ChopboxAnchor targetAnchor = new ChopboxAnchor ( this.rvRect );
        c.setSourceAnchor ( sourceAnchor );
        c.setTargetAnchor ( targetAnchor );

        return c;
    }

    private Figure createRV ()
    {
        final Figure rvFigure = new Figure ();
        rvFigure.setLayoutManager ( new BorderLayout () );
        final Label label = new Label ( "Result Value" );
        label.setBorder ( new MarginBorder ( 10 ) );
        rvFigure.add ( label, BorderLayout.RIGHT );

        this.rvRect = new RoundedRectangle ();
        this.rvRect.setLayoutManager ( new BorderLayout () );
        this.rvValue = new Label ();
        this.rvRect.setBackgroundColor ( ColorConstants.lightGray );
        this.rvValue.setBorder ( new MarginBorder ( 10 ) );
        this.rvRect.add ( this.rvValue, BorderLayout.CENTER );

        rvFigure.add ( this.rvRect, BorderLayout.CENTER );
        return rvFigure;
    }

    private Figure createMV ()
    {
        final Figure mvFigure = new Figure ();
        mvFigure.setLayoutManager ( new BorderLayout () );
        final Label label = new Label ( "Manual Value" );
        label.setBorder ( new MarginBorder ( 10 ) );
        mvFigure.add ( label, BorderLayout.LEFT );

        this.mvRect = new RoundedRectangle ();
        this.mvRect.setLayoutManager ( new BorderLayout () );
        this.mvValue = new Label ();
        this.mvValue.setBorder ( new MarginBorder ( 10 ) );
        this.mvRect.setBackgroundColor ( ColorConstants.lightGray );
        this.mvRect.add ( this.mvValue, BorderLayout.CENTER );

        this.mvRect.addMouseListener ( new MouseListener () {

            public void mouseDoubleClicked ( final MouseEvent me )
            {
                ManualOverride.this.switchToManual ();
            }

            public void mousePressed ( final MouseEvent me )
            {
                // TODO Auto-generated method stub

            }

            public void mouseReleased ( final MouseEvent me )
            {
                // TODO Auto-generated method stub

            }
        } );

        mvFigure.add ( this.mvRect, BorderLayout.CENTER );
        return mvFigure;
    }

    protected void switchToManual ()
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();

        Variant value = null;
        try
        {
            value = getManualSetValue ();
        }
        catch ( final NotConvertableException e1 )
        {
            // FIXME: warn
        }

        attributes.put ( "org.openscada.da.manual.value", value );
        this.itemHolder.getConnection ().writeAttributes ( this.item.getItemId (), attributes, new WriteAttributeOperationCallback () {

            public void complete ( final WriteAttributeResults result )
            {

            }

            public void error ( final Throwable e )
            {
            }

            public void failed ( final String error )
            {
            }
        } );
    }

    private Figure createPV ()
    {
        final Figure pvFigure = new Figure ();
        pvFigure.setLayoutManager ( new BorderLayout () );
        final Label label = new Label ( "Process Value" );
        label.setBorder ( new MarginBorder ( 10 ) );
        pvFigure.add ( label, BorderLayout.LEFT );

        this.pvRect = new RoundedRectangle ();
        this.pvRect.setLayoutManager ( new BorderLayout () );
        this.pvValue = new Label ();
        this.pvValue.setBorder ( new MarginBorder ( 10 ) );
        this.pvRect.setBackgroundColor ( ColorConstants.lightGray );
        this.pvRect.add ( this.pvValue, BorderLayout.CENTER );

        pvFigure.add ( this.pvRect, BorderLayout.CENTER );

        this.pvRect.addMouseListener ( new MouseListener () {

            public void mouseDoubleClicked ( final MouseEvent me )
            {
                ManualOverride.this.switchToProcess ();
            }

            public void mousePressed ( final MouseEvent me )
            {
            }

            public void mouseReleased ( final MouseEvent me )
            {
            }
        } );

        return pvFigure;
    }

    protected void switchToProcess ()
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();

        attributes.put ( "org.openscada.da.manual.value", new Variant () );
        this.itemHolder.getConnection ().writeAttributes ( this.item.getItemId (), attributes, new WriteAttributeOperationCallback () {

            public void complete ( final WriteAttributeResults result )
            {

            }

            public void error ( final Throwable e )
            {
            }

            public void failed ( final String error )
            {
            }
        } );
    }

    private Variant getManualSetValue () throws NotConvertableException
    {
        Variant value = new Variant ();

        final int idx = this.manualValueCombo.getSelectionIndex ();
        for ( final ValueType vt : ValueType.values () )
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
        this.rvValue.setText ( this.value.getValue ().toString () );
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
        final Variant manualValue = this.value.getAttributes ().get ( "org.openscada.da.manual.value" );
        final Variant processValue = this.value.getAttributes ().get ( "org.openscada.da.manual.value.original" );
        Variant processError = this.value.getAttributes ().get ( "org.openscada.da.manual.error.original" );
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

            this.p2rConnection.setTargetDecoration ( null );
            final PolygonDecoration dec = new PolygonDecoration ();
            dec.setTemplate ( PolygonDecoration.TRIANGLE_TIP );
            this.m2rConnection.setTargetDecoration ( dec );

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

            final PolygonDecoration dec = new PolygonDecoration ();
            dec.setTemplate ( PolygonDecoration.TRIANGLE_TIP );
            this.p2rConnection.setTargetDecoration ( dec );
            this.m2rConnection.setTargetDecoration ( null );

            this.pvValue.setText ( this.value.getValue ().toString () );
        }
    }
}
