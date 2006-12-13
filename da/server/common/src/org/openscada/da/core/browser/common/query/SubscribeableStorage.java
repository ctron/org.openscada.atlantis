package org.openscada.da.core.browser.common.query;

public interface SubscribeableStorage extends ItemStorage
{
    public void addChild ( ItemStorage child );
    public void removeChild ( ItemStorage child );
    public void clear ();
}
