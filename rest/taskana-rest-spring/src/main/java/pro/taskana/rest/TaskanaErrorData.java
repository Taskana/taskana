package pro.taskana.rest;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;

/**
 * This class holds error data.
 *
 * @author bbr
 */
public class TaskanaErrorData {

    private Date timestamp;
    private int status;
    private String error;
    private String exception;
    private String message;
    private String path;

    TaskanaErrorData(HttpStatus stat, Exception ex, WebRequest req) {
        this.timestamp = new Date();
        this.status = stat.value();
        this.error = stat.name();
        this.exception = ex.getClass().getName();
        this.message = ex.getMessage();
        this.path = req.getDescription(false);
        if (this.path != null && this.path.startsWith("uri=")) {
            this.path = this.path.substring(4);
        }
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getException() {
        return exception;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "TaskanaErrorData [timestamp=" + timestamp + ", status=" + status + ", error=" + error + ", exception="
            + exception + ", message=" + message + ", path=" + path + "]";
    }

}
