package com.gafbank.rashop.service.managers.start;

import com.gafbank.rashop.service.factory.KeyboardFactory;
import com.gafbank.rashop.service.factory.MethodFactory;
import com.gafbank.rashop.service.managers.AbstractManager;
import com.gafbank.rashop.telegram.Bot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

import static com.gafbank.rashop.service.data.CallbackData.MENU;

@Component
public class StartManager extends AbstractManager {
    private final MethodFactory methodFactory;
    private final KeyboardFactory keyboardFactory;
    @Autowired

    public StartManager(MethodFactory methodFactory, KeyboardFactory keyboardFactory) {
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return mainMenu(message);
    }



    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return null;
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        return null;
    }

    private BotApiMethod<?> mainMenu(Message message) {
        var userName = message.getFrom().getFirstName();
        return methodFactory.getSendMessage(
                message.getChatId(),
                "\uD83E\uDD16 Привет, " + userName + "!" +"\n\nХочешь совершить самую непредесказуемую покупку?\uD83D\uDECD \n\n" +
                        "Если возникнут вопросы нажми на кнопку \"Помощь\" или введи /help",
                keyboardFactory.getInlineKeyboard(
                        List.of("Начать"),
                        List.of(1),
                        List.of(MENU)
                )
        );
    }
}
