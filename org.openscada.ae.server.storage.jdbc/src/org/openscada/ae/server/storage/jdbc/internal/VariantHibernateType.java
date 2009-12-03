package org.openscada.ae.server.storage.jdbc.internal;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;
import org.openscada.core.NotConvertableException;
import org.openscada.core.NullValueException;
import org.openscada.core.Variant;
import org.openscada.core.VariantType;

public class VariantHibernateType implements CompositeUserType, Serializable {

    private static final long serialVersionUID = -1245809821282686169L;

    private static final int[] TYPES = { Types.VARCHAR, Types.VARCHAR,
			Types.BIGINT, Types.DOUBLE };

	private static final String[] PROPERTY_NAMES = { "type", "string",
			"integer", "double" };

	private static final Type[] PROPERTY_TYPES = { Hibernate.STRING,
			Hibernate.STRING, Hibernate.INTEGER, Hibernate.DOUBLE };

	public VariantHibernateType ()
    {
    }
	
	public Object assemble(Serializable cached, SessionImplementor session,
			Object owner) throws HibernateException {
		return cached;
	}

	public Object deepCopy(Object value) throws HibernateException {
		return new Variant((Variant) value);
	}

	public Serializable disassemble(Object value, SessionImplementor session)
			throws HibernateException {
		return (Serializable) value;
	}

	public boolean equals(Object x, Object y) throws HibernateException {
		if (x == y)
			return true;
		if (x == null || y == null)
			return false;
		return x.equals(y);
	}

	public String[] getPropertyNames() {
		return PROPERTY_NAMES;
	}

	public Type[] getPropertyTypes() {
		return PROPERTY_TYPES;
	}

	public Object getPropertyValue(Object component, int property)
			throws HibernateException {
		try {
			switch (property) {
			case 0:
				return ((Variant) component).getType();
			case 1:
				return ((Variant) component).asString();
			case 2:
				return ((Variant) component).asInteger();
			case 3:
				return ((Variant) component).asDouble();
			default:
				break;
			}
		} catch (NullValueException e) {
			return null;
		} catch (NotConvertableException e) {
			return null;
		}
		return null;
	}

	public int hashCode(Object x) throws HibernateException {
		if (x == null) {
			return 0;
		}
		return x.hashCode();
	}

	public boolean isMutable() {
		return false;
	}

	public Object nullSafeGet(ResultSet rs, String[] names,
			SessionImplementor session, Object owner)
			throws HibernateException, SQLException {
		String type = rs.getString(names[0]);
		String str = rs.getString(names[1]);
		Long i = rs.getLong(names[2]);
		Double d = rs.getDouble(names[3]);
		if (type == null) {
			return null;
		}
		if (VariantType.NULL.equals(type)) {
			return new Variant((Object) null);
		}
		if (VariantType.UNKNOWN.equals(type)) {
			return new Variant(new Object());
		}
		if (VariantType.BOOLEAN.equals(type)) {
			if (i == null) {
				return new Variant((Boolean) null);
			} else {
				return new Variant(!i.equals(0L));
			}
		}
		if (VariantType.INT32.equals(type)) {
			if (i == null) {
				return new Variant((Integer) null);
			} else {
				return new Variant(i.intValue());
			}
		}
		if (VariantType.INT64.equals(type)) {
			if (i == null) {
				return new Variant((Long) null);
			} else {
				return new Variant(i.longValue());
			}
		}
		if (VariantType.DOUBLE.equals(type)) {
			if (i == null) {
				return new Variant((Double) null);
			} else {
				return new Variant(d);
			}
		}
		return new Variant(str);
	}

	public void nullSafeSet(PreparedStatement st, Object value, int index,
			SessionImplementor session) throws HibernateException, SQLException {
		st.setNull(index + 0, Types.VARCHAR);
		st.setNull(index + 1, Types.VARCHAR);
		st.setNull(index + 2, Types.BIGINT);
		st.setNull(index + 3, Types.DOUBLE);
		Variant v = (Variant) value;
		if (v != null) {
			try {
				st.setString(index + 0, v.getType().toString());
				st.setString(index + 1, v.asString());
				st.setLong(index + 2, v.asLong());
				st.setDouble(index + 3, v.asDouble());
			} catch (NullValueException e) {
				// leave at null
			} catch (NotConvertableException e) {
				// leave at null
			}
		}
	}

	public Object replace(Object original, Object target,
			SessionImplementor session, Object owner) throws HibernateException {
		return original;
	}

	public Class returnedClass() {
		return Variant.class;
	}

	public void setPropertyValue(Object component, int property, Object value)
			throws HibernateException {
		throw new UnsupportedOperationException("Variant is immutable");
	}

}
