package com.gafbank.rashop.service.managers.help;

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
public class HelpManager extends AbstractManager {

    private final KeyboardFactory keyboardFactory;
    private final MethodFactory methodFactory;

    @Autowired
    public HelpManager(KeyboardFactory keyboardFactory, MethodFactory methodFactory) {
        this.keyboardFactory = keyboardFactory;
        this.methodFactory = methodFactory;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return methodFactory.getSendMessage(
                message.getChatId(),
                """
                        Помощь ℹ️
                        
                        Этот бот предоставляет случайные товары по определенной категории.
                        Вам остается только добавить в корзину или пропустить товар
                        
                        Команды:
                        /go - Начать покупки
                        /help - Помощь
                        /feedback - Автор
                        /menu - Главное меню
                        /box - Корзина
                        
                        ⚠️Мы не ответственны за потерю товара при доставке,
                        за плохое качество или за другие виды мошенничества по отношению к Вам
                        
                        Приятных покупок!
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
                        Помощь ℹ️
                        
                        Этот бот предоставляет случайные товары по определенной категории.
                        Вам остается только добавить в корзину или пропустить товар
                        
                        Команды:
                        /go - Начать покупки
                        /help - Помощь
                        /feedback - Автор
                        /menu - Главное меню
                        /box - Корзина
                        
                        ⚠️Мы не ответственны за потерю товара при доставке,
                        за плохое качество или за другие виды мошенничества по отношению к Вам
                        
                        Приятных покупок!
                                """,
                keyboardFactory.getInlineKeyboard(
                        List.of("Назад"),
                        List.of(1),
                        List.of(MENU)
                )
        );
    }
}
