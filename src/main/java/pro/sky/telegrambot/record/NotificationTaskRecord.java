package pro.sky.telegrambot.record;

import java.time.LocalDateTime;
import java.util.Objects;

public class NotificationTaskRecord {
    private Long chatId;

    private String textNotification;
    private LocalDateTime sendingDateTime;

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getTextNotification() {
        return textNotification;
    }

    public void setTextNotification(String textNotification) {
        this.textNotification = textNotification;
    }

    public LocalDateTime getSendingDateTime() {
        return sendingDateTime;
    }

    public void setSendingDateTime(LocalDateTime sendingDateTime) {
        this.sendingDateTime = sendingDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationTaskRecord that = (NotificationTaskRecord) o;
        return Objects.equals(textNotification, that.textNotification) && Objects.equals(sendingDateTime, that.sendingDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(textNotification, sendingDateTime);
    }
}
