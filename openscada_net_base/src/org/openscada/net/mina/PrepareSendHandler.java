package org.openscada.net.mina;

import org.openscada.net.base.data.Message;

public interface PrepareSendHandler
{
    public void prepareSend ( Message message );
}
