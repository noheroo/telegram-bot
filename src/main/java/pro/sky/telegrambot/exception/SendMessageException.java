package pro.sky.telegrambot.exception;

public class SendMessageException extends RuntimeException{
    public SendMessageException(String message) {
        super(message);
    }
}
