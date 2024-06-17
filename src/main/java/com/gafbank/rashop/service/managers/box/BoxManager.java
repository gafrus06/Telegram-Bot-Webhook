package com.gafbank.rashop.service.managers.box;

import com.gafbank.rashop.Repository.ProductRepository;
import com.gafbank.rashop.Repository.UserRepository;
import com.gafbank.rashop.entity.Product;
import com.gafbank.rashop.service.factory.KeyboardFactory;
import com.gafbank.rashop.service.factory.MethodFactory;
import com.gafbank.rashop.service.managers.AbstractManager;
import com.gafbank.rashop.telegram.Bot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

import static com.gafbank.rashop.service.data.CallbackData.*;

@Component
@Slf4j
public class BoxManager extends AbstractManager {
    private final UserRepository userRepository;
    private final KeyboardFactory keyboardFactory;
    private final MethodFactory methodFactory;
    private final ProductRepository productRepository;

    @Autowired
    public BoxManager(UserRepository userRepository, KeyboardFactory keyboardFactory, MethodFactory methodFactory, ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.keyboardFactory = keyboardFactory;
        this.methodFactory = methodFactory;
        this.productRepository = productRepository;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return mainMenu(message, bot);
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return null;
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        try {
            bot.execute(methodFactory.getDeleteMessage(callbackQuery.getMessage().getChatId(),
                    callbackQuery.getMessage().getMessageId()-1));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        return mainMenu(callbackQuery, bot);
    }

    private BotApiMethod<?> mainMenu(CallbackQuery callbackQuery, Bot bot) {

        List<String> text = new ArrayList<>();
        List<Integer> cfg = new ArrayList<>();
        List<String> data = new ArrayList<>();
        int index = 0;
        var user = userRepository.findUserByChatId(callbackQuery.getMessage().getChatId());
        for (Product product : user.getProducts()) {
            text.add(product.getTitle());
            data.add(PRODUCT_INFO + product.getProductId() + "_" + user.getChatId());
            log.info(PRODUCT_INFO + product.getProductId() + "_" + user.getChatId());
            if (index == 2) {
                cfg.add(3);
                index = 0;
            } else {
                index += 1;
            }
        }
        if (index != 0) {
            cfg.add(index);
        }
        cfg.add(1);
        text.add("Назад");
        data.add(MENU);
        String textMessage = "Корзина 🛒\n\n"+ "Товаров: " + user.getProducts().size() + "шт.";
        if(user.getProducts().size() == 0){
            textMessage = "Корзина 🛒\n\n"+ "Ваша корзина пуста";
        }
        log.info(data.toString());
        return methodFactory.getEditMessageText(
                callbackQuery,
                textMessage,
                keyboardFactory.getInlineKeyboard(
                        text,
                        cfg,
                        data
                )
        );
    }
    private BotApiMethod<?> mainMenu(Message message, Bot bot) {

        List<String> text = new ArrayList<>();
        List<Integer> cfg = new ArrayList<>();
        List<String> data = new ArrayList<>();
        int index = 0;
        var user = userRepository.findUserByChatId(message.getChatId());
        for (Product product : user.getProducts()) {
            text.add(product.getTitle());
            data.add(PRODUCT_INFO + product.getProductId() + "_" + user.getChatId());
            log.info(PRODUCT_INFO + product.getProductId() + "_" + user.getChatId());
            if (index == 2) {
                cfg.add(3);
                index = 0;
            } else {
                index += 1;
            }
        }
        if (index != 0) {
            cfg.add(index);
        }
        cfg.add(1);
        text.add("Назад");
        data.add(MENU);
        String textMessage = "Корзина 🛒\n\n"+ "Товаров: " + user.getProducts().size() + "шт.";
        if(user.getProducts().size() == 0){
            textMessage = "Корзина 🛒\n\n"+ "Ваша корзина пуста";
        }
        log.info(data.toString());
        return methodFactory.getSendMessage(
                message.getChatId(),
                textMessage,
                keyboardFactory.getInlineKeyboard(
                        text,
                        cfg,
                        data
                )
        );
    }

}