package org.lejos.tools.api;

/**
 * The <code>ToolsetException</code> will be raised by the toolset in any
 * error case.
 * 
 * <p>
 * A <code>ToolsetException</code> will have a message indicating the problem,
 * and optionally, an originating exception.
 * </p>
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller </a>
 */
public class ToolsetException extends Exception
{
  /**
   * @param message
   */
  public ToolsetException(String message)
  {
    super(message);
  }

  /**
   * @param cause
   */
  public ToolsetException(Throwable cause)
  {
    super(cause);
  }

  /**
   * @param message
   * @param cause
   */
  public ToolsetException(String message, Throwable cause)
  {
    super(message, cause);
  }
}