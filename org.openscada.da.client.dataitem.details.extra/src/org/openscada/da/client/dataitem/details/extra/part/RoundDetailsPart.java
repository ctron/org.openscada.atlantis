package org.openscada.da.client.dataitem.details.extra.part;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.Button;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.MidpointLocator;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RoundedRectangle;
import org.openscada.core.Variant;

public class RoundDetailsPart extends AbstractBaseDraw2DDetailsPart
{

    private RoundedRectangle sourceRect;

    private Label sourceLabel;

    private RoundedRectangle targetRect;

    private Label targetLabel;

    private Label typeLabel;

    private PolylineConnection roundConnection;

    @Override
    protected IFigure createRoot ()
    {
        final Figure rootFigure = new Figure ();

        rootFigure.setLayoutManager ( new GridLayout ( 3, true ) );

        rootFigure.add ( createSourceValue (), new GridData ( GridData.CENTER, GridData.CENTER, true, true ) );
        rootFigure.add ( new Figure () );
        rootFigure.add ( createTargetValue (), new GridData ( GridData.CENTER, GridData.CENTER, true, true ) );

        rootFigure.add ( new Figure () );
        rootFigure.add ( createCommandPanel (), new GridData ( GridData.CENTER, GridData.CENTER, true, true ) );

        createRoundArrow ( rootFigure );

        return rootFigure;
    }

    private IFigure createCommandPanel ()
    {
        final Figure commandPanel = new Figure ();

        commandPanel.setLayoutManager ( new FlowLayout () );

        Button noneButton;
        commandPanel.add ( noneButton = new Button ( "NONE" ) );
        Button roundButton;
        commandPanel.add ( roundButton = new Button ( "ROUND" ) );
        Button ceilButton;
        commandPanel.add ( ceilButton = new Button ( "CEIL" ) );
        Button floorButton;
        commandPanel.add ( floorButton = new Button ( "FLOOR" ) );

        noneButton.addActionListener ( new ActionListener () {

            public void actionPerformed ( final ActionEvent event )
            {
                RoundDetailsPart.this.setRoundType ( "NONE" );
            }
        } );

        roundButton.addActionListener ( new ActionListener () {

            public void actionPerformed ( final ActionEvent event )
            {
                RoundDetailsPart.this.setRoundType ( "ROUND" );
            }
        } );

        ceilButton.addActionListener ( new ActionListener () {

            public void actionPerformed ( final ActionEvent event )
            {
                RoundDetailsPart.this.setRoundType ( "CEIL" );
            }
        } );

        floorButton.addActionListener ( new ActionListener () {

            public void actionPerformed ( final ActionEvent event )
            {
                RoundDetailsPart.this.setRoundType ( "FLOOR" );
            }
        } );

        return commandPanel;
    }

    protected void setRoundType ( final String string )
    {
        final Map<String, Variant> attributes = new HashMap<String, Variant> ();
        attributes.put ( "org.openscada.da.round.type", new Variant ( string ) );
        this.item.writeAtrtibutes ( attributes );
    }

    private void createRoundArrow ( final Figure figure )
    {
        final PolylineConnection c = new PolylineConnection ();
        c.setSourceAnchor ( new ChopboxAnchor ( this.sourceRect ) );
        c.setTargetAnchor ( new ChopboxAnchor ( this.targetRect ) );

        final PolygonDecoration dec = new PolygonDecoration ();
        dec.setTemplate ( PolygonDecoration.TRIANGLE_TIP );
        c.setTargetDecoration ( dec );

        final MidpointLocator typeLocator = new MidpointLocator ( c, 0 );
        typeLocator.setRelativePosition ( PositionConstants.NORTH );
        this.typeLabel = new Label ( "" );
        c.add ( this.typeLabel, typeLocator );

        figure.add ( c );
        this.roundConnection = c;
    }

    private IFigure createTargetValue ()
    {
        this.targetRect = new RoundedRectangle ();
        this.targetRect.setLayoutManager ( new BorderLayout () );
        this.targetRect.setBackgroundColor ( ColorConstants.lightGray );
        this.targetRect.setForegroundColor ( ColorConstants.black );
        this.targetRect.setBorder ( new MarginBorder ( 10 ) );

        this.targetRect.add ( this.targetLabel = new Label (), BorderLayout.CENTER );

        return this.targetRect;
    }

    private IFigure createSourceValue ()
    {
        this.sourceRect = new RoundedRectangle ();
        this.sourceRect.setLayoutManager ( new BorderLayout () );
        this.sourceRect.setBackgroundColor ( ColorConstants.lightGray );
        this.sourceRect.setForegroundColor ( ColorConstants.black );
        this.sourceRect.setBorder ( new MarginBorder ( 10 ) );

        this.sourceRect.add ( this.sourceLabel = new Label (), BorderLayout.CENTER );

        return this.sourceRect;
    }

    @Override
    protected void update ()
    {
        if ( this.value == null )
        {
            return;
        }

        this.targetLabel.setText ( String.format ( "%s", this.value.getValue () ) );
        final Variant originalValue = this.value.getAttributes ().get ( "org.openscada.da.round.value.original" );
        final boolean active = getBooleanAttribute ( "org.openscada.da.round.active" );
        final Variant type = this.value.getAttributes ().get ( "org.openscada.da.round.type" );

        if ( type != null )
        {
            this.typeLabel.setText ( type.toLabel () );
        }
        else
        {
            this.typeLabel.setText ( "" );
        }

        if ( originalValue == null )
        {
            this.sourceLabel.setText ( "" );
        }
        else
        {
            this.sourceLabel.setText ( String.format ( "%s", originalValue ) );
        }

        if ( active )
        {
            this.roundConnection.setForegroundColor ( ColorConstants.green );
        }
        else
        {
            this.roundConnection.setForegroundColor ( ColorConstants.darkGray );
        }
        this.typeLabel.setForegroundColor ( ColorConstants.black );
    }
}
