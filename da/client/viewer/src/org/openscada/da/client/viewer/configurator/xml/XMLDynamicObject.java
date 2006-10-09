package org.openscada.da.client.viewer.configurator.xml;

import java.util.List;

import org.openscada.da.client.viewer.model.impl.BaseDynamicObject;
import org.openscada.da.client.viewer.model.impl.View;

public class XMLDynamicObject extends BaseDynamicObject
{
    private View _view = null;
    
    public XMLDynamicObject ( View view )
    {
        super ();
        _view = view;
    }

    public View getView ()
    {
        return _view;
    }
    
    public void setInputExports ( List<XMLInputExport> inputs )
    {
        for ( XMLInputExport input : inputs )
        {
            addInput ( input );
        }
    }
    
    public void setOutputExports ( List<XMLOutputExport> outputs )
    {
        for ( XMLOutputExport output : outputs )
        {
            addOutput ( output );
        }
    }
    
    
}
