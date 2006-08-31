package org.openscada.ae.net;

public interface Messages
{
    public final static int CC_CREATE_SESSION =                     0x00020000;
    public final static int CC_CLOSE_SESSION =                      0x00020001;
    public final static int CC_CANCEL_OPERATION =                   0x00020002;
    
    public final static int CC_LIST =                               0x00020010;
    public final static int CC_LIST_REPLY =                         0x00020011;
    
    public final static int CC_READ =                               0x00020100;
    public final static int CC_READ_REPLY =                         0x00020101;
    
    public final static int CC_SUBSCRIBE =                          0x00020200;
    public final static int CC_UNSUBSCRIBE =                        0x00020201;
    public final static int CC_SUBSCRIPTION_EVENT =                 0x00020202;
    
    public final static int CC_MODIFY_EVENT =                       0x00020300;
}
