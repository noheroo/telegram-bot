package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.component.RecordMapper;
import pro.sky.telegrambot.exception.SendMessageException;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.record.NotificationTaskRecord;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);


    private final TelegramBot telegramBot;

    private final NotificationTaskRepository notificationTaskRepository;

    private final RecordMapper recordMapper;

    public TelegramBotUpdatesListener(TelegramBot telegramBot,
                                      NotificationTaskRepository notificationTaskRepository, RecordMapper recordMapper) {
        this.telegramBot = telegramBot;
        this.notificationTaskRepository = notificationTaskRepository;
        this.recordMapper = recordMapper;
    }

    private final Pattern pattern = Pattern.compile("([\\d\\.\\:\\s]{16})(\\s)([\\W+]+)");
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);

            Long chatId = update.message().chat().id();
            String receivedMessageText = update.message().text();
            String firstName = update.message().chat().firstName();
            String sendingText = createMessageTextForStart(firstName);

            if (receivedMessageText.equals("/start")) {
                sendMessage(sendingText, chatId);
            }

            if (!receivedMessageText.startsWith("/")) {
                NotificationTaskRecord notificationTaskRecord = parseMessage(chatId, receivedMessageText);
                if (notificationTaskRecordIsChecked(notificationTaskRecord, chatId)) {
                    addNotificationTask(notificationTaskRecord);
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private boolean notificationTaskRecordIsChecked(NotificationTaskRecord notificationTaskRecord, Long chatId) {
        if (notificationTaskRecord.getTextNotification() == null || notificationTaskRecord.getSendingDateTime() == null) {
            String errorMessage = "You sent message in wrong format";
            sendMessage(errorMessage, chatId);
            return false;
        }
        return true;
    }

    //Create message for /start command
    private String createMessageTextForStart(String firstname) {
        return "Hello my friend " + firstname + ". Send me Notification task. Format example: DD.MM.YYYY HH:MM Text notification";
    }

    //parse message by groups and create notificationTaskRecord
    private NotificationTaskRecord parseMessage(Long chatId, String receivedMessageText) {
        NotificationTaskRecord notificationTaskRecord = new NotificationTaskRecord();

        Matcher matcher = pattern.matcher(receivedMessageText);
        if (matcher.find()) {
            notificationTaskRecord.setSendingDateTime(LocalDateTime.parse(matcher.group(1), formatter));
            notificationTaskRecord.setTextNotification(matcher.group(3));
            notificationTaskRecord.setChatId(chatId);
            logger.info("Notification task record created");
        }
        return notificationTaskRecord;
    }

    //send message with needed text and in needed chat
    private void sendMessage(String sendingText, Long chatId) {
        SendMessage sendMessage = new SendMessage(chatId, sendingText);
        SendResponse response = telegramBot.execute(sendMessage);
        if (!response.isOk()) {
            throw new SendMessageException("Send error " + response.errorCode());
        }
        logger.info("Message {" + sendingText + "} sent successfully to user id " + chatId);
    }
    /*This test method I wrote for test dateTimeFormatted
    private String createMessageTextForNotification(NotificationTask notificationTask) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm d MMMM yyyy");
        return "В " + dateTimeFormatter.format(notificationTask.getSendingDateTime()) + " " + notificationTask.getTextNotification();
    }
    */

    //Add notification task in DB
    private void addNotificationTask(NotificationTaskRecord notificationTaskRecord) {
        notificationTaskRepository.save(recordMapper.toEntity(notificationTaskRecord));
        logger.info("Notification task created and added successfully");
    }

    // searching and sending actual message every minute
    @Scheduled(cron = "0 * * * * *")
    public void searchAndSendActualNotification() {
        List<NotificationTask> actualTask = notificationTaskRepository.findNotificationTaskBySendingDateTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));

        if (!actualTask.isEmpty()) {
            for (NotificationTask notificationTask : actualTask) {
                sendMessage(notificationTask.getTextNotification(), notificationTask.getChatId());
                notificationTaskRepository.delete(notificationTask);
                logger.info("Notification task deleted successfully");
            }
        }
    }
}
