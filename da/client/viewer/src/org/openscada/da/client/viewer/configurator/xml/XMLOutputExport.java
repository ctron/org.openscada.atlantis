package org.openscada.da.client.viewer.configurator.xml;

import java.util.EnumSet;

import org.openscada.da.client.viewer.model.OutputDefinition;
import org.openscada.da.client.viewer.model.OutputListener;
import org.openscada.da.client.viewer.model.Type;

public class XMLOutputExport implements OutputDefinition
{
    private OutputDefinition _output = null;
    private String _alias = null;
    
    public XMLOutputExport ( OutputDefinition output, String alias )
    {
        _output = output;
        _alias = alias;
    }

    public void addListener ( OutputListener listener )
    {
        _output.addListener ( listener );
    }

    public void removeListener ( OutputListener listener )
    {
        _output.removeListener ( listener );
    }

    public String getName ()
    {
        return _alias;
    }

    public EnumSet<Type> getSupportedTypes ()
    {
        return _output.getSupportedTypes ();
    }


}
