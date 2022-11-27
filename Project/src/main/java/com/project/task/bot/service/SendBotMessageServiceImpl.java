package com.project.task.bot.service;

import com.project.task.Entities.Book;
import com.project.task.bot.BookManagerBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class SendBotMessageServiceImpl implements SendBotMessageService{
    private final BookManagerBot bookManagerBot;

    @Autowired
    public SendBotMessageServiceImpl(BookManagerBot bookManagerBot) {
        this.bookManagerBot = bookManagerBot;
    }

    @Override
    public void sendMessage(String chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.enableHtml(true);
        sendMessage.setText(message);

        try {
            bookManagerBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            //todo add logging to the project.
            e.printStackTrace();
        }
    }
}
