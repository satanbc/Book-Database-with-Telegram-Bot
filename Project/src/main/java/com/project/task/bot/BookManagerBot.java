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
import org.springframework.stereotype.Component;
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

            if (msg.getText().trim().equals("/help")){
                SendMessage helpMessage = new SendMessage();
                helpMessage.setChatId(msg.getChatId());
                helpMessage.setText("✨<b>Існуючі команди</b>✨\n\n"
                        + "/functions - виконати дії над базою книжок\n"
                        + "/help - отримати список команд\n");

                try {
                    execute(helpMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }

            else if (msg.getText().trim().equals("/functions")) {
                SendMessage message = new SendMessage();
                message.setChatId(msg.getChatId());
                message.setText("Here is your keyboard");

                ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                List<KeyboardRow> rowsReply = new ArrayList<>();
                KeyboardRow rowReply = new KeyboardRow();
                rowReply.add("Add a new book");

                rowsReply.add(rowReply);
                replyKeyboardMarkup.setKeyboard(rowsReply);
                message.setReplyMarkup(replyKeyboardMarkup);

                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }

            if (msg.getText().trim().equals("Add a new book")) {
                SendMessage message1 = new SendMessage();
                message1.setChatId(msg.getChatId());
                message1.setText("Please, enter the name");

                try {
                    execute(message1);
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

                    SendMessage message2 = new SendMessage();
                    message2.setChatId(msg.getChatId());
                    message2.setText("Please, enter the author");

                    try {
                        execute(message2);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    state = BotState.STEP_2;
                    break;
                case STEP_2:
                    System.out.println(msg.getText());
                    Author author = new Author(msg.getText());
                    book.setAuthor(author);

                    SendMessage message3 = new SendMessage();
                    message3.setChatId(msg.getChatId());
                    message3.setText("Please, enter the release year");

                    try {
                        execute(message3);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    state = BotState.STEP_3;
                    break;
                case STEP_3:
                    System.out.println(msg.getText());
                    book.setRelease_year(msg.getText());

                    SendMessage message4 = new SendMessage();
                    message4.setChatId(msg.getChatId());
                    message4.setText("Please, enter the page count");

                    try {
                        execute(message4);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    state = BotState.STEP_4;
                    break;
                case STEP_4:
                    System.out.println(msg.getText());
                    book.setPage_count(msg.getText());

                    SendMessage message5 = new SendMessage();
                    message5.setChatId(msg.getChatId());
                    message5.setText("Please, enter the description");

                    try {
                        execute(message5);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    state = BotState.STEP_5;
                    break;
                case STEP_5:
                    System.out.println(msg.getText());
                    book.setDescription(msg.getText());


                    SendMessage message6 = new SendMessage();
                    message6.setChatId(msg.getChatId());
                    message6.setText("Please, enter the rating");

                    try {
                        execute(message6);
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

                    SendMessage message8 = new SendMessage();
                    message8.setChatId(msg.getChatId());
                    message8.setText("Please, enter the " + characters + " character's role");

                    ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                    List<KeyboardRow> rowsReply = new ArrayList<>();
                    KeyboardRow rowReply = new KeyboardRow();

                    rowReply.add("main");
                    rowReply.add("secondary");
                    rowReply.add("episodic");

                    rowsReply.add(rowReply);
                    replyKeyboardMarkup.setKeyboard(rowsReply);
                    message8.setReplyMarkup(replyKeyboardMarkup);

                    try {
                        execute(message8);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    state = BotState.STEP_7;
                    break;
                case STEP_7:
                    System.out.println(msg.getText());
                    rolesList.add(msg.getText());

                    SendMessage message7 = new SendMessage();
                    message7.setChatId(msg.getChatId());
                    message7.setText("What's its name?");

                    try {
                        execute(message7);
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

                    SendMessage message9 = new SendMessage();
                    message9.setChatId(msg.getChatId());
                    message9.setText("Book was added, thanks!");

                    try {
                        execute(message9);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }

                    characters = 1;
                    namesList = new ArrayList<>();
                    rolesList = new ArrayList<>();
                    book = new Book();

                    state = BotState.STEP_0;
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
