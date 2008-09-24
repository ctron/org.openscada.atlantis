package org.openscada.da.server.opc2.connection;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class OPCIoContext
{
    private Map<String, Boolean> activations;

    private List<ItemRequest> registrations;

    private List<OPCWriteRequest> writeRequests;

    private Set<String> readItems;

    public Map<String, Boolean> getActivations ()
    {
        return this.activations;
    }

    public void setActivations ( final Map<String, Boolean> activations )
    {
        this.activations = activations;
    }

    public List<ItemRequest> getRegistrations ()
    {
        return this.registrations;
    }

    public void setRegistrations ( final List<ItemRequest> registrations )
    {
        this.registrations = registrations;
    }

    public List<OPCWriteRequest> getWriteRequests ()
    {
        return this.writeRequests;
    }

    public void setWriteRequests ( final List<OPCWriteRequest> writeRequests )
    {
        this.writeRequests = writeRequests;
    }

    public Set<String> getReadItems ()
    {
        return this.readItems;
    }

    public void setReadItems ( final Set<String> readItems )
    {
        this.readItems = readItems;
    }

}
