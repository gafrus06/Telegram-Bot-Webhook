package com.gafbank.rashop.service.managers.feedback;

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
public class FeedbackManager extends AbstractManager {
    private final MethodFactory methodFactory;
    private final KeyboardFactory keyboardFactory;

    @Autowired
    public FeedbackManager(MethodFactory methodFactory, KeyboardFactory keyboardFactory) {
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return methodFactory.getSendMessage(
                message.getChatId(),
                """
                        Автор 👨‍💻
                        
                        Руслан Гафиятов
                        Telegram: https://t.me/russlan_1esse
                        GitHub: https://github.com/gafrus06
                        VK: https://vk.com/rgafiatov
                        
                        ⚠️Мы не ответственны за потерю товара при доставке,
                        за плохое качество или за другие виды мошенничества по отношению к Вам
                        """,
                keyboardFactory.getInlineKeyboard(
                        List.of("Перейти в меню"),
                        List.of(1),
                        List.of(MENU)

                )
        );
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return null;
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        return methodFactory.getEditMessageText(
                callbackQuery,
                """
                        Автор 👨‍💻
                        
                        Руслан Гафиятов
                        Telegram: https://t.me/russlan_1esse
                        GitHub: https://github.com/gafrus06
                        VK: https://vk.com/rgafiatov
                        
                       
                        ⚠️Мы не ответственны за потерю товара при доставке,
                        за плохое качество или за другие виды мошенничества по отношению к Вам
                        """,
                keyboardFactory.getInlineKeyboard(
                        List.of("Назад"),
                        List.of(1),
                        List.of(MENU)

                )
        );
    }
}
