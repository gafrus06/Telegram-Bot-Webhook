package com.gafbank.rashop.service.managers.buy;

import com.gafbank.rashop.Repository.CategoryRepository;
import com.gafbank.rashop.Repository.ProductRepository;
import com.gafbank.rashop.Repository.UserRepository;
import com.gafbank.rashop.entity.Action;
import com.gafbank.rashop.service.factory.KeyboardFactory;
import com.gafbank.rashop.service.factory.MethodFactory;
import com.gafbank.rashop.service.managers.AbstractManager;
import com.gafbank.rashop.telegram.Bot;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;
import java.util.List;

import static com.gafbank.rashop.service.data.CallbackData.*;


@Component
@Slf4j
public class BuyManager extends AbstractManager {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final MethodFactory methodFactory;
    private final KeyboardFactory keyboardFactory;
    private int currentPage = 1;

    @Autowired
    public BuyManager(UserRepository userRepository,
                      ProductRepository productRepository,
                      CategoryRepository categoryRepository,
                      MethodFactory methodFactory,
                      KeyboardFactory keyboardFactory) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
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
        String[] splitCallbackData = callbackQuery.getData().split("_");

        log.info(callbackQuery.getData());
        if (splitCallbackData.length == 3 && CATEGORY.equals(splitCallbackData[2])) {
            currentPage = 1;
            return chooseCategory(callbackQuery, bot);

        }
        if (splitCallbackData.length == 6 && CATEGORY.equals(splitCallbackData[2])) {
            return randomProduct(callbackQuery, bot, splitCallbackData[4]);
        }
        if (splitCallbackData.length == 4 && CATEGORY.equals(splitCallbackData[2])) {
            switch (splitCallbackData[3]) {
                case NEXT -> {
                    return nextPage(callbackQuery);
                }
                case PREV -> {
                    return prevPage(callbackQuery);
                }
            }
        }
        if (splitCallbackData.length == 5 && PRODUCT.equals(splitCallbackData[2])) {
            switch (splitCallbackData[3]) {
                case SETBOX -> {
                    return setProductInBox(callbackQuery, bot, splitCallbackData[4]);
                }
                case GET -> {
                    return getProductForBuy(callbackQuery, bot, splitCallbackData[4]);
                }
                case INFO -> {
                    return productInfo(callbackQuery, bot, splitCallbackData[4]);
                }
            }
        }
        if (splitCallbackData.length == 5 && CATEGORY.equals(splitCallbackData[2])) {
            switch (splitCallbackData[3]) {
                case INFO -> {
                    return productInfo(callbackQuery, bot, splitCallbackData[4]);
                }
            }
        }
        return null;
    }

    @SneakyThrows
    private BotApiMethod<?> productInfo(CallbackQuery callbackQuery, Bot bot, String id) {
        if(callbackQuery.getMessage().getReplyToMessage() != null && callbackQuery.getMessage().getReplyToMessage().getReplyToMessage() != null){
            bot.execute(methodFactory.getDeleteMessage(
                    callbackQuery.getMessage().getChatId(),
                    callbackQuery.getMessage().getMessageId()-2
            ));
        }
        var user = userRepository.findUserByChatId(callbackQuery.getMessage().getChatId());
        String categoryName = user.getSelectedCategory();
        var product = productRepository.findById(Long.valueOf(id)).orElseThrow();
        var productNext = productRepository.findRandomProductByCategory(categoryName);

        log.info(categoryName);

        if (productNext != null) {
            InputFile inputMedia = new InputFile();
            inputMedia.setMedia(product.getPhotoId());
            SendPhoto sendPhoto = methodFactory.getSendPhoto(
                    callbackQuery.getMessage().getChatId(),
                    "Товар №" + product.getProductId(),
                    inputMedia);
            bot.execute(methodFactory.getDeleteMessage(
                    callbackQuery.getMessage().getChatId(),
                    callbackQuery.getMessage().getMessageId()
            ));
            bot.execute(sendPhoto);


            String text = "\uD83C\uDFF7\uFE0F Номер товара: " + product.getProductId() + "\n\n" +
                    "\uD83D\uDCCC Название: " + product.getTitle() + "\n\n" +
                    "\uD83D\uDCB5 Цена: " + product.getPrice() + "\n" +
                    "\uD83D\uDDD2 Описание: \n" + product.getDescription() + "\n\n" +
                    "\uD83D\uDCA1 Категория: " + product.getNameCategory() + "\n\n";

            return methodFactory.getSendMessage(
                    callbackQuery.getMessage().getChatId(),
                    text,
                    keyboardFactory.getInlineKeyboard(
                            List.of("Купить \uD83D\uDCB0", "В корзину 🛒", "↩️ Назад", "➡️"),
                            List.of(2, 2),
                            List.of(BUY_CHOOSE_PRODUCT_GET + product.getProductId(),
                                    BUY_CHOOSE_PRODUCT_SETBOX + product.getProductId(),
                                    GO,
                                    BUY_CHOOSE_CATEGORY_INFO + productNext.getProductId())
                    )
            );
        }
        return null;
    }

    @SneakyThrows
    private BotApiMethod<?> getProductForBuy(CallbackQuery callbackQuery, Bot bot, String id) {

        bot.execute(methodFactory.getDeleteMessage(
                callbackQuery.getMessage().getChatId(),
                callbackQuery.getMessage().getMessageId() - 1
        ));

        var product = productRepository.findById(Long.valueOf(id)).orElseThrow();
        String text = "\uD83C\uDFF7 Номер товара: " + product.getProductId() + "\n\n" +
                "\uD83D\uDCF1 Контакты продавца: @" + product.getUser().getUsername();

        return methodFactory.getEditMessageText(
                callbackQuery,
                text,
                keyboardFactory.getInlineKeyboard(
                        List.of("↩️ Вернуться к товару"),
                        List.of(1),
                        List.of(BUY_CHOOSE_PRODUCT_INFO + id)
                )
        );
    }

    @SneakyThrows
    private BotApiMethod<?> setProductInBox(CallbackQuery callbackQuery, Bot bot, String id) {
        bot.execute(methodFactory.getDeleteMessage(
                callbackQuery.getMessage().getChatId(),
                callbackQuery.getMessage().getMessageId()-1
        ));
        var user = userRepository.findUserByChatId(callbackQuery.getMessage().getChatId());
        var product = productRepository.findById(Long.valueOf(id)).orElseThrow();
        if(!user.getProducts().contains(product)){
            user.addProduct(product);
            userRepository.save(user);
            return methodFactory.getEditMessageText(
                    callbackQuery,
                    "Товар добавлен в коризну ✅",
                    keyboardFactory.getInlineKeyboard(
                            List.of("↩️ Вернуться к товару"),
                            List.of(1),
                            List.of(BUY_CHOOSE_PRODUCT_INFO + id)
                    )

            );
        }

        return methodFactory.getEditMessageText(
                callbackQuery,
                "Товар уже есть коризине ⚠️",
                keyboardFactory.getInlineKeyboard(
                        List.of("↩️ Вернуться к товару"),
                        List.of(1),
                        List.of(BUY_CHOOSE_PRODUCT_INFO + id)
                )

        );
    }

    private BotApiMethod<?> prevPage(CallbackQuery callbackQuery) {
        currentPage--;
        return methodFactory.getEditMessageText(
                callbackQuery,
                """
                        Выберите категорию товара \uD83D\uDCE9\s
                        """,
                keyboardFactory.createCategoriesKeyboard(
                        categoryRepository.findAll(),
                        currentPage,
                        List.of(BUY_CHOOSE_CATEGORY_INFO,
                                BUY_CHOOSE_CATEGORY_NEXT,
                                BUY_CHOOSE_CATEGORY_PREV,
                                GO)
                )
        );
    }

    private BotApiMethod<?> nextPage(CallbackQuery callbackQuery) {
        currentPage++;
        return methodFactory.getEditMessageText(
                callbackQuery,
                """
                        Выберите категорию товара \uD83D\uDCE9\s
                        """,
                keyboardFactory.createCategoriesKeyboard(
                        categoryRepository.findAll(),
                        currentPage,
                        List.of(BUY_CHOOSE_CATEGORY_INFO,
                                BUY_CHOOSE_CATEGORY_NEXT,
                                BUY_CHOOSE_CATEGORY_PREV,
                                GO)
                )
        );
    }

    @SneakyThrows
    private BotApiMethod<?> randomProduct(CallbackQuery callbackQuery, Bot bot, String categoryId) {
        var category = categoryRepository.findById(Long.valueOf(categoryId)).orElseThrow();
        var product = productRepository.findRandomProductByCategory(category.getName());
        var productNext = productRepository.findRandomProductByCategory(category.getName());
        var user = userRepository.findUserByChatId(callbackQuery.getMessage().getChatId());
        log.info(category.getName());
        if (product != null) {
            user.setSelectedCategory(category.getName());
            userRepository.save(user);
            InputFile inputMedia = new InputFile();
            inputMedia.setMedia(product.getPhotoId());
            SendPhoto sendPhoto = methodFactory.getSendPhoto(
                    callbackQuery.getMessage().getChatId(),
                    "Товар №" + product.getProductId(),
                    inputMedia);

            bot.execute(methodFactory.getDeleteMessage(
                    callbackQuery.getMessage().getChatId(),
                    callbackQuery.getMessage().getMessageId()
            ));
            bot.execute(sendPhoto);


            String text = "\uD83C\uDFF7\uFE0F Номер товара: " + product.getProductId() + "\n\n" +
                    "\uD83D\uDCCC Название: " + product.getTitle() + "\n\n" +
                    "\uD83D\uDCB5 Цена: " + product.getPrice() + "\n" +
                    "\uD83D\uDDD2 Описание: \n" + product.getDescription() + "\n\n" +
                    "\uD83D\uDCA1 Категория: " + product.getNameCategory() + "\n\n";

            return methodFactory.getSendMessage(
                    callbackQuery.getMessage().getChatId(),
                    text,
                    keyboardFactory.getInlineKeyboard(
                            List.of("Купить \uD83D\uDCB0", "В корзину 🛒", "↩️ Назад", "➡️"),
                            List.of(2, 2),
                            List.of(BUY_CHOOSE_PRODUCT_GET + product.getProductId(),
                                    BUY_CHOOSE_PRODUCT_SETBOX + product.getProductId(),
                                    GO,
                                    BUY_CHOOSE_CATEGORY_INFO + productNext.getProductId())
                    )
            );
        }
        return methodFactory.getEditMessageText(
                callbackQuery,

                "К сожалению, товаров этой категории нет \uD83D\uDE22\n\n" +
                        "Но вы можете стать первым!\n\n Просто нажмите Начать покупки -> Продать",
                keyboardFactory.getInlineKeyboard(
                        List.of("В меню", "Вернуться к выбору"),
                        List.of(2),
                        List.of(MENU, BUY_CHOOSE_CATEGORY)
                )
        );

    }


    private BotApiMethod<?> chooseCategory(CallbackQuery callbackQuery, Bot bot) {
        var categories = categoryRepository.findAll();
        return methodFactory.getEditMessageText(
                callbackQuery,
                "Выберите категорию товара \uD83D\uDCE9 ",
                keyboardFactory.createCategoriesKeyboard(
                        categories,
                        currentPage,
                        List.of(BUY_CHOOSE_CATEGORY_INFO,
                                BUY_CHOOSE_CATEGORY_NEXT,
                                BUY_CHOOSE_CATEGORY_PREV,
                                GO)

                )
        );
    }
}
