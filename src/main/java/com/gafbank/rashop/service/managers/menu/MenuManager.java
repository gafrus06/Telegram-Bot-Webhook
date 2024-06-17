package com.gafbank.rashop.service.managers.menu;

import com.gafbank.rashop.service.factory.KeyboardFactory;
import com.gafbank.rashop.service.factory.MethodFactory;
import com.gafbank.rashop.service.managers.AbstractManager;
import com.gafbank.rashop.service.managers.feedback.FeedbackManager;
import com.gafbank.rashop.service.managers.help.HelpManager;
import com.gafbank.rashop.telegram.Bot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

import static com.gafbank.rashop.service.data.CallbackData.*;

@Component
@Slf4j
public class MenuManager extends AbstractManager {
    private final MethodFactory methodFactory;
    private final KeyboardFactory keyboardFactory;

    @Autowired
    public MenuManager(MethodFactory methodFactory, KeyboardFactory keyboardFactory) {
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;

    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        try {
            return mainMenu(message, bot);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return null;
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        try {
            return mainMenu(callbackQuery, bot);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    private BotApiMethod<?> mainMenu(CallbackQuery callbackQuery, Bot bot) throws TelegramApiException {
        return methodFactory.getEditMessageText(
                callbackQuery,
                """
                        Меню📌
                                                
                                 𝐑𝐀𝐍𝐃𝐎𝐌 𝐌𝐀𝐑𝐊𝐄𝐓 
                                                           
                                                           
                                                                
                        """,
                keyboardFactory.getInlineKeyboard(
                        List.of("Начать покупки \uD83D\uDECD",
                                "Корзина \uD83D\uDED2",
                                "Помощь ℹ\uFE0F",
                                "Автор \uD83D\uDC68\u200D\uD83D\uDCBB"),
                        List.of(1, 1, 1, 1),
                        List.of(GO, BOX, HELP, FEEDBACK)
                )
        );

    }

    private BotApiMethod<?> mainMenu(Message message, Bot bot) throws TelegramApiException {
        return methodFactory.getSendMessage(
                message.getChatId(),
                """
                        Меню📌
                                                
                                 𝐑𝐀𝐍𝐃𝐎𝐌 𝐌𝐀𝐑𝐊𝐄𝐓
                                                                
                                                                
                                                                
                        """,
                keyboardFactory.getInlineKeyboard(
                        List.of("Начать покупки \uD83D\uDECD",
                                "Корзина \uD83D\uDED2",
                                "Помощь ℹ\uFE0F",
                                "Автор \uD83D\uDC68\u200D\uD83D\uDCBB"),
                        List.of(1, 1, 1, 1),
                        List.of(GO, BOX, HELP, FEEDBACK)
                )
        );
    }
}
