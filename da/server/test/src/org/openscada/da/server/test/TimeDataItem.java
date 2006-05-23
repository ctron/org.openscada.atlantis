package org.openscada.da.server.test;


import java.util.Map;

import org.openscada.da.core.InvalidOperationException;
import org.openscada.da.core.common.DataItemInput;
import org.openscada.da.core.common.DataItemInputCommon;
import org.openscada.da.core.data.Variant;
import org.openscada.utils.timing.Scheduler;

public class TimeDataItem extends ScheduledDataItem {

	public TimeDataItem(String name, Scheduler scheduler) {
		super(name, scheduler, 1000);
	}

	public void run() {
		updateValue(new Variant(System.currentTimeMillis()));
	}

}
