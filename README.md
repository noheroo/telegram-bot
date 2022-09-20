# telegram-bot
This telegram-bot do next things: 
* if received message "/start", and will send welcoming message
* if received message in format DD.MM.YYYY HH:MM Text notification, Bot will create and add entity NotificationTask in Data Base. 
Bot monitoring Data Base every minute, if it searched needed sending date and time, it will send notification from data base.
* if received other messages, bot will send message "You sent message in wrong format"
