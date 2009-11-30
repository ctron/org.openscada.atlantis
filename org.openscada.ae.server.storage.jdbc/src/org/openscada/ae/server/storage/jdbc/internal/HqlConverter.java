package org.openscada.ae.server.storage.jdbc.internal;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.openscada.core.NotConvertableException;
import org.openscada.core.NullValueException;
import org.openscada.core.Variant;
import org.openscada.utils.filter.Assertion;
import org.openscada.utils.filter.Filter;
import org.openscada.utils.filter.FilterAssertion;
import org.openscada.utils.filter.FilterExpression;
import org.openscada.utils.filter.Operator;

public class HqlConverter {

	public static class HqlResult {

		private String hql = "";

		private Object[] parameters = new Object[] {};

		public String getHql() {
			return hql;
		}

		public void setHql(String hql) {
			this.hql = hql;
		}

		public Object[] getParameters() {
			return parameters;
		}

		public void setParameters(Object[] parameters) {
			this.parameters = parameters;
		}
	}

	public static HqlResult toHql(Filter filter) throws NotSupportedException {
		HqlResult result = new HqlResult();
		result.hql = "SELECT M from MutableEvent M left join fetch M.attributes as A";
		if (filter.isEmpty()) {
			// pass
		} else if (filter.isExpression()) {
			HqlResult h = toHql((FilterExpression) filter);
			result.hql += " WHERE " + h.hql;
			result.parameters = combine(result.parameters, h.parameters);
		} else if (filter.isAssertion()) {
			HqlResult h = toHql((FilterAssertion) filter);
			result.hql += " WHERE " + h.hql;
			result.parameters = combine(result.parameters, h.parameters);
		} else {
			//
		}
		return result;
	}

	static HqlResult toHql(FilterExpression expression) throws NotSupportedException {
		HqlResult result = new HqlResult();
		result.hql = "(";
		int i = 0;
		for (Filter term : expression.getFilterSet()) {
			if (i > 0) {
				if (expression.getOperator() == Operator.AND) {
					result.hql += " AND ";
				} else if (expression.getOperator() == Operator.OR) {
					result.hql += " OR ";
				}
			}
			if (term.isExpression()) {
				HqlResult r = toHql((FilterExpression) term);
				result.hql += r.hql;
				result.parameters = combine(result.parameters, r.parameters);
			} else if (term.isAssertion()) {
				HqlResult r = toHql((FilterAssertion) term);
				result.hql += r.hql;
				result.parameters = combine(result.parameters, r.parameters);
			}
			i++;
		}
		if (expression.getOperator() == Operator.NOT) {
			result.hql = "NOT " + result.hql;
		}
		result.hql += ")";
		return result;
	}

	static HqlResult toHql(FilterAssertion assertion) throws NotSupportedException {
		HqlResult result = null;
		if (assertion.getAssertion() == Assertion.EQUALITY) {
			result = toHql(assertion.getAttribute(), "=", assertion.getValue());
		} else if (assertion.getAssertion() == Assertion.GREATEREQ) {
			result = toHql(assertion.getAttribute(), ">=", assertion.getValue());
		} else if (assertion.getAssertion() == Assertion.LESSEQ) {
			result = toHql(assertion.getAttribute(), "<=", assertion.getValue());
		} else if (assertion.getAssertion() == Assertion.APPROXIMATE) {
			result = toHql(assertion.getAttribute(), "like", assertion.getValue());
		} else {
			throw new NotSupportedException();
		}
		return result;
	}

	static HqlResult toHql(String field, String op, Object value) {
		HqlResult term = new HqlResult();
		term.hql = "(";
		if (isField(field)) {
			term.hql += "M." + field;
			for (PropertyDescriptor pd : PropertyUtils
					.getPropertyDescriptors(MutableEvent.class)) {
				if (pd.getName().equals(field)) {
					term.parameters = new Object[] { value };
				}
			}
			term.hql += " " + op + " ?)";
		} else {
			term.hql += "index(A) = '" + field
					+ "' AND (A.string = ? OR A.integer = ? OR A.double = ?))";
			Variant v = (Variant) value;
			String strValue = v.asString("");
			Long longValue = null;
			Double doubleValue = null;
			try {
				longValue = v.asLong();
				doubleValue = v.asDouble();
			} catch (NullValueException e) {
				longValue = null;
				doubleValue = null;
			} catch (NotConvertableException e) {
				longValue = null;
				doubleValue = null;
			}
			term.parameters = new Object[] { strValue, longValue, doubleValue };
		}
		return term;
	}

	static boolean isField(String field) {
		for (PropertyDescriptor pd : PropertyUtils
				.getPropertyDescriptors(MutableEvent.class)) {
			if (pd.getName().equals(field)) {
				return true;
			}
		}
		return false;
	}

	static Object[] combine(Object[] a, Object[] b) {
		List<Object> l = new ArrayList<Object>();
		if (a != null) {
			l.addAll(Arrays.asList(a));
		}
		if (b != null) {
			l.addAll(Arrays.asList(b));
		}
		return l.toArray();
	}
}
