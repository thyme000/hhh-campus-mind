package cn.hhh.campusmind.common.exceptions;

public class NoAuthException extends RuntimeException {
    public NoAuthException(String message) {
        super(message);
    }
}
