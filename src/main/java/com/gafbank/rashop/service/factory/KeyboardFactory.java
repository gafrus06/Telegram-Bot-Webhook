package com.gafbank.rashop.service.factory;

import com.gafbank.rashop.entity.Category;
import com.gafbank.rashop.entity.Product;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class KeyboardFactory {
    public InlineKeyboardMarkup getInlineKeyboard(
            List<String> text,
            List<Integer> cfg,
            List<String> data
    ){
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        int index = 0;
        for(Integer rowNumber : cfg){
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int i = 0; i < rowNumber; i++) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(text.get(index));
                button.setCallbackData(data.get(index));
                row.add(button);
                index++;
            }
            keyboard.add(row);
        }
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }
    public InlineKeyboardMarkup createProductsKeyboard(List<Product> products, int currentPage, List<String> constants) {
        InlineKeyboardMarkup.InlineKeyboardMarkupBuilder keyboardBuilder = InlineKeyboardMarkup.builder();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        int pageSize = 8;
        int totalPages = (int) Math.ceil((double) products.size() / pageSize);
        // Определение индекса первого товара на текущей странице
        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = Math.min(currentPage * pageSize, products.size());

        // Добавление товаров на текущей странице
        for (int i = startIndex; i < endIndex; i++) {
            Product product = products.get(i);
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .text(product.getTitle())
                    .callbackData(constants.get(0) + product.getProductId() + "_" + currentPage)
                    .build();
            keyboard.add(Collections.singletonList(button));
        }

        // Добавление кнопок переключения страниц
        List<InlineKeyboardButton> navigationRow = new ArrayList<>();
        if (currentPage > 1) {
            InlineKeyboardButton prevButton = InlineKeyboardButton.builder()
                    .text("◀️ Prev")
                    .callbackData(constants.get(2))
                    .build();
            navigationRow.add(prevButton);
        }
        if (currentPage < totalPages) {
            InlineKeyboardButton nextButton = InlineKeyboardButton.builder()
                    .text("Next ▶️")
                    .callbackData(constants.get(1))
                    .build();
            navigationRow.add(nextButton);
        }
        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton backButton = InlineKeyboardButton.builder()
                .text("↩️ Вернуться")
                .callbackData(constants.get(3))
                .build();

        backRow.add(backButton);
        keyboard.add(navigationRow);
        keyboard.add(backRow);

        keyboardBuilder.keyboard(keyboard);
        return keyboardBuilder.build();
    }
    public InlineKeyboardMarkup createCategoriesKeyboard(List<Category> categories, int currentPage, List<String> constants) {
        InlineKeyboardMarkup.InlineKeyboardMarkupBuilder keyboardBuilder = InlineKeyboardMarkup.builder();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        int pageSize = 7;
        int totalPages = (int) Math.ceil((double) categories.size() / pageSize);
        // Определение индекса первого товара на текущей странице
        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = Math.min(currentPage * pageSize, categories.size());

        // Добавление товаров на текущей странице
        for (int i = startIndex; i < endIndex; i++) {
            Category category = categories.get(i);
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .text(category.getName())
                    .callbackData(constants.get(0) + category.getCategoryId() + "_" + currentPage)
                    .build();
            keyboard.add(Collections.singletonList(button));
        }

        // Добавление кнопок переключения страниц
        List<InlineKeyboardButton> navigationRow = new ArrayList<>();
        if (currentPage > 1) {
            InlineKeyboardButton prevButton = InlineKeyboardButton.builder()
                    .text("⬅️")
                    .callbackData(constants.get(2))
                    .build();
            navigationRow.add(prevButton);
        }
        if (currentPage < totalPages) {
            InlineKeyboardButton nextButton = InlineKeyboardButton.builder()
                    .text("➡️")
                    .callbackData(constants.get(1))
                    .build();
            navigationRow.add(nextButton);
        }
        List<InlineKeyboardButton> backRow = new ArrayList<>();
        InlineKeyboardButton backButton = InlineKeyboardButton.builder()
                .text("↩️ Назад")
                .callbackData(constants.get(3))
                .build();

        backRow.add(backButton);
        keyboard.add(navigationRow);
        keyboard.add(backRow);

        keyboardBuilder.keyboard(keyboard);
        return keyboardBuilder.build();
    }
    public ReplyKeyboardMarkup getReplyKeyboard(List<String> text,
                                                List<Integer> configuration){
        List<KeyboardRow> keyboard = new ArrayList<>();
        int index = 0;
        for(Integer rowNumber : configuration){
            KeyboardRow row = new KeyboardRow();

            for(int i = 0; i < rowNumber; i++){
                KeyboardButton button = new KeyboardButton();
                button.setText(text.get(index));
                row.add(button);

                index += 1;
            }
            keyboard.add(row);
        }
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

}
