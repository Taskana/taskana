package pro.taskana.common.api.exceptions;

public class WrongCustomHolidayFormatException extends Exception {

  private static final long serialVersionUID = -7644923780787018797L;

  public WrongCustomHolidayFormatException(String message) {
    super(message);
  }
}
