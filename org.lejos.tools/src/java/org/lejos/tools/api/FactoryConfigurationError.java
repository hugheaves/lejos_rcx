package org.lejos.tools.api;

/**
 * Thrown when a problem with configuration of the parser factories exists.
 * 
 * <p>
 * This error will typically be thrown when the class of a parser factory
 * specified in the system properties cannot be found or instantiated.
 * </p>
 * 
 * @see ToolsetFactory
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class FactoryConfigurationError extends Error {

	// attributes

	/** The error message. */
	private String message;

	/** The originating exception. */
	private Exception originatingException;

	// constructors

	/**
	 * Creates a new error with a given message.
	 * 
	 * @param msg the message
	 */
	public FactoryConfigurationError(String msg) {
		this.message = msg;
		this.originatingException = null;
	}

	/**
	 * Creates a new error for an originating exception.
	 * 
	 * @param msg the message
	 * @param exception the originating exception
	 */
	public FactoryConfigurationError(String msg, Exception exception) {
		this.message = msg;
		this.originatingException = exception;
	}

	// public methods

	/**
	 * Get the message of this error.
	 * 
	 * <p>
	 * If message has been set, it will be returned.
	 * </p>
	 * <p>
	 * If the originatingException has NOT been set, the name of this class
	 * will be returned.
	 * </p>
	 * <p>
	 * If the originatingException has been set, the representation of this
	 * encapsulated exception will be returned, using the <code>getMessage()</code>
	 * method.
	 * </p>
	 * <p>
	 * If this message is also not set, the name of the originating exception
	 * will be returned.
	 * </p>
	 * 
	 * @return the error message as described above. It is always NOT null
	 */
	public String getMessage() {
		if (this.message != null) {
			return this.message;
		}
		if (this.originatingException == null) {
			return FactoryConfigurationError.class.getName();
		}
		if (this.originatingException.getMessage() != null) {
			return this.originatingException.getMessage();
		} else {
			return this.originatingException.getClass().getName();
		}
	}

	/**
	 * Get the originating exception of this error.
	 * 
	 * <p>
	 * Can be null, if message has been used.
	 * </p>
	 * 
	 * @return the originating exception or null
	 */
	public Exception getException() {
		return this.originatingException;
	}
}
