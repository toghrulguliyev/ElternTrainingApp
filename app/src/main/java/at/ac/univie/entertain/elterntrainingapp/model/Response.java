package at.ac.univie.entertain.elterntrainingapp.model;

public class Response {

    private String message;
    private int httpStatus;

    public Response(String message, int httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }


    public int getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }



}
