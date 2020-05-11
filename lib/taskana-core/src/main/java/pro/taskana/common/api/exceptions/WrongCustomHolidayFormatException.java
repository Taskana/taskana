package pro.taskana.common.api.exceptions;

public class WrongCustomHolidayFormatException extends Exception {

  private static final long serialVersionUID = -7644923780787018797L;

  public WrongCustomHolidayFormatException() {
    super();
  }

  public WrongCustomHolidayFormatException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public WrongCustomHolidayFormatException(String message, Throwable cause) {
    super(message, cause);
  }

  public WrongCustomHolidayFormatException(String message) {
    super(message);
  }

  public WrongCustomHolidayFormatException(Throwable cause) {
    super(cause);
  }
}
