package com.gafbank.rashop.service.managers.go;

import com.gafbank.rashop.Repository.CategoryRepository;
import com.gafbank.rashop.Repository.ProductRepository;
import com.gafbank.rashop.Repository.UserRepository;
import com.gafbank.rashop.entity.Action;
import com.gafbank.rashop.entity.Product;
import com.gafbank.rashop.entity.User;
import com.gafbank.rashop.service.factory.KeyboardFactory;
import com.gafbank.rashop.service.factory.MethodFactory;
import com.gafbank.rashop.service.managers.AbstractManager;
import com.gafbank.rashop.telegram.Bot;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

import static com.gafbank.rashop.entity.Action.*;
import static com.gafbank.rashop.service.data.CallbackData.*;

@Component
@Slf4j
public class GoManager extends AbstractManager {
    private final MethodFactory methodFactory;
    private final KeyboardFactory keyboardFactory;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private int currentPage = 1;
    private int currentPageCategory = 1;
    private final CategoryRepository categoryRepository;

    public GoManager(MethodFactory methodFactory, KeyboardFactory keyboardFactory, ProductRepository productRepository, UserRepository userRepository, CategoryRepository categoryRepository) {
        this.methodFactory = methodFactory;
        this.keyboardFactory = keyboardFactory;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public BotApiMethod<?> answerCommand(Message message, Bot bot) {
        var user = userRepository.findUserByChatId(message.getChatId());
        user.setAction(Action.FREE);
        userRepository.save(user);
        return mainMenu(message);
    }


    @Override
    public BotApiMethod<?> answerMessage(Message message, Bot bot) {
        try {
            bot.execute(methodFactory.getDeleteMessage(
                    message.getChatId(),
                    message.getMessageId() - 1
            ));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        var user = userRepository.findUserByChatId(message.getChatId());
        switch (user.getAction()) {
            case SENDING_MEDIA -> {
                return setMedia(message, user, bot);
            }
            case SENDING_TITLE -> {
                return setTitle(message, user, bot);
            }
            case SENDING_PRICE -> {
                return setPrice(message, user, bot);
            }
            case SENDING_DESCRIPTION -> {
                return setDesc(message, user, bot);
            }
        }
        return null;

    }


    @Override
    public BotApiMethod<?> answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) {
        String[] splitCallbackData = callbackQuery.getData().split("_");
        var user = userRepository.findUserByChatId(callbackQuery.getMessage().getChatId());
        log.info(callbackQuery.getData());
        if (GO.equals(callbackQuery.getData())) {
            return mainMenu(callbackQuery, bot);
        }
        if (splitCallbackData.length == 6) {
            if (SELL.equals(splitCallbackData[1])) {
                log.info("SELL");
                log.info(callbackQuery.getData());
                if (SEND.equals(splitCallbackData[3])) {
                    return setCategory(callbackQuery, user, bot, splitCallbackData[4]);
                }
            }
        }

        if (splitCallbackData.length == 4) {
            if (BACK.equals(splitCallbackData[3])) {
                return back(callbackQuery.getMessage(), bot);
            }
        }
        if (splitCallbackData.length == 3) {
            if (SELL.equals(splitCallbackData[1])) {
                switch (splitCallbackData[2]) {
                    case NEXT -> {
                        return nextPageCategory(callbackQuery, bot);
                    }
                    case PREV -> {
                        return prevPageCategory(callbackQuery, bot);
                    }
                }
            }
            if (MY.equals(splitCallbackData[1])) {
                switch (splitCallbackData[2]) {
                    case NEXT -> {
                        return nextPage(callbackQuery);
                    }
                    case PREV -> {
                        return prevPage(callbackQuery);

                    }

                }
            }

        }

        if (splitCallbackData.length == 2) {
            switch (splitCallbackData[1]) {
                case SELL -> {
                    currentPageCategory = 1;
                    return addProductForSell(callbackQuery);
                }
                case MY -> {
                    currentPage = 1;
                    return myProduct(callbackQuery, bot);
                }

            }
        } else {
            switch (splitCallbackData[2]) {
                case PHOTO -> {
                    log.info(splitCallbackData[2]);
                    return addPhoto(callbackQuery);
                }
                case NAME -> {
                    return addName(callbackQuery);
                }
                case PRICE -> {
                    return addPrice(callbackQuery);
                }
                case DES -> {
                    return addDesc(callbackQuery);
                }
                case CATEGORY -> {
                    return addCategory(callbackQuery);
                }
                case CANCEL -> {
                    return cancel(callbackQuery, bot);
                }
                case FINISH -> {
                    return finish(callbackQuery, bot);
                }
                case PRODUCT -> {
                    return productInfo(callbackQuery, bot, splitCallbackData[3]);
                }
                case DELETE -> {
                    return deleteProduct(callbackQuery, bot, splitCallbackData[3]);
                }
            }
        }

        return null;
    }

    private BotApiMethod<?> nextPageCategory(CallbackQuery callbackQuery, Bot bot) {
        currentPageCategory++;
        return methodFactory.getEditMessageText(
                callbackQuery,
                """
                        Выберите категорию товара \uD83D\uDCE9\s
                        """,
                keyboardFactory.createCategoriesKeyboard(
                        categoryRepository.findAll(),
                        currentPageCategory,
                        List.of(GO_SELL_CATEGORY_SEND, GO_SELL_NEXT, GO_SELL_PREV, GO_SELL_PHOTO_BACK)
                )
        );
    }

    private BotApiMethod<?> prevPageCategory(CallbackQuery callbackQuery, Bot bot) {
        currentPageCategory--;
        return methodFactory.getEditMessageText(
                callbackQuery,
                """
                        Выберите категорию товара \uD83D\uDCE9\s
                        """,
                keyboardFactory.createCategoriesKeyboard(
                        categoryRepository.findAll(),
                        currentPageCategory,
                        List.of(GO_SELL_CATEGORY_SEND, GO_SELL_NEXT, GO_SELL_PREV, GO_SELL_PHOTO_BACK)
                )
        );
    }

    private BotApiMethod<?> deleteProduct(CallbackQuery callbackQuery, Bot bot, String id) {
        var productId = Long.valueOf(id);
        var product = productRepository.findById(productId).orElseThrow();
        var user = userRepository.findUserByChatId(callbackQuery.getMessage().getChatId());
        user.getSellProducts().remove(product);
        user.getProducts().remove(product);
        productRepository.delete(product);
        try {
            bot.execute(methodFactory.getDeleteMessage(
                    callbackQuery.getMessage().getChatId(),
                    callbackQuery.getMessage().getMessageId() - 1
            ));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        userRepository.save(user);
        return methodFactory.getEditMessageText(
                callbackQuery,
                "\uD83D\uDEAB Товар удален",
                keyboardFactory.getInlineKeyboard(
                        List.of("↩\uFE0F Вернуться"),
                        List.of(1),
                        List.of(GO_MY)
                )

        );

    }

    private BotApiMethod<?> productInfo(CallbackQuery callbackQuery, Bot bot, String id) {
        var product = productRepository.findById(Long.valueOf(id)).orElseThrow();
        String text = "\uD83C\uDFF7\uFE0F Номер товара: " + product.getProductId() + "\n\n" +
                "\uD83D\uDCCC Название: " + product.getTitle() + "\n\n" +
                "\uD83D\uDCB5 Цена: " + product.getPrice() + "\n" +
                "\uD83D\uDDD2 Описание: \n" + product.getDescription() + "\n\n" +
                "🛍 Категория: " + product.getNameCategory() + "\n\n";


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
                        List.of("Удалить \uD83D\uDEAB",
                                "Назад ↩️\uFE0F"),
                        List.of(2),
                        List.of(GO_MY_DELETE + product.getProductId(),
                                GO_MY)
                ))
                .build();
        try {
            bot.execute(methodFactory.getDeleteMessage(
                    callbackQuery.getMessage().getChatId(),
                    callbackQuery.getMessage().getMessageId()
            ));

        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        try {
            bot.execute(sendPhoto);
            return sendMessage;
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    private BotApiMethod<?> prevPage(CallbackQuery callbackQuery) {
        currentPage--;
        return methodFactory.getEditMessageText(
                callbackQuery,
                "\uD83D\uDCE6 Ваши товары" + "\n\n" +
                        "\uD83D\uDCC4 Страница " + currentPage,
                keyboardFactory.createProductsKeyboard(
                        userRepository.findUserByChatId(
                                callbackQuery.getMessage().getChatId()).getSellProducts(),
                        currentPage,
                        List.of(GO_MY_PRODUCT, GO_MY_NEXT, GO_MY_PREV, GO)
                )
        );
    }

    private BotApiMethod<?> nextPage(CallbackQuery callbackQuery) {
        currentPage++;
        return methodFactory.getEditMessageText(
                callbackQuery,
                "\uD83D\uDCE6 Ваши товары" + "\n\n" +
                        "\uD83D\uDCC4 Страница " + currentPage,
                keyboardFactory.createProductsKeyboard(
                        userRepository.findUserByChatId(
                                callbackQuery.getMessage().getChatId()).getSellProducts(),
                        currentPage,
                        List.of(GO_MY_PRODUCT, GO_MY_NEXT, GO_MY_PREV, GO)
                )
        );
    }


    private BotApiMethod<?> finish(CallbackQuery callbackQuery, Bot bot) {
        var user = userRepository.findUserByChatId(callbackQuery.getMessage().getChatId());
        var product = productRepository.findProductByUserAndIsInCreating(user, true);
        if (product != null) {
            if (product.getTitle() != null && product.getDescription() != null &&
                    product.getPhotoId() != null && product.getNameCategory() != null &&
                    product.getPrice() != null) {
                product.setIsInCreating(false);
                user.setAction(FREE);
                userRepository.save(user);
                productRepository.save(product);
                return (methodFactory.getEditMessageText(
                        callbackQuery,
                        "Товар успешно создан ✅",
                        keyboardFactory.getInlineKeyboard(
                                List.of("Вернуться в меню ↩️"),
                                List.of(1),
                                List.of(GO)
                        )
                ));
            }
            return methodFactory.getEditMessageText(
                    callbackQuery,
                    "❌ Не все параметры заполнены!",
                    keyboardFactory.getInlineKeyboard(
                            List.of("Вернуться к созданию ↩️"),
                            List.of(1),
                            List.of(GO_SELL_PHOTO_BACK)
                    )
            );
        }
        return null;

    }

    private BotApiMethod<?> setDesc(Message message, User user, Bot bot) {
        try {
            bot.execute(methodFactory.getDeleteMessage(
                    message.getChatId(),
                    message.getMessageId() - 1
            ));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        Product product = productRepository.findProductByUserAndIsInCreating(user, true);
        if (product.getIsInCreating() && message.hasText()) {
            product.setDescription(message.getText());
            productRepository.save(product);
            return back(message, bot);
        }
        return null;
    }

    private BotApiMethod<?> setPrice(Message message, User user, Bot bot) {
        Product product = productRepository.findProductByUserAndIsInCreating(user, true);
        if (product.getIsInCreating() && message.hasText()) {
            if (isLong(message.getText())) {
                try {
                    bot.execute(methodFactory.getDeleteMessage(
                            message.getChatId(),
                            message.getMessageId() - 1
                    ));
                } catch (TelegramApiException e) {
                    log.error(e.getMessage());
                }
                product.setPrice(Long.valueOf(message.getText()));
                productRepository.save(product);
                return back(message, bot);
            } else {
                return methodFactory.getSendMessage(
                        message.getChatId(),
                        "⚠️ Это не число!\nПопробуйте снова",
                        null
                );
            }
        }
        return null;
    }

    private boolean isLong(String input) {
        try {
            Long.parseLong(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private BotApiMethod<?> setCategory(CallbackQuery callbackQuery, User user, Bot bot, String id) {
        var category = categoryRepository.findById(Long.valueOf(id)).orElseThrow();
        log.info(category.getName());
        Product product = productRepository.findProductByUserAndIsInCreating(user, true);
        if (product.getIsInCreating()) {
            product.setNameCategory(category.getName());
            user.setAction(IN_CREATING);
            userRepository.save(user);
            productRepository.save(product);
            return back(callbackQuery, bot);
        }
        return null;
    }

    private BotApiMethod<?> back(CallbackQuery callbackQuery, Bot bot) {
        var user = userRepository.findUserByChatId(callbackQuery.getMessage().getChatId());
        var product = productRepository.findProductByUserAndIsInCreating(user, true);

        user.setAction(IN_CREATING);
        userRepository.save(user);
        return methodFactory.getEditMessageText(
                callbackQuery,
                "Заполните все параметры товара \uD83D\uDCDD \n\n" +
                        "Нажмите \"Создать\", когда будут заполнены все параметры\n" +
                        "Чтобы отменить создание и вернуться в меню нажмите \"Отмена создания\"",
                keyboardFactory.getInlineKeyboard(
                        List.of(
                                "Фото \uD83D\uDDBC" + (product.getPhotoId() != null ? " ➕" : " ➖"),
                                "Название ✏️" + (product.getTitle() != null ? " ➕" : " ➖"),
                                "Цена \uD83D\uDCB5" + (product.getPrice() != null ? " ➕" : " ➖"),
                                "Описание \uD83D\uDCDC" + (product.getDescription() != null ? " ➕" : " ➖"),
                                "Категория товара \uD83D\uDECD" + (product.getNameCategory() != null ? " ➕" : " ➖"), "Создать ✅", "Отмена создания ❌"
                        ),
                        List.of(2, 2, 1, 1, 1),
                        List.of(GO_SELL_PHOTO, GO_SELL_NAME,
                                GO_SELL_PRICE, GO_SELL_DES,
                                GO_SELL_CATEGORY, GO_SELL_FINISH,
                                GO_SELL_CANCEL)
                )
        );
    }

    private BotApiMethod<?> setTitle(Message message, User user, Bot bot) {
        try {
            bot.execute(methodFactory.getDeleteMessage(
                    message.getChatId(),
                    message.getMessageId() - 1
            ));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        Product product = productRepository.findProductByUserAndIsInCreating(user, true);
        if (product.getIsInCreating() && message.hasText()) {
            product.setTitle(message.getText());
            productRepository.save(product);
            return back(message, bot);
        }
        return null;
    }

    private BotApiMethod<?> setMedia(Message message, User user, Bot bot) {

        log.info("set");
        Product product = productRepository.findProductByUserAndIsInCreating(user, true);
        if (product.getIsInCreating() && message.hasPhoto()) {
            try {
                bot.execute(methodFactory.getDeleteMessage(
                        message.getChatId(),
                        message.getMessageId() - 1
                ));
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
            List<PhotoSize> photos = message.getPhoto();
            String f_id = Objects
                    .requireNonNull(photos
                            .stream()
                            .max(Comparator.comparing(PhotoSize::getFileSize))
                            .orElse(null))
                    .getFileId();
            log.info(f_id);
            product.setPhotoId(f_id);
            productRepository.save(product);

            return back(message, bot);
        }
        return methodFactory.getSendMessage(
                message.getChatId(),
                "⚠️ Это не фотография\nПопробуйте снова",
                null
        );
    }

    private BotApiMethod<?> back(Message message, Bot bot) {
        var user = userRepository.findUserByChatId(message.getChatId());
        var product = productRepository.findProductByUserAndIsInCreating(user, true);
        if (!user.getAction().equals(FREE)) {
            if (product.getPhotoId() != null && product.getTitle() != null
                    && product.getDescription() != null && product.getPrice() != null &&
                    product.getNameCategory() != null) {
                try {
                    bot.execute(methodFactory.getDeleteMessage(
                            message.getChatId(),
                            message.getMessageId() - 1
                    ));
                    bot.execute(methodFactory.getDeleteMessage(
                            message.getChatId(),
                            message.getMessageId()
                    ));
                } catch (TelegramApiException e) {
                    log.error(e.getMessage());
                }
            } else {
                try {
                    bot.execute(methodFactory.getDeleteMessage(
                            message.getChatId(),
                            message.getMessageId()
                    ));
                } catch (TelegramApiException e) {
                    log.error(e.getMessage());
                }
            }

            user.setAction(IN_CREATING);
            userRepository.save(user);
            return methodFactory.getSendMessage(
                    message.getChatId(),
                    "Заполните все параметры товара \uD83D\uDCDD \n\n" +
                            "Нажмите \"Создать\", когда будут заполнены все параметры\n" +
                            "Чтобы отменить создание и вернуться в меню нажмите \"Отмена создания\"",
                    keyboardFactory.getInlineKeyboard(
                            List.of(
                                    "Фото \uD83D\uDDBC" + (product.getPhotoId() != null ? " ➕" : " ➖"),
                                    "Название ✏️" + (product.getTitle() != null ? " ➕" : " ➖"),
                                    "Цена \uD83D\uDCB5" + (product.getPrice() != null ? " ➕" : " ➖"),
                                    "Описание \uD83D\uDCDC" + (product.getDescription() != null ? " ➕" : " ➖"),
                                    "Категория товара \uD83D\uDECD" + (product.getNameCategory() != null ? " ➕" : " ➖"), "Создать ✅", "Отмена создания ❌"
                            ),
                            List.of(2, 2, 1, 1, 1),
                            List.of(GO_SELL_PHOTO, GO_SELL_NAME,
                                    GO_SELL_PRICE, GO_SELL_DES,
                                    GO_SELL_CATEGORY, GO_SELL_FINISH,
                                    GO_SELL_CANCEL)
                    )
            );
        }
        return null;

    }

    private BotApiMethod<?> addDesc(CallbackQuery callbackQuery) {
        log.info(callbackQuery.getData());
        var user = userRepository.findUserByChatId(callbackQuery.getMessage().getChatId());
        user.setAction(SENDING_DESCRIPTION);
        userRepository.save(user);
        return methodFactory.getEditMessageText(
                callbackQuery,
                "Напишите описание товара \uD83D\uDCE9",
                keyboardFactory.getInlineKeyboard(
                        List.of("Назад ↩️"),
                        List.of(1),
                        List.of(GO_SELL_PHOTO_BACK)
                )
        );
    }

    private BotApiMethod<?> addCategory(CallbackQuery callbackQuery) {
        var user = userRepository.findUserByChatId(callbackQuery.getMessage().getChatId());
        user.setAction(SENDING_CATEGORY);
        userRepository.save(user);
        return methodFactory.getEditMessageText(
                callbackQuery,
                """
                        Выберите категорию товара \uD83D\uDCE9\s
                        """,
                /*keyboardFactory.getInlineKeyboard(
                        List.of("Назад ↩️"),
                        List.of(1),
                        List.of(GO_SELL_PHOTO_BACK)
                )*/
                keyboardFactory.createCategoriesKeyboard(
                        categoryRepository.findAll(),
                        currentPageCategory,
                        List.of(GO_SELL_CATEGORY_SEND, GO_SELL_NEXT, GO_SELL_PREV, GO_SELL_PHOTO_BACK)
                )
        );
    }

    private BotApiMethod<?> cancel(CallbackQuery callbackQuery, Bot bot) {
        var user = userRepository.findUserByChatId(callbackQuery.getMessage().getChatId());
        var product = productRepository.findProductByUserAndIsInCreating(user, true);
        user.getSellProducts().remove(product);
        user.setAction(FREE);
        userRepository.save(user);
        productRepository.delete(product);
        return mainMenu(callbackQuery, bot);
    }

    private BotApiMethod<?> addPrice(CallbackQuery callbackQuery) {
        var user = userRepository.findUserByChatId(callbackQuery.getMessage().getChatId());
        user.setAction(SENDING_PRICE);
        userRepository.save(user);
        return methodFactory.getEditMessageText(
                callbackQuery,
                "Отправьте цену за товар \uD83D\uDCE9",
                keyboardFactory.getInlineKeyboard(
                        List.of("Назад ↩️"),
                        List.of(1),
                        List.of(GO_SELL_PHOTO_BACK)
                )
        );
    }

    private BotApiMethod<?> addName(CallbackQuery callbackQuery) {
        var user = userRepository.findUserByChatId(callbackQuery.getMessage().getChatId());
        user.setAction(SENDING_TITLE);
        userRepository.save(user);
        return methodFactory.getEditMessageText(
                callbackQuery,
                "Отправьте название товара \uD83D\uDCE9",
                keyboardFactory.getInlineKeyboard(
                        List.of("Назад ↩️"),
                        List.of(1),
                        List.of(GO_SELL_PHOTO_BACK)
                )
        );
    }

    private BotApiMethod<?> addPhoto(CallbackQuery callbackQuery) {
        log.info(callbackQuery.getData());
        var user = userRepository.findUserByChatId(callbackQuery.getMessage().getChatId());
        user.setAction(SENDING_MEDIA);
        userRepository.save(user);
        return methodFactory.getEditMessageText(
                callbackQuery,
                "Отправьте фотографию товара \uD83D\uDCE9",
                keyboardFactory.getInlineKeyboard(
                        List.of("Назад ↩️"),
                        List.of(1),
                        List.of(GO_SELL_PHOTO_BACK)
                )
        );
    }


    private BotApiMethod<?> mainMenu(Message message) {
        return methodFactory.getSendMessage(
                message.getChatId(),
                """
                        Хотите перейти к покупкам 🛍 или что-то продать📦?
                        """,
                keyboardFactory.getInlineKeyboard(
                        List.of("К покупам 🛍", "Мои товары \uD83D\uDCA1", "Продать 📦", "В меню ↩️"),
                        List.of(1, 2, 1),
                        List.of(BUY_CHOOSE_CATEGORY, GO_MY, GO_SELL, MENU)
                )
        );
    }

    @SneakyThrows
    private BotApiMethod<?> mainMenu(CallbackQuery callbackQuery, Bot bot) {
        return methodFactory.getEditMessageText(
                callbackQuery,
                """
                        Хотите перейти к покупкам 🛍 или что-то продать📦?
                        """,
                keyboardFactory.getInlineKeyboard(
                        List.of("К покупам 🛍", "Мои товары \uD83D\uDCA1", "Продать 📦", "Вернуться в меню ↩️"),
                        List.of(1, 2, 1),
                        List.of(BUY_CHOOSE_CATEGORY, GO_MY, GO_SELL, MENU)
                )
        );
    }

    private BotApiMethod<?> addProductForSell(CallbackQuery callbackQuery) {
        var user = userRepository.findUserByChatId(callbackQuery.getMessage().getChatId());
        user.setAction(IN_CREATING);
        Product product = new Product();
        product.setUser(user);
        user.addSellProduct(product);
        product.setIsInCreating(true);
        productRepository.save(product);
        userRepository.save(user);

        return methodFactory.getEditMessageText(
                callbackQuery,
                "Заполните все параметры товара \uD83D\uDCDD \n\n" +
                        "Нажмите \"Создать\", когда будут заполнены все параметры\n" +
                        "Чтобы отменить создание и вернуться в меню нажмите \"Отмена создания\"",
                keyboardFactory.getInlineKeyboard(
                        List.of(
                                "Фото \uD83D\uDDBC" + (product.getPhotoId() != null ? " ➕" : " ➖"),
                                "Название ✏️" + (product.getTitle() != null ? " ➕" : " ➖"),
                                "Цена \uD83D\uDCB5" + (product.getPrice() != null ? " ➕" : " ➖"),
                                "Описание \uD83D\uDCDC" + (product.getDescription() != null ? " ➕" : " ➖"),
                                "Категория товара \uD83D\uDECD" + (product.getNameCategory() != null ? " ➕" : " ➖"), "Создать ✅", "Отмена создания ❌"
                        ),
                        List.of(2, 2, 1, 1, 1),
                        List.of(GO_SELL_PHOTO, GO_SELL_NAME,
                                GO_SELL_PRICE, GO_SELL_DES,
                                GO_SELL_CATEGORY, GO_SELL_FINISH,
                                GO_SELL_CANCEL)
                )
        );

    }


    private BotApiMethod<?> myProduct(CallbackQuery callbackQuery, Bot bot) {
        try {
            bot.execute(methodFactory.getDeleteMessage(
                    callbackQuery.getMessage().getChatId(),
                    callbackQuery.getMessage().getMessageId() - 1
            ));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        return methodFactory.getEditMessageText(
                callbackQuery,
                "\uD83D\uDCE6 Ваши товары" + "\n\n" +
                        "\uD83D\uDCC4 Страница " + currentPage,
                keyboardFactory.createProductsKeyboard(
                        userRepository.findUserByChatId(
                                callbackQuery.getMessage().getChatId()).getSellProducts(),
                        currentPage,
                        List.of(GO_MY_PRODUCT, GO_MY_NEXT, GO_MY_PREV, GO)
                )
        );
    }

}
