package com.gafbank.rashop.service.managers.productInfo;

import com.gafbank.rashop.Repository.ProductRepository;
import com.gafbank.rashop.Repository.UserRepository;
import com.gafbank.rashop.service.factory.KeyboardFactory;
import com.gafbank.rashop.service.factory.MethodFactory;
import com.gafbank.rashop.service.managers.AbstractManager;
import com.gafbank.rashop.telegram.Bot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

import static com.gafbank.rashop.service.data.CallbackData.*;

@Component
@Slf4j
public class ProductInfoManager extends AbstractManager {
    private final MethodFactory methodFactory;
    private final KeyboardFactory keyboardFactory;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Autowired
    public ProductInfoManager(MethodFactory methodFactory, KeyboardFactory keyboardFactory, ProductRepository productRepository, UserRepository userRepository) {
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        return null;
    }

    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        return null;
    }

    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        log.info(callbackQuery.getData());
        String[] splitCallbackData = callbackQuery.getData().split("_");
        switch (splitCallbackData[1]){
            case INFO -> {
                log.info(callbackQuery.getData());
                try {
                    return infoMenu(callbackQuery, bot, splitCallbackData[2]);
                } catch (TelegramApiException e) {
                    log.error(e.getMessage());
                }
            }
            case REMOVE -> {
                log.info(callbackQuery.getData());
                try {
                    return removeProduct(callbackQuery, bot, splitCallbackData[2]);
                } catch (TelegramApiException e) {
                    log.error(e.getMessage());
                }
            }
            case BUY ->{
                return buyInfo(callbackQuery, bot, splitCallbackData[2], splitCallbackData[3]);
            }
        }


        return null;
    }

    private BotApiMethod<?> buyInfo(CallbackQuery callbackQuery, Bot bot, String id, String userid) {

        try {
            bot.execute(methodFactory.getDeleteMessage(
                    callbackQuery.getMessage().getChatId(),
                    callbackQuery.getMessage().getMessageId()-1
            ));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        var product = productRepository.findById(Long.valueOf(id)).orElseThrow();
        StringBuilder sb = new StringBuilder();
        String text = sb.append("\uD83C\uDFF7 Номер товара: ").append(product.getProductId()).append("\n\n")
                .append("\uD83D\uDCF1 Контакты продавца: @").append(product.getUser().getUsername()).toString();

        return methodFactory.getEditMessageText(
                callbackQuery,
                text,
                keyboardFactory.getInlineKeyboard(
                        List.of("↩\uFE0F Назад"),
                        List.of(1),
                        List.of(PRODUCT_INFO + id + "_" + userid)
                )
        );
    }

    private BotApiMethod<?> removeProduct(CallbackQuery callbackQuery,Bot bot, String id) throws TelegramApiException {
        var productId = Long.valueOf(id);
        var product = productRepository.findById(productId);
        var user = userRepository.findUserByChatId(callbackQuery.getMessage().getChatId());
        if (user != null && product.isPresent()) {
            user.getProducts().removeIf(p -> p.getProductId().equals(productId));
            userRepository.save(user);
            try {
                bot.execute(methodFactory.getDeleteMessage(
                        callbackQuery.getMessage().getChatId(),
                        callbackQuery.getMessage().getMessageId()-1
                ));
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
            userRepository.save(user);
            return methodFactory.getEditMessageText(
                    callbackQuery,
                    "\uD83D\uDEAB Товар удален из корзины",
                    keyboardFactory.getInlineKeyboard(
                            List.of("↩\uFE0F Вернуться в корзину"),
                            List.of(1),
                            List.of(BOX)
                    )

            );
        }
        return null;

    }

        private BotApiMethod<?> infoMenu (CallbackQuery callbackQuery, Bot bot, String splitCallbackData) throws
        TelegramApiException {
            var product = productRepository.findById(Long.valueOf(splitCallbackData)).orElseThrow();
            String text = "\uD83C\uDFF7\uFE0F Номер товара: " + product.getProductId() + "\n\n" +
                    "\uD83D\uDCCC Название: " + product.getTitle() + "\n\n" +
                    "\uD83D\uDCB5 Цена: " + product.getPrice() + "\n" +
                    "\uD83D\uDDD2 Описание: \n" + product.getDescription() + "\n\n" +
                    "\uD83D\uDCA1 Категория: " + product.getNameCategory() + "\n\n";


            InputFile inputMedia = new InputFile();
            inputMedia.setMedia(product.getPhotoId());
            SendPhoto sendPhoto = methodFactory.getSendPhoto(
                    callbackQuery.getMessage().getChatId(),
                    "",
                    inputMedia);

            SendMessage sendMessage = SendMessage.builder()
                    .chatId(callbackQuery.getMessage().getChatId())
                    .text(text)
                    .replyMarkup(keyboardFactory.getInlineKeyboard(
                            List.of("Купить \uD83D\uDCB0",
                                    "Убрать \uD83D\uDEAB",
                                    "Назад ↩️\uFE0F"),
                            List.of(1, 2),
                            List.of(PRODUCT_BUY  + product.getProductId() + "_" + callbackQuery.getMessage().getChatId(),
                                    PRODUCT_REMOVE + product.getProductId(),
                                    BOX)
                    ))
                    .build();
            bot.execute(methodFactory.getDeleteMessage(
                    callbackQuery.getMessage().getChatId(),
                    callbackQuery.getMessage().getMessageId()
            ));
            try {
                bot.execute(sendPhoto);
                return sendMessage;
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


