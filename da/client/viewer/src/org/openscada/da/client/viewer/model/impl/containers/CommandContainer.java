package org.openscada.da.client.viewer.model.impl.containers;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.openscada.da.client.viewer.model.impl.BooleanSetterOutput;

public class CommandContainer extends FigureContainer
{
    private static Logger _log = Logger.getLogger ( CommandContainer.class );
    
    private IFigure _figure = null;
    private org.eclipse.draw2d.Clickable _clickable = null;
    
    private Map<String, CommandInformation> _commands = new HashMap<String, CommandInformation> ();
    
    public CommandContainer ( String id )
    {
        super ( id );
    }

    @Override
    protected void update ()
    {
        super.update ();
    }

    @Override
    public IFigure getFigure ()
    {
        if ( _figure == null )
        {
            _figure = super.getFigure ();
            
            _clickable = new org.eclipse.draw2d.Clickable ( _figure );
            _clickable.setCursor ( Cursors.CROSS );
            _clickable.setBounds ( new Rectangle ( _figure.getBounds () ) );
            _clickable.setRolloverEnabled ( true );
            _clickable.setSelected ( false );
            _clickable.getModel ().addActionListener ( new ActionListener () {

                public void actionPerformed ( ActionEvent event )
                {
                    clicked ( event );
                }} );
        }
        return _clickable;
    }

    protected void clicked ( ActionEvent event )
    {
        _log.debug ( "Clicked" );
        
        CommandMessageDialog cmd = new CommandMessageDialog ( null, _commands.values ().toArray ( new CommandInformation [0] ) );
        cmd.open ();
    }
    
    public void setCommands ( String commands )
    {
        clear ();
        
        for ( String command : commands.split ( "," ) )
        {
            String name = "";
            String label = "";
            
            Matcher m = Pattern.compile ( "(.*?)\\|(.*)" ).matcher ( command );
            if ( m.matches () )
            {
                name = m.group ( 1 );
                label = m.group ( 2 );
            }
            else
            {
                name = label = command;
            }
            
            addCommand ( name, label );
        }
    }
    
    public void clear ()
    {
        for ( String name : _commands.keySet () )
        {
            removeCommand ( name );
        }
    }
    
    protected void addCommand ( String name, String label )
    {
        _log.debug ( String.format ( "Adding command %s|%s", name, label ) );
        
        if ( _commands.containsKey ( name ) )
            removeCommand ( name );
        
        CommandInformation ci = new CommandInformation ();
        ci.setName ( name );
        ci.setLabel ( label );
        ci.setOutput ( new BooleanSetterOutput ( name ) );
        
        _commands.put ( name, ci );
        addOutput ( ci.getOutput () );
    }
    
    protected void removeCommand ( String name )
    {
        CommandInformation ci = _commands.remove ( name );
        if ( ci != null )
        {
            removeOutput ( ci.getName () );
        }
    }
}
