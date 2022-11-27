package com.project.task.bot.service;

public interface SendBotMessageService {

    /**
     * @param chatId provided chatId in which messages would be sent.
     * @param message provided message to be sent.
     */
    void sendMessage(String chatId, String message);
}
