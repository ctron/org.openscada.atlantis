package org.openscada.hd.exporter.http.server.internal;

import java.util.ArrayList;
import org.openscada.utils.concurrent.AbstractFuture;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.openscada.core.InvalidSessionException;
import org.openscada.hd.InvalidItemException;
import org.openscada.hd.Query;
import org.openscada.hd.QueryListener;
import org.openscada.hd.QueryParameters;
import org.openscada.hd.QueryState;
import org.openscada.hd.Value;
import org.openscada.hd.ValueInformation;
import org.openscada.hd.exporter.http.DataPoint;
import org.openscada.hd.exporter.http.HttpExporter;
import org.openscada.hd.server.Service;
import org.openscada.hd.server.Session;

public class LocalHttpExporter implements HttpExporter
{
	private class QueryFuture extends AbstractFuture<List<DataPoint>> implements QueryListener {
		final List<DataPoint> result = new ArrayList<DataPoint>();
		
		final String type;
		
		public QueryFuture(String type) {
			this.type = type;
		}

		public void updateData(int index, Map<String, Value[]> values,
				ValueInformation[] valueInformation) {
			int i = 0;
			for (ValueInformation vi : valueInformation) {
				DataPoint dp = new DataPoint();
				dp.setQuality(vi.getQuality());
				dp.setManual(vi.getManualPercentage());
				dp.setTimestamp(vi.getStartTimestamp().getTime());
				dp.setValue(values.get(type)[i].toDouble());
				result.add(dp);
				i++;
			}
		}

		public void updateParameters(QueryParameters parameters,
				Set<String> valueTypes) {
		}

		public void updateState(QueryState state) {
			if (state == QueryState.COMPLETE) {
				setResult(result);
			}
		}		
	}
	
    private final Service hdService;

    private final Session session;

	public LocalHttpExporter(Service hdService) throws Exception {
		this.hdService = hdService;
		this.session = (Session) this.hdService.createSession(new Properties());
	}

	public List<DataPoint> getData ( final String item, final String type, final Date from, final Date to, final Integer number )
    {
		final Calendar calFrom = new GregorianCalendar();
		calFrom.setTime(from);
		final Calendar calTo =  new GregorianCalendar();
		calTo.setTime(to);
		
		QueryParameters parameters = new QueryParameters(calFrom,calTo, number);
		QueryFuture queryFuture = new QueryFuture(type);
		try {
			Query q = this.hdService.createQuery(this.session, item, parameters , queryFuture, false);
			final List<DataPoint> result = queryFuture.get(30, TimeUnit.SECONDS);
			return result;
		} catch (InvalidSessionException e) {
			e.printStackTrace();
		} catch (InvalidItemException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		return null;
    }

    public List<String> getItems ()
    {
        return new ArrayList<String> ();
    }

    public List<String> getSeries ( final String itemId )
    {
        return new ArrayList<String> ();
    }
}
