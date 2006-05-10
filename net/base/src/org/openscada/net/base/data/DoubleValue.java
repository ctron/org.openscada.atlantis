package org.openscada.net.base.data;


public class DoubleValue extends Value {

	private double _value;

	public DoubleValue(double value) {
		super();
		_value = value;
	}

	public double getValue() {
		return _value;
	}

	public void setValue(double value) {
		_value = value;
	}
	
	@Override
	public String toString() {
		return String.valueOf(_value);
	}
	
}
