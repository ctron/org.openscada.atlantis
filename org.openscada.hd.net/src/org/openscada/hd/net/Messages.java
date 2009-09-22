package org.openscada.hd.net;

public class Messages
{
    /**
     * Base command code for all HD messages
     */
    public static final int CC_HD_BASE = 0x00040000;

    /* query interface */

    public static final int CC_HD_CREATE_QUERY = CC_HD_BASE + 0x0001;

    public static final int CC_HD_CLOSE_QUERY = CC_HD_BASE + 0x0002;

    public static final int CC_HD_CHANGE_QUERY_PARAMETERS = CC_HD_BASE + 0x0003;

    public static final int CC_HD_UPDATE_QUERY_STATUS = CC_HD_BASE + 0x0004;

    public static final int CC_HD_UPDATE_QUERY_DATA = CC_HD_BASE + 0x0005;

    public static final int CC_HD_UPDATE_QUERY_PARAMETERS = CC_HD_BASE + 0x0006;

    /* List interface */

    public static final int CC_HD_START_LIST = CC_HD_BASE + 0x0011;

    public static final int CC_HD_STOP_LIST = CC_HD_BASE + 0x0012;

    public static final int CC_HD_LIST_UPDATE = CC_HD_BASE + 0x0013;

}
