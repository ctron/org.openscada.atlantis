package org.openscada.da.server.snmp.items;


import org.openscada.da.core.common.DataItemInputCommon;
import org.openscada.utils.timing.Scheduler;

public abstract class ScheduledDataItem extends DataItemInputCommon implements Runnable {

	private Scheduler _scheduler;
	
	public ScheduledDataItem(String name, Scheduler scheduler, int period) {
		super(name);
		_scheduler = scheduler;
		_scheduler.addJob(this,period);
	}
	
}
