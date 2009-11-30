package org.openscada.ae.server.storage.jdbc.internal;

public class NotSupportedException extends Exception {

	private static final long serialVersionUID = -530194790629785166L;

	public NotSupportedException() {
		super();
	}

	public NotSupportedException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotSupportedException(String message) {
		super(message);
	}

	public NotSupportedException(Throwable cause) {
		super(cause);
	}
}
