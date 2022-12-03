package com.project.task.bot;

import com.project.task.Entities.Author;
import com.project.task.Entities.Book;
import com.project.task.Entities.Character;
import com.project.task.bot.BotState;
import com.project.task.controller.BookController;
import com.project.task.service.AuthorService;
import com.project.task.service.BookService;
import com.project.task.service.CharacterService;
import com.project.task.service.SeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
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
            if (msg.getText().trim().equals("Отримати список книжок")){
                sendMessage.setChatId(msg.getChatId());
                List<Book> l = bookService.findAll();
                sendMessage.setText(l.toString());

                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }

                ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                List<KeyboardRow> rowsReply = new ArrayList<>();
                KeyboardRow rowReply = new KeyboardRow();
                rowReply.add("Додати книгу");
                rowReply.add("Отримати список книжок");
                rowReply.add("Видалити книгу за номером");
                rowReply.add("Знайти книгу за назвою");
                rowsReply.add(rowReply);
                replyKeyboardMarkup.setKeyboard(rowsReply);

                sendMessage.setChatId(msg.getChatId());
                sendMessage.setText("Доступні функції: ");
                sendMessage.setReplyMarkup(replyKeyboardMarkup);
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

                if (check){
                    sendMessage.setText("Видалено книгу №" + id);
                    deleteAction = false;
                    bookController.delete(id);
                }else
                    sendMessage.setText("INVALID NUMBER");
                    deleteAction = true;

                try {
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }

                ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                List<KeyboardRow> rowsReply = new ArrayList<>();
                KeyboardRow rowReply = new KeyboardRow();
                rowReply.add("Додати книгу");
                rowReply.add("Отримати список книжок");
                rowReply.add("Видалити книгу за номером");
                rowReply.add("Знайти книгу за назвою");
                rowsReply.add(rowReply);
                replyKeyboardMarkup.setKeyboard(rowsReply);

                sendMessage.setChatId(msg.getChatId());
                sendMessage.setText("Доступні функції: ");
                sendMessage.setReplyMarkup(replyKeyboardMarkup);
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

                ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                List<KeyboardRow> rowsReply = new ArrayList<>();
                KeyboardRow rowReply = new KeyboardRow();
                rowReply.add("Додати книгу");
                rowReply.add("Отримати список книжок");
                rowReply.add("Видалити книгу за номером");
                rowReply.add("Знайти книгу за назвою");
                rowsReply.add(rowReply);
                replyKeyboardMarkup.setKeyboard(rowsReply);

                sendMessage.setChatId(msg.getChatId());
                sendMessage.setText("Доступні функції: ");
                sendMessage.setReplyMarkup(replyKeyboardMarkup);
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
                sendMessage.setText("Доступні функції: ");

                ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                List<KeyboardRow> rowsReply = new ArrayList<>();
                KeyboardRow rowReply = new KeyboardRow();
                rowReply.add("Додати книгу");
                rowReply.add("Отримати список книжок");
                rowReply.add("Видалити книгу за номером");
                rowReply.add("Знайти книгу за назвою");
                rowsReply.add(rowReply);
                replyKeyboardMarkup.setKeyboard(rowsReply);
                sendMessage.setReplyMarkup(replyKeyboardMarkup);

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

            switch (state){
                case STEP_1:
                    System.out.println(msg.getText());
                    book.setName(msg.getText());

                    sendMessage.setChatId(msg.getChatId());
                    sendMessage.setText("Введіть автора");

                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    state = BotState.STEP_2;
                    break;
                case STEP_2:
                    System.out.println(msg.getText());
                    Author author = new Author(msg.getText());
                    book.setAuthor(author);

                    sendMessage.setChatId(msg.getChatId());
                    sendMessage.setText("Введіть рік виходу");

                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    state = BotState.STEP_3;
                    break;
                case STEP_3:
                    System.out.println(msg.getText());
                    book.setRelease_year(msg.getText());

                    sendMessage.setChatId(msg.getChatId());
                    sendMessage.setText("Введіть кількість сторінок");

                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    state = BotState.STEP_4;
                    break;
                case STEP_4:
                    System.out.println(msg.getText());
                    book.setPage_count(msg.getText());

                    sendMessage.setChatId(msg.getChatId());
                    sendMessage.setText("Введіть опис");

                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    state = BotState.STEP_5;
                    break;
                case STEP_5:
                    System.out.println(msg.getText());
                    book.setDescription(msg.getText());

                    sendMessage.setChatId(msg.getChatId());
                    sendMessage.setText("Введіть рейтинг: від 0 до 100");

                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    state = BotState.STEP_6;
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
                    rolesList.add(msg.getText());

                    sendMessage.setChatId(msg.getChatId());
                    sendMessage.setText("Як його звати?");

                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    characters++;

                    if (characters < 4){
                        state = BotState.STEP_6;
                    }else
                        state = BotState.STEP_8;
                    break;
                case STEP_8:
                    System.out.println(msg.getText());
                    namesList.add(msg.getText());
                    List<Character> characterList = new ArrayList<>();

                    for (int i = 0; i < 3; i++){
                        Character c = new Character(namesList.get(i), rolesList.get(i));
                        characterList.add(c);
                    }
                    book.setCharacters(characterList);
                    bookController.saveBook(book);

                    sendMessage.setChatId(msg.getChatId());
                    sendMessage.setText("Книга була додана, дякую!");

                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }

                    characters = 1;
                    namesList = new ArrayList<>();
                    rolesList = new ArrayList<>();
                    book = new Book();

                    state = BotState.STEP_0;

                    sendMessage.setChatId(msg.getChatId());
                    sendMessage.setText("Доступні функції: ");
                    ReplyKeyboardMarkup replyKeyboardMarkup2 = new ReplyKeyboardMarkup();
                    List<KeyboardRow> rowsReply2 = new ArrayList<>();
                    KeyboardRow rowReply2 = new KeyboardRow();

                    rowReply2.add("Додати книгу");
                    rowReply2.add("Отримати список книжок");
                    rowReply2.add("Видалити книгу за номером");
                    rowReply2.add("Знайти книгу за назвою");

                    rowsReply2.add(rowReply2);
                    replyKeyboardMarkup2.setKeyboard(rowsReply2);
                    sendMessage.setReplyMarkup(replyKeyboardMarkup2);
                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                }
            }
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
