package com.project.task.bot;

import com.project.task.Entities.Author;
import com.project.task.Entities.Book;
import com.project.task.Entities.Character;
import com.project.task.controller.BookController;
import com.project.task.service.AuthorService;
import com.project.task.service.BookService;
import com.project.task.service.CharacterService;
import com.project.task.service.SeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;


@Component
@Configurable
@Transactional
public class BookManagerBot extends TelegramLongPollingBot {

    private BookService bookService;

    private SeriesService seriesService;

    private AuthorService authorService;

    private CharacterService characterService;

    public BookManagerBot(BookService theBookService, SeriesService seriesService, AuthorService authorService, CharacterService characterService) {
        bookService = theBookService;
        this.seriesService = seriesService;
        this.authorService = authorService;
        this.characterService = characterService;
    }

    BotState state = BotState.STEP_0;
    int characters = 1;
    List<String> namesList = new ArrayList<>();
    List<String> rolesList = new ArrayList<>();
    Book book = new Book();

    boolean deleteAction = false;
    boolean findAction = false;
    @Autowired
    BookController bookController = new BookController(bookService, seriesService, authorService, characterService);

    @Value("${bot.username}")
    private String username;

    @Value("${bot.token}")
    private String token;

    @Override
    public void onUpdateReceived(Update update){

        if (update.hasMessage() && update.getMessage().hasText()) {
            Message msg = update.getMessage();
            SendMessage sendMessage = new SendMessage();

            if (msg.getText().trim().equals("/help")){
                sendMessage.setChatId(msg.getChatId());
                sendMessage.setText("✨Існуючі команди✨\n\n"
                        + "/functions - виконати дії над базою книжок\n\n"
                        + "/help - отримати список команд\n\n");

                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
            if (msg.getText().trim().equals("/start")){
                sendMessage.setChatId(msg.getChatId());
                sendMessage.setText("Привіт! Я BookManagerBot. Я зможу допомогти тобі виконувати операції з базою книжок!\n\n" +
                        "Щоб отримати список доступних команд, введи /help");

                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
            if (msg.getText().trim().equals("Отримати список книжок")) {
                sendMessage.setChatId(msg.getChatId());

                List<Book> books = bookService.findAll();

                StringBuilder table = new StringBuilder();
                table.append(" # | Назва           | Автор\n");
                table.append("--------------------------------------\n");

                for (Book book : books) {
                    table.append(String.format("%2d | %-15s | %-15s\n",
                            book.getId(),
                            book.getName().length() > 15 ? book.getName().substring(0, 12) + "..." : book.getName(),
                            book.getAuthor().getName().length() > 15 ? book.getAuthor().getName().substring(0, 12) + "..." : book.getAuthor().getName()));
                }

                if (table.length() > 0) {
                    sendMessage.setText("```" + table.toString() + "```");
                    sendMessage.setParseMode("Markdown");
                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }

                menu(sendMessage);

                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                int i = Integer.parseInt(msg.getText().trim());
            } catch (NumberFormatException nfe) {
                deleteAction = false;
            }

            if (deleteAction){
                sendMessage.setChatId(msg.getChatId());
                int id = Integer.parseInt(msg.getText());
                boolean check = false;

                for (Book bookn : bookService.findAll()){
                    if (bookn.getId() == id)
                        check = true;
                }

                if (check) {
                    deleteAction = false;
                    bookController.delete(id);
                    sendMessage.setText("Видалено книгу №" + id);
                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }

                    menu(sendMessage);
                } else {
                    sendMessage.setText("INVALID NUMBER");
                    deleteAction = true;
                }

                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }


            if (findAction){
                sendMessage.setChatId(msg.getChatId());
                String keyword = msg.getText();

                sendMessage.setText("Знайдено: \n\n" +
                        bookService.getByKeyword(keyword).toString());
                findAction = false;
                if (bookService.getByKeyword(keyword).isEmpty())
                    sendMessage.setText("Нічого не знайдено");

                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }

                menu(sendMessage);
                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }

            if (msg.getText().trim().equals("Видалити книгу за номером")){
                sendMessage.setChatId(msg.getChatId());
                sendMessage.setText("Введіть id для видалення");

                deleteAction = true;

                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }

            if (msg.getText().trim().equals("Знайти книгу за назвою")){
                sendMessage.setChatId(msg.getChatId());
                sendMessage.setText("Введіть назву для пошуку");

                findAction = true;

                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }

            if (msg.getText().trim().equals("/functions")) {
                sendMessage.setChatId(msg.getChatId());

                menu(sendMessage);

                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }

            if (msg.getText().trim().equals("Додати книгу")) {
                sendMessage.setChatId(msg.getChatId());
                sendMessage.setText("Введіть назву");

                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                state = BotState.STEP_1;
                return;
            }

            switch (state) {
                case STEP_1:
                    if (msg.getText().isEmpty()) {
                        sendMessage.setChatId(msg.getChatId());
                        sendMessage.setText("Назва книги не може бути порожньою. Введіть назву книги:");
                        try {
                            execute(sendMessage);
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                        return;
                    }
                    book.setName(msg.getText());
                    sendMessage.setChatId(msg.getChatId());
                    sendMessage.setText("Введіть автора:");
                    state = BotState.STEP_2;
                    executeMessage(sendMessage);
                    break;

                case STEP_2:
                    if (msg.getText().isEmpty()) {
                        sendMessage.setChatId(msg.getChatId());
                        sendMessage.setText("Ім’я автора не може бути порожнім. Введіть автора:");
                        executeMessage(sendMessage);
                        return;
                    }
                    book.setAuthor(new Author(msg.getText()));
                    sendMessage.setChatId(msg.getChatId());
                    sendMessage.setText("Введіть рік виходу:");
                    state = BotState.STEP_3;
                    executeMessage(sendMessage);
                    break;

                case STEP_3:
                    try {
                        int year = Integer.parseInt(msg.getText());
                        if (year < 1000 || year > 2025) {
                            throw new NumberFormatException();
                        }
                        book.setRelease_year(msg.getText());
                        sendMessage.setChatId(msg.getChatId());
                        sendMessage.setText("Введіть кількість сторінок:");
                        state = BotState.STEP_4;
                    } catch (NumberFormatException e) {
                        sendMessage.setChatId(msg.getChatId());
                        sendMessage.setText("Невірний рік. Будь ласка, введіть дійсний рік (наприклад, 2023):");
                    }
                    executeMessage(sendMessage);
                    break;

                case STEP_4:
                    try {
                        int pages = Integer.parseInt(msg.getText());
                        if (pages <= 0) {
                            throw new NumberFormatException();
                        }
                        book.setPage_count(msg.getText());
                        sendMessage.setChatId(msg.getChatId());
                        sendMessage.setText("Введіть опис книги:");
                        state = BotState.STEP_5;
                    } catch (NumberFormatException e) {
                        sendMessage.setChatId(msg.getChatId());
                        sendMessage.setText("Кількість сторінок має бути позитивним цілим числом. Спробуйте ще раз:");
                    }
                    executeMessage(sendMessage);
                    break;

                case STEP_5:
                    if (msg.getText().isEmpty()) {
                        sendMessage.setChatId(msg.getChatId());
                        sendMessage.setText("Опис не може бути порожнім. Введіть опис книги:");
                        executeMessage(sendMessage);
                        return;
                    }
                    book.setDescription(msg.getText());
                    sendMessage.setChatId(msg.getChatId());
                    sendMessage.setText("Введіть рейтинг книги (від 0 до 10):");
                    state = BotState.STEP_6;
                    executeMessage(sendMessage);
                    break;

                case STEP_6:
                    System.out.println(msg.getText());
                    if (characters == 1){
                        book.setRating(Integer.parseInt(msg.getText()));
                    }else namesList.add(msg.getText());

                    sendMessage.setChatId(msg.getChatId());
                    sendMessage.setText("Введіть роль " + characters + " героя");

                    ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                    List<KeyboardRow> rowsReply = new ArrayList<>();
                    KeyboardRow rowReply = new KeyboardRow();

                    rowReply.add("main");
                    rowReply.add("secondary");
                    rowReply.add("episodic");

                    rowsReply.add(rowReply);
                    replyKeyboardMarkup.setKeyboard(rowsReply);
                    sendMessage.setReplyMarkup(replyKeyboardMarkup);

                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    state = BotState.STEP_7;
                    break;

                case STEP_7:
                    System.out.println(msg.getText());

                    List<String> validRoles = List.of("main", "secondary", "episodic");

                    if (!validRoles.contains(msg.getText().toLowerCase())) {
                        sendMessage.setChatId(msg.getChatId());
                        sendMessage.setText("Будь ласка, виберіть одну з правильних ролей: 'main', 'secondary' або 'episodic'.");

                        ReplyKeyboardMarkup replyKeyboardMarkupC = new ReplyKeyboardMarkup();
                        List<KeyboardRow> rowsReplyC = new ArrayList<>();
                        KeyboardRow rowReplyC = new KeyboardRow();

                        rowReplyC.add("main");
                        rowReplyC.add("secondary");
                        rowReplyC.add("episodic");

                        rowsReplyC.add(rowReplyC);
                        replyKeyboardMarkupC.setKeyboard(rowsReplyC);
                        sendMessage.setReplyMarkup(replyKeyboardMarkupC);

                        try {
                            execute(sendMessage);
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }

                        return;
                    }

                    if (characters == 1) {
                        rolesList.add("main");
                    } else {
                        rolesList.add(msg.getText());
                    }

                    sendMessage.setChatId(msg.getChatId());
                    sendMessage.setText("Як його звати?");

                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }

                    characters++;

                    if (characters < 4) {
                        state = BotState.STEP_6;
                    } else {
                        state = BotState.STEP_8;
                    }
                    break;


                case STEP_8:
                    if (msg.getText().isEmpty()) {
                        sendMessage.setChatId(msg.getChatId());
                        sendMessage.setText("Ім’я героя не може бути порожнім. Введіть ім’я:");
                        executeMessage(sendMessage);
                        return;
                    }
                    namesList.add(msg.getText());
                    if (characters < 3) {
                        characters++;
                        state = BotState.STEP_6;
                    } else {
                        List<Character> characterList = new ArrayList<>();
                        for (int i = 0; i < namesList.size(); i++) {
                            characterList.add(new Character(namesList.get(i), rolesList.get(i)));
                        }
                        book.setCharacters(characterList);
                        bookController.saveBook(book);

                        sendMessage.setChatId(msg.getChatId());
                        sendMessage.setText("Книга успішно додана! 🎉");
                        executeMessage(sendMessage);
                        characters = 1;
                        namesList.clear();
                        rolesList.clear();
                        book = new Book();
                        state = BotState.STEP_0;
                    }
                    menu(sendMessage);
                    executeMessage(sendMessage);
                    break;
            }
        }
        }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


    private static void menu(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rowsReply = new ArrayList<>();
        KeyboardRow rowReply = new KeyboardRow();
        rowReply.add("Додати книгу");
        rowReply.add("Отримати список книжок");
        rowReply.add("Видалити книгу за номером");
        rowReply.add("Знайти книгу за назвою");
        rowsReply.add(rowReply);
        replyKeyboardMarkup.setKeyboard(rowsReply);

        sendMessage.setText("Доступні функції: ");
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }
}
