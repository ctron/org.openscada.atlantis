package org.openscada.ae.net;

public class Messages
{
    /**
     * Base command code for all A&E messages
     */
    public static final int CC_AE_BASE = 0x00030000;

    public static final int CC_SUBSCRIBE_EVENT_POOL = CC_AE_BASE + 0x0001;

    public static final int CC_UNSUBSCRIBE_EVENT_POOL = CC_AE_BASE + 0x0002;

    public static final int CC_EVENT_POOL_STATUS = CC_AE_BASE + 0x0003;

    public static final int CC_EVENT_POOL_DATA = CC_AE_BASE + 0x0004;

    public static final int CC_SUBSCRIBE_CONDITIONS = CC_AE_BASE + 0x0011;

    public static final int CC_UNSUBSCRIBE_CONDITIONS = CC_AE_BASE + 0x0012;

    public static final int CC_CONDITIONS_STATUS = CC_AE_BASE + 0x0013;

    public static final int CC_CONDITIONS_DATA = CC_AE_BASE + 0x0014;

    public static final int CC_CONDITION_AKN = CC_AE_BASE + 0x0015;

    public static final int CC_BROWSER_UPDATE = CC_AE_BASE + 0x0021;

}
