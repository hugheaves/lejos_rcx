package org.lejos.tools.api;

/**
 * The <code>ToolsetException</code> will be raised by the toolset in any
 * error case.
 * 
 * <p>
 * A <code>ToolsetException</code> will have a message indicating the
 * problem, and optionally, an originating exception.
 * </p>
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class ToolsetException extends Exception {

	// attributes

	/** the message */
	private String message;

	/** the originating exception. Is optional, could also be null. */
	private Exception originatingException = null;

	// constructors

	/**
	 * Creates a new <code>ToolsetException</code> based on the given
	 * message.
	 * 
	 * @param aMessage a given error message. Must not be null. If null has
	 *        been specified, an <code>IllegalArgumentException</code> will
	 *        be thrown.
	 */
	public ToolsetException(String aMessage) {
		if (aMessage == null) {
			throw new IllegalArgumentException("Message must not be null");
		}
		this.message = aMessage;
	}

	/**
	 * Creates a new <code>ToolsetException</code> based on the given message
	 * and an orginating exception.
	 * 
	 * @param aMessage a given error message. Must not be null. If null has
	 *        been specified, an <code>IllegalArgumentException</code> will
	 *        be thrown.
	 * @param anException a given originating exception. Must not be null. If
	 *        null has been specified, an <code>IllegalArgumentException</code>
	 *        will be thrown.
	 */
	public ToolsetException(String aMessage, Exception anException) {
		if (aMessage == null) {
			throw new IllegalArgumentException("Message must not be null");
		}
		this.message = aMessage;
		if (anException == null) {
			throw new IllegalArgumentException("Exception must not be null");
		}
		this.originatingException = anException;
	}

	// public methods

	/**
	 * Get the error message.
	 * 
	 * @return the error message, which is always not null.
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * Get the originating exception.
	 * 
	 * @return the originating exception or null.
	 */
	public Exception getOriginationException() {
		return this.originatingException;
	}
}
