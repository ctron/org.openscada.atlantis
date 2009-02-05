package org.openscada.da.client.viewer.model.impl.figures;

import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.openscada.da.client.viewer.model.impl.AnySetterOutput;
import org.openscada.da.client.viewer.model.impl.AnyValue;
import org.openscada.da.client.viewer.model.impl.PropertyInput;

public class CommandButton extends Button
{
    private AnyValue _value = null;
    private AnySetterOutput _output = null;

    public CommandButton ( String id )
    {
        super ( id );
        addInput ( new PropertyInput ( this, "value" ) );
        addOutput ( _output = new AnySetterOutput ( "value" ) );
    }

    @Override
    protected void setupButton ( org.eclipse.draw2d.Button button )
    {
       button.addActionListener ( new ActionListener () {

        public void actionPerformed ( ActionEvent event )
        {
            writeCommand ();
        }} );
    }
    
    protected void writeCommand ()
    {
        _output.setValue ( _value );
    }

    public AnyValue getValue ()
    {
        return _value;
    }

    public void setValue ( AnyValue value )
    {
        _value = value;
    }

}
