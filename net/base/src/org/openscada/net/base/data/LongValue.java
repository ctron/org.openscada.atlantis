package org.openscada.net.base.data;


public class LongValue extends Value {

	private long _value;

	public LongValue(long value) {
		super();
		_value = value;
	}

	public long getValue() {
		return _value;
	}

	public void setValue(long value) {
		_value = value;
	}
	
	@Override
	public String toString() {
		return String.valueOf(_value);
	}
}
