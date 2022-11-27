package com.project.task.bot.command;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface Command {

    /**
     * @param update provided {@link Update} object with all the needed data for command.
     */
    void execute(Update update);
}
