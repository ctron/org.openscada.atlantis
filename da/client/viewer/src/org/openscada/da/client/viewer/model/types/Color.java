package org.openscada.da.client.viewer.model.types;

public class Color
{
    private int _red = 0;
    private int _green = 0;
    private int _blue = 0;
    
    public Color ()
    {
    }
    
    public Color ( int red, int green, int blue )
    {
        _red = red;
        _green = green;
        _blue = blue;
    }
    
    public int getBlue ()
    {
        return _blue;
    }
    public void setBlue ( int blue )
    {
        _blue = blue;
    }
    public int getGreen ()
    {
        return _green;
    }
    public void setGreen ( int green )
    {
        _green = green;
    }
    public int getRed ()
    {
        return _red;
    }
    public void setRed ( int red )
    {
        _red = red;
    }
}
