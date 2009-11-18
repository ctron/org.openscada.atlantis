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
import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.ConnectionRouter;
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
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.openscada.core.Variant;
import org.openscada.da.client.dataitem.details.part.AbstractBaseDetailsPart;

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

    private Variant manualValue;

    private RoundedRectangle rmvRect;

    private Label rmvValue;

    private RoundedRectangle rpvRect;

    private Label rpvValue;

    private PolylineConnection rp2pConnection;

    private PolylineConnection rm2pConnection;

    public void createPart ( final Composite parent )
    {
        parent.setLayout ( new org.eclipse.swt.layout.GridLayout ( 1, false ) );

        this.canvas = new Canvas ( parent, SWT.NONE );
        this.canvas.setLayoutData ( new org.eclipse.swt.layout.GridData ( SWT.FILL, SWT.FILL, true, true ) );
        final LightweightSystem lws = new LightweightSystem ( this.canvas );

        lws.setContents ( createRoot () );
    }

    private IFigure createRoot ()
    {
        final Figure baseFigure = new Figure ();

        final ConnectionLayer rootFigure = new ConnectionLayer ();
        rootFigure.setAntialias ( 1 );
        rootFigure.setConnectionRouter ( ConnectionRouter.NULL );
        baseFigure.add ( rootFigure );

        rootFigure.setLayoutManager ( new GridLayout ( 3, true ) );
        rootFigure.setBackgroundColor ( ColorConstants.white );

        final Figure rpvFigure = createRPV ();
        final Figure pvFigure = createPV ();
        final Figure rmvFigure = createRMV ();
        final Figure mvFigure = createMV ();
        final Figure rvFigure = createRV ();

        rootFigure.add ( rpvFigure, new GridData ( GridData.CENTER, GridData.CENTER, true, true, 1, 1 ) );
        rootFigure.add ( pvFigure, new GridData ( GridData.CENTER, GridData.CENTER, true, true, 1, 2 ) );
        rootFigure.add ( rvFigure, new GridData ( GridData.CENTER, GridData.CENTER, true, true, 1, 3 ) );

        rootFigure.add ( rmvFigure, new GridData ( GridData.CENTER, GridData.CENTER, true, true, 1, 1 ) );

        rootFigure.add ( mvFigure, new GridData ( GridData.CENTER, GridData.CENTER, true, true, 1, 1 ) );
        rootFigure.add ( new Figure (), new GridData ( GridData.CENTER, GridData.CENTER, true, true, 1, 1 ) ); // placeholder

        rootFigure.add ( this.p2rConnection = createConnection ( this.pvRect, this.rvRect ) );
        rootFigure.add ( this.m2rConnection = createConnection ( this.mvRect, this.rvRect ) );

        rootFigure.add ( this.rp2pConnection = createConnection ( this.rpvRect, this.pvRect ) );
        rootFigure.add ( this.rm2pConnection = createConnection ( this.rmvRect, this.pvRect ) );

        return rootFigure;
    }

    private PolylineConnection createConnection ( final IFigure source, final IFigure target )
    {
        final PolylineConnection c = new PolylineConnection ();
        final ChopboxAnchor sourceAnchor = new ChopboxAnchor ( source );
        final ChopboxAnchor targetAnchor = new ChopboxAnchor ( target );
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

    private Figure createRMV ()
    {
        final Figure rmvFigure = new Figure ();
        rmvFigure.setLayoutManager ( new BorderLayout () );
        final Label label = new Label ( "Remote Manual Value" );
        label.setBorder ( new MarginBorder ( 10 ) );
        rmvFigure.add ( label, BorderLayout.LEFT );

        this.rmvRect = new RoundedRectangle ();
        this.rmvRect.setLayoutManager ( new BorderLayout () );
        this.rmvValue = new Label ();
        this.rmvValue.setBorder ( new MarginBorder ( 10 ) );
        this.rmvRect.setBackgroundColor ( ColorConstants.lightGray );
        this.rmvRect.add ( this.rmvValue, BorderLayout.CENTER );

        this.rmvRect.addMouseMotionListener ( new MouseMotionListener.Stub () {

            public void mouseEntered ( final MouseEvent me )
            {
                ManualOverride.this.rmvRect.setLineWidth ( 2 );
            }

            public void mouseExited ( final MouseEvent me )
            {
                ManualOverride.this.rmvRect.setLineWidth ( 1 );
            }
        } );

        this.rmvRect.addMouseListener ( new MouseListener () {

            public void mouseDoubleClicked ( final MouseEvent me )
            {
                handleSetRemoteManualValue ();
            }

            public void mousePressed ( final MouseEvent me )
            {
                setRemoteManualState ( true );
            }

            public void mouseReleased ( final MouseEvent me )
            {
                // TODO Auto-generated method stub

            }
        } );

        rmvFigure.add ( this.rmvRect, BorderLayout.CENTER );
        return rmvFigure;
    }

    protected void setRemoteManualState ( final boolean state )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( "remote.manual.active", new Variant ( state ) );
        writeAttributes ( attributes );
    }

    protected void handleSetRemoteManualValue ()
    {
        final Variant value = new VariantEntryDialog ( this.shell ).getValue ();
        if ( value != null )
        {
            final Map<String, Variant> attributes = new HashMap<String, Variant> ();
            attributes.put ( "remote.manual.value", value );
            writeAttributes ( attributes );
        }
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

        this.mvRect.addMouseMotionListener ( new MouseMotionListener.Stub () {

            public void mouseEntered ( final MouseEvent me )
            {
                ManualOverride.this.mvRect.setLineWidth ( 2 );
            }

            public void mouseExited ( final MouseEvent me )
            {
                ManualOverride.this.mvRect.setLineWidth ( 1 );
            }
        } );

        this.mvRect.addMouseListener ( new MouseListener () {

            public void mouseDoubleClicked ( final MouseEvent me )
            {
                ManualOverride.this.manualValue = null;
                ManualOverride.this.switchToManual ();
            }

            public void mousePressed ( final MouseEvent me )
            {
                ManualOverride.this.switchToManual ();
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
        if ( this.manualValue == null )
        {
            // enter manual value if we don't have one
            enterManualValue ();
            if ( this.manualValue == null )
            {
                // still have none ... abort
                return;
            }
        }

        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( "org.openscada.da.manual.value", this.manualValue );
        writeAttributes ( attributes );
    }

    protected void writeAttributes ( final Map<String, Variant> attributes )
    {
        this.item.writeAtrtibutes ( attributes );
    }

    private Figure createRPV ()
    {
        final Figure rpvFigure = new Figure ();
        rpvFigure.setLayoutManager ( new BorderLayout () );
        final Label label = new Label ( "Remote Process Value" );
        label.setBorder ( new MarginBorder ( 10 ) );
        rpvFigure.add ( label, BorderLayout.LEFT );

        this.rpvRect = new RoundedRectangle ();
        this.rpvRect.setLayoutManager ( new BorderLayout () );
        this.rpvValue = new Label ();
        this.rpvValue.setBorder ( new MarginBorder ( 10 ) );
        this.rpvRect.setBackgroundColor ( ColorConstants.lightGray );
        this.rpvRect.add ( this.rpvValue, BorderLayout.CENTER );

        rpvFigure.add ( this.rpvRect, BorderLayout.CENTER );

        this.rpvRect.addMouseMotionListener ( new MouseMotionListener.Stub () {

            public void mouseEntered ( final MouseEvent me )
            {
                ManualOverride.this.rpvRect.setLineWidth ( 2 );
            }

            public void mouseExited ( final MouseEvent me )
            {
                ManualOverride.this.rpvRect.setLineWidth ( 1 );
            }
        } );
        this.rpvRect.addMouseListener ( new MouseListener () {

            public void mouseDoubleClicked ( final MouseEvent me )
            {
            }

            public void mousePressed ( final MouseEvent me )
            {
                setRemoteManualState ( false );
            }

            public void mouseReleased ( final MouseEvent me )
            {
            }
        } );

        return rpvFigure;
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

        this.pvRect.addMouseMotionListener ( new MouseMotionListener.Stub () {

            public void mouseEntered ( final MouseEvent me )
            {
                ManualOverride.this.pvRect.setLineWidth ( 2 );
            }

            public void mouseExited ( final MouseEvent me )
            {
                ManualOverride.this.pvRect.setLineWidth ( 1 );
            }
        } );
        this.pvRect.addMouseListener ( new MouseListener () {

            public void mouseDoubleClicked ( final MouseEvent me )
            {
            }

            public void mousePressed ( final MouseEvent me )
            {
                ManualOverride.this.switchToProcess ();
            }

            public void mouseReleased ( final MouseEvent me )
            {
            }
        } );

        return pvFigure;
    }

    /**
     * Enter the manual value
     */
    protected void enterManualValue ()
    {
        final VariantEntryDialog dlg = new VariantEntryDialog ( this.shell );
        this.manualValue = dlg.getValue ();
    }

    protected void switchToProcess ()
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();

        attributes.put ( "org.openscada.da.manual.value", new Variant () );
        this.item.writeAtrtibutes ( attributes );
    }

    @Override
    public void dispose ()
    {
        this.canvas.dispose ();
        super.dispose ();
    }

    protected void updateRemote ()
    {
        final Boolean remoteManual = this.value.isAttribute ( "remote.manual.active" );
        final Variant remoteProcessValue = this.value.getAttributes ().get ( "remote.manual.value.original" );
        final Variant remoteManualValue = this.value.getAttributes ().get ( "remote.manual.value" );

        if ( remoteManual == null )
        {
            setConnectionState ( this.rp2pConnection, false );
            setConnectionState ( this.rm2pConnection, false );
        }
        else if ( remoteManual )
        {
            setConnectionState ( this.rp2pConnection, false );
            setConnectionState ( this.rm2pConnection, true );
        }
        else
        {
            setConnectionState ( this.rp2pConnection, true );
            setConnectionState ( this.rm2pConnection, false );
        }

        if ( remoteManualValue != null )
        {
            this.rmvValue.setText ( remoteManualValue.toString () );
        }
        if ( remoteProcessValue != null )
        {
            this.rpvValue.setText ( remoteProcessValue.toString () );
        }
    }

    private boolean isLocalManual ()
    {
        return this.value.isAttribute ( "org.openscada.da.manual.active", false );
    }

    private boolean isRemoteManual ()
    {
        return this.value.isAttribute ( "remote.manual.active", false );
    }

    private boolean isGlobalManual ()
    {
        return isLocalManual () || isRemoteManual ();
    }

    private void updateLocalManualValue ()
    {
        if ( this.manualValue == null )
        {
            this.manualValue = this.value.getAttributes ().get ( "org.openscada.da.manual.value" );
        }
    }

    @Override
    protected void update ()
    {
        if ( this.value == null )
        {
            return;
        }

        updateRemote ();
        updateLocalManualValue ();

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
        else if ( isGlobalManual () )
        {
            this.rvRect.setBackgroundColor ( ColorConstants.cyan );
        }
        else
        {
            this.rvRect.setBackgroundColor ( ColorConstants.lightGray );
        }

        if ( isRemoteManual () )
        {
            this.pvRect.setBackgroundColor ( ColorConstants.cyan );
        }
        else
        {
            this.pvRect.setBackgroundColor ( ColorConstants.lightGray );
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

        if ( isLocalManual () )
        {
            setConnectionState ( this.p2rConnection, false );
            setConnectionState ( this.m2rConnection, true );

            // set process value
            if ( processValue != null )
            {
                this.pvValue.setText ( processValue.toString () );
            }
            else
            {
                this.pvValue.setText ( "<none>" );
            }

            if ( processError.asBoolean () )
            {
                this.pvRect.setBackgroundColor ( ColorConstants.red );
            }
        }
        else
        {
            setConnectionState ( this.p2rConnection, true );
            setConnectionState ( this.m2rConnection, false );

            this.pvValue.setText ( this.value.getValue ().toString () );
        }
    }

    /**
     * Set graphics attribute according to the connection state
     * @param connection the connection to change 
     * @param state the state
     */
    protected void setConnectionState ( final PolylineConnection connection, final boolean state )
    {
        final PolygonDecoration dec = new PolygonDecoration ();
        dec.setTemplate ( PolygonDecoration.TRIANGLE_TIP );

        connection.setLineStyle ( state ? Graphics.LINE_SOLID : Graphics.LINE_DOT );
        connection.setLineWidth ( state ? 2 : 1 );
        connection.setTargetDecoration ( state ? dec : null );
    }

}
