package org.openscada.da.core.browser.common.query;

public interface ItemStorage
{
    void added ( ItemDescriptor descriptor );
    void removed ( ItemDescriptor descriptor );
}
