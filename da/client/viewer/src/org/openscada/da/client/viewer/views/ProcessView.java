package org.openscada.da.client.viewer.views;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.openscada.core.client.net.ConnectionInfo;
import org.openscada.da.client.net.Connection;
import org.openscada.da.client.viewer.Activator;
import org.openscada.da.client.viewer.configurator.Configurator;
import org.openscada.da.client.viewer.configurator.xml.XMLConfigurator;
import org.openscada.da.client.viewer.model.AlreadyConnectedException;
import org.openscada.da.client.viewer.model.Connector;
import org.openscada.da.client.viewer.model.Container;
import org.openscada.da.client.viewer.model.DynamicUIObject;
import org.openscada.da.client.viewer.model.OutputDefinition;
import org.openscada.da.client.viewer.model.View;
import org.openscada.da.client.viewer.model.impl.DisplaySynchronizedConnector;
import org.openscada.da.client.viewer.model.impl.IntegerSetterOutput;
import org.openscada.da.client.viewer.model.impl.PassThroughConnector;
import org.openscada.da.client.viewer.model.impl.containers.FigureContainer;
import org.openscada.da.client.viewer.model.impl.converter.ColorComposer;
import org.openscada.da.client.viewer.model.impl.converter.Double2IntegerConverter;
import org.openscada.da.client.viewer.model.impl.converter.FactorCalculator;
import org.openscada.da.client.viewer.model.impl.converter.Integer2DoubleConverter;
import org.openscada.da.client.viewer.model.impl.converter.ModuloCalculator;
import org.openscada.da.client.viewer.model.impl.converter.SimpleVariantIntegerConverter;
import org.openscada.da.client.viewer.model.impl.figures.Rectangle;
import org.openscada.da.client.viewer.model.impl.items.DataItemOutput;

public class ProcessView extends ViewPart
{
    private static Logger _log = Logger.getLogger ( ProcessView.class );
    
    private Canvas _canvas = null;
    private LightweightSystem _system = null;

    private IFigure _rootFigure = null;
    
    private Container _container = null;

    public ProcessView ()
    {
        try
        {
            test2 ();
        }
        catch ( Exception e )
        {
            _log.warn ( "failed to create test view", e );
        }
    }
    
    private void test2 ()
    {
        try
        {
            Configurator cfg = new XMLConfigurator ( Activator.getDefault ().getSampleView () );
            List<View> views = cfg.configure ();
            
            setView ( views.get ( 0 ) );
        }
        catch ( Exception e )
        {
            _log.warn ( "Failed to parse view", e );
        }
    }
    
    protected void setView ( Container container )
    {
        _container = container;   
    }
    
