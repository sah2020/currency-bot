import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CurrencyBot extends TelegramLongPollingBot {
    public static final String botToken = "1994614660:AAGFEWz9CHnNP6HLxvVZg4jY56voX5yhADs"; //botni uniqal kaliti
    public static final String botUserName = "something_by_someone_bot";

    public String getBotUsername() {
        return botUserName;
    }

    public String getBotToken() {
        return botToken;
    }

    int level = 0; //qadam
    Long chatId;
    String text = "";
    String uzsToOther = "";
    String currency = "";
    double amount = 0;

    @SneakyThrows
    public void onUpdateReceived(Update update) {

        Message message = update.getMessage();
        SendMessage sendMessage = new SendMessage();

        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
            text = update.getMessage().getText();

            if (text.equals("/start")) {
                sendMessage.setText("Currency botga Xush kelibsiz!");
                level = 0;
            } else if (text.equals("UZS --> Other") || text.equals("Other --> UZS")) {
                uzsToOther = text;
                sendMessage.setText("Valyutalar ro'yxati :");
                level = 1;
            } else if (text.equals("USD") || text.equals("EUR") || text.equals("CNY")) {
                currency = text;
                sendMessage.setText("Qiymatni kiriting: ");
                level = 2;
            } else {
                amount = Double.parseDouble(text);
                level = 3;
            }
            sendMessage.setChatId(chatId);
            switch (level) {
                case 0:
                    chooseType(sendMessage); //tilni tanglash un buttonlar chiqarishi kk
                    level = 1;
                    break;
                case 1:
                    try {
                        getCurrency(sendMessage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    level = 2;
                    break;
                case 2:
                    ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
                    sendMessage.setReplyMarkup(replyKeyboardRemove);
                    level = 3;
                    break;
                case 3:
                    sendMessage.setChatId(chatId);
                    sendMessage.setText(String.valueOf(calculate(uzsToOther, currency, amount)) + " " + currency);
                    break;
            }
        }
        execute(sendMessage);
    }


    public void getCurrency(SendMessage sendMessage) throws IOException {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        replyKeyboardMarkup.setSelective(true); //tanlash xususiyati
        replyKeyboardMarkup.setResizeKeyboard(true); //avto size
        replyKeyboardMarkup.setOneTimeKeyboard(false); //bir martalikmi?

        List<KeyboardRow> keyboardRows = new ArrayList<KeyboardRow>();

        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(new KeyboardButton("USD"));
        keyboardRow.add(new KeyboardButton("EUR"));
        keyboardRow.add(new KeyboardButton("CNY"));
        keyboardRows.add(keyboardRow);

        replyKeyboardMarkup.setKeyboard(keyboardRows);
    }

    public void chooseType(SendMessage sendMessage) {

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        replyKeyboardMarkup.setSelective(true); //tanlash xususiyati
        replyKeyboardMarkup.setResizeKeyboard(true); //avto size
        replyKeyboardMarkup.setOneTimeKeyboard(false); //bir martalikmi?

        List<KeyboardRow> keyboardRows = new ArrayList<KeyboardRow>();

        KeyboardRow firstRow = new KeyboardRow();
        KeyboardRow secondRow = new KeyboardRow();
        firstRow.add(new KeyboardButton("UZS --> Other"));
        secondRow.add(new KeyboardButton("Other --> UZS"));

        keyboardRows.add(firstRow);
        keyboardRows.add(secondRow);

        replyKeyboardMarkup.setKeyboard(keyboardRows);

    }

    public static double calculate(String uzs, String currency, double amount) throws IOException {
        ArrayList<Currency> currencies = CurrencyUtil.getCurrency();
        double r = 0;

        if (uzs.equals("Other --> UZS")) {
            switch (currency) {
                case "USD":
                    r = amount * Double.parseDouble(currencies.get(0).getRate());
                    break;
                case "EUR":
                    r = amount * Double.parseDouble(currencies.get(1).getRate());
                    break;
                case "CNY":
                    r = amount * Double.parseDouble(currencies.get(14).getRate());
                    break;
            }
        } else {
            switch (currency) {
                case "USD":
                    r = amount / Double.parseDouble(currencies.get(0).getRate());
                    break;
                case "EUR":
                    r = amount / Double.parseDouble(currencies.get(1).getRate());
                    break;
                case "CNY":
                    r = amount / Double.parseDouble(currencies.get(14).getRate());
                    break;
            }
        }
        return r;
    }
}
