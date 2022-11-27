package com.project.task.bot.command;

public enum CommandName {

    START("/start"),
    HELP("/help"),
    NO("/"),
    STOP("/stop");


    private final String commandName;

    CommandName(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }
}
