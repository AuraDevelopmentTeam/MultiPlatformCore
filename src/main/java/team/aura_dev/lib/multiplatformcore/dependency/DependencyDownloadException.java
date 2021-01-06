package team.aura_dev.lib.multiplatformcore.dependency;

import team.aura_dev.auraban.platform.common.AuraBanBase;

/**
 * An exception to indicate that downloading a dependency went wrong.<br>
 * Adds a small help message to the message to help admins resolve issues easier
 */
public class DependencyDownloadException extends IllegalArgumentException {
  private static final long serialVersionUID = 6515795989694196936L;

  /** Constructs an <code>DependencyDownloadException</code> with no detail message. */
  public DependencyDownloadException() {
    super();
  }

  /**
   * Constructs an <code>DependencyDownloadException</code> with the specified detail message.
   *
   * @param message the detail message.
   */
  public DependencyDownloadException(String message) {
    super(modifyMessage(message));
  }

  /**
   * Constructs a new exception with the specified detail message and cause.
   *
   * <p>Note that the detail message associated with <code>cause</code> is <i>not</i> automatically
   * incorporated in this exception's detail message.
   *
   * @param message the detail message (which is saved for later retrieval by the {@link
   *     Throwable#getMessage()} method).
   * @param cause the cause (which is saved for later retrieval by the {@link Throwable#getCause()}
   *     method). (A <tt>null</tt> value is permitted, and indicates that the cause is nonexistent
   *     or unknown.)
   */
  public DependencyDownloadException(String message, Throwable cause) {
    super(modifyMessage(message), cause);
  }

  /**
   * Constructs a new exception with the specified cause and a detail message of <tt>(cause==null ?
   * null : cause.toString())</tt> (which typically contains the class and detail message of
   * <tt>cause</tt>). This constructor is useful for exceptions that are little more than wrappers
   * for other throwables (for example, {@link java.security.PrivilegedActionException}).
   *
   * @param cause the cause (which is saved for later retrieval by the {@link Throwable#getCause()}
   *     method). (A <tt>null</tt> value is permitted, and indicates that the cause is nonexistent
   *     or unknown.)
   */
  public DependencyDownloadException(Throwable cause) {
    this((cause == null) ? null : cause.toString(), cause);
  }

  private static String modifyMessage(String message) {
    return message
        + "\nIf you see this error for the first time delete the \""
        + AuraBanBase.getInstance().getLibsDir().toAbsolutePath()
        + "\" folder and restart the server."
        + "\nIf this error persists feel free to report it to the plugin support.";
  }
}