    @SuppressWarnings("unused")
    private void test () throws AlreadyConnectedException
    {
        Container container = new FigureContainer ( "1" );
        
        Rectangle rect = new Rectangle ( "2" );
        
        IntegerSetterOutput boundsOutput = new IntegerSetterOutput ( "bounds" );
        
        Connector connector3 = new PassThroughConnector ();
        connector3.setOutput ( boundsOutput );
        connector3.setInput ( rect.getInputByName ( "height" ) );
        
        ConnectionInfo ci = new ConnectionInfo ();
        ci.setAutoReconnect ( true );
        ci.setHostName ( "localhost" );
        ci.setPort ( 1202 );
        
        Connection c = new Connection ( ci );
        c.connect ();
        
        OutputDefinition diOutput = new DataItemOutput ( c, "time", "time" );
        OutputDefinition diOutput2 = new DataItemOutput ( c, "memory", "memory" );
        
        SimpleVariantIntegerConverter svic = new SimpleVariantIntegerConverter ( "3" );
        svic.setDefaultValue ( 0L );
        
        Connector connector7 = new DisplaySynchronizedConnector ();
        connector7.setOutput ( diOutput );
        connector7.setInput ( svic.getInputByName ( "value" ) );
        
        SimpleVariantIntegerConverter svic2 = new SimpleVariantIntegerConverter ( "4" );
        svic2.setDefaultValue ( 0L );
        
        Connector connector9 = new DisplaySynchronizedConnector ();
        connector9.setOutput ( diOutput2 );
        connector9.setInput ( svic2.getInputByName ( "value" ) );

        FactorCalculator fc = new FactorCalculator ( "5" );
        fc.setFactor ( 0.01 );
        
        ModuloCalculator mc = new ModuloCalculator ( "6" );
        mc.setModulo ( 255L );
        
        Integer2DoubleConverter i2dc = new Integer2DoubleConverter ( "7" );
        Connector connector12 = new PassThroughConnector ();
        connector12.setOutput ( svic.getOutputByName ( "value" ) );
        connector12.setInput ( i2dc.getInputByName ( "value" ) );
        
        Connector connector13 = new PassThroughConnector ();
        connector13.setInput ( fc.getInputByName ( "value" ) );
        connector13.setOutput ( i2dc.getOutputByName ( "value" ) );
        
        Double2IntegerConverter d2ic = new Double2IntegerConverter ( "8" );
        
        Connector connector11 = new PassThroughConnector ();
        connector11.setOutput ( fc.getOutputByName ( "value" ) );
        connector11.setInput ( d2ic.getInputByName ( "value" ) );
        
        Connector connector5 = new PassThroughConnector ();
        connector5.setInput ( mc.getInputByName ( "value" ) );
        connector5.setOutput ( d2ic.getOutputByName ( "value" ) );
        
        ModuloCalculator mc2 = new ModuloCalculator ( "9" );
        mc2.setModulo ( 255L );
        
        Connector connector8 = new PassThroughConnector ();
        connector8.setInput ( mc2.getInputByName ( "value" ) );
        connector8.setOutput ( svic2.getOutputByName ( "value" ) );
        
        ColorComposer cc = new ColorComposer ( "10" );
        
        Connector connector4r = new PassThroughConnector ();
        connector4r.setOutput ( mc.getOutputByName ( "value" ) );
        connector4r.setInput ( cc.getInputByName ( "red" ) );
        Connector connector4g = new PassThroughConnector ();
        connector4g.setOutput ( mc2.getOutputByName ( "value" ) );
        connector4g.setInput ( cc.getInputByName ( "green" ) );
        
        Connector connector10 = new PassThroughConnector ();
        connector10.setOutput ( mc.getOutputByName ( "value" ) );
        connector10.setInput ( rect.getInputByName ( "width" ) );
        
        Connector connector6 = new PassThroughConnector ();
        connector6.setInput ( rect.getInputByName ( "color" ) );
        connector6.setOutput ( cc.getOutputByName ( "color" ) );
     
        container.getObjects ().add ( rect );
        setView ( container );
        
        boundsOutput.setValue ( 100 );
    }
    
    @Override
    public void createPartControl ( Composite parent )
    {
        _canvas = new Canvas ( parent, SWT.NONE );
        _system = new LightweightSystem ( _canvas );

        _rootFigure = new Figure ();
        _rootFigure.setLayoutManager ( new XYLayout () );
        _rootFigure.setBackgroundColor ( ColorConstants.white );
        _rootFigure.setOpaque ( true );
        _system.setContents ( _rootFigure );
        
        createObjects ();
    }

    protected void createObjects ()
    {
        if ( _container == null )
            return;
        
        if ( _container instanceof DynamicUIObject )
        {
            _rootFigure.add ( ((DynamicUIObject)_container).getFigure () );
        }
    }

    @Override
    public void setFocus ()
    {
        _canvas.setFocus ();
    }
    
    @Override
    public void dispose ()
    {
        if ( _container != null )
        {
            _container.dispose ();
            _container = null;
        }
        if ( _canvas != null )
        {
            _canvas.dispose ();
            _canvas = null;
        }
        _system = null;
        super.dispose ();
    }

}
