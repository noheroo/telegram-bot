package pro.sky.telegrambot.component;

import org.springframework.stereotype.Component;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.record.NotificationTaskRecord;

@Component
public class RecordMapper {

    public NotificationTaskRecord toRecord(NotificationTask notificationTask) {
        NotificationTaskRecord notificationTaskRecord = new NotificationTaskRecord();
        notificationTaskRecord.setChatId(notificationTask.getChatId());
        notificationTaskRecord.setTextNotification(notificationTask.getTextNotification());
        notificationTaskRecord.setSendingDateTime(notificationTask.getSendingDateTime());
        return notificationTaskRecord;
    }

    public NotificationTask toEntity(NotificationTaskRecord notificationTaskRecord) {
        NotificationTask notificationTask = new NotificationTask();
        notificationTask.setChatId(notificationTaskRecord.getChatId());
        notificationTask.setTextNotification(notificationTaskRecord.getTextNotification());
        notificationTask.setSendingDateTime(notificationTaskRecord.getSendingDateTime());
        return notificationTask;
    }
}
