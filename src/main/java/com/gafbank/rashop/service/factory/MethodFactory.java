package com.gafbank.rashop.service.factory;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.io.File;

@Component
public class MethodFactory {

    public SendMessage getSendMessage(Long chatId,
                                      String text,
                                      ReplyKeyboard keyboard) {
        return SendMessage.builder()
                .chatId(chatId)
                .replyMarkup(keyboard)
                .text(text)
                .build();
    }
    public EditMessageText getEditMessageText(CallbackQuery callbackQuery,
                                              String text,
                                              InlineKeyboardMarkup keyboard) {
        return EditMessageText.builder()
                .chatId(callbackQuery.getMessage().getChatId())
                .messageId(callbackQuery.getMessage().getMessageId())
                .replyMarkup(keyboard)
                .text(text)
                .build();
    }
    public DeleteMessage getDeleteMessage(Long chatId,
                                          Integer messageId) {
        return DeleteMessage.builder()
                .chatId(chatId)
                .messageId(messageId)
                .build();
    }
    public AnswerCallbackQuery getAnswerCallbackQuery(String  callbackQueryId, String text){
        return AnswerCallbackQuery.builder()
                .callbackQueryId(callbackQueryId)
                .text(text)
                .build();
    }

    public SendPhoto getSendPhoto(Long chatId, String caption, InputFile fId) {
        return SendPhoto.builder()
                .chatId(chatId)
                .photo(fId)
                .caption(caption)
                .build();
    }
    public SendPhoto getSendPhoto(Long chatId, String caption, InputFile fId, InlineKeyboardMarkup inlineKeyboardMarkup) {
        return SendPhoto.builder()
                .chatId(chatId)
                .photo(fId)
                .caption(caption)
                .replyMarkup(inlineKeyboardMarkup)
                .build();
    }
    public EditMessageMedia getEditMessageMedia(CallbackQuery callbackQuery, String caption, String photoUrl) {
        return EditMessageMedia.builder()
                .chatId(callbackQuery.getMessage().getChatId())
                .messageId(callbackQuery.getMessage().getMessageId())
                .media(new InputMediaPhoto(photoUrl))

                .build();
    }
    public EditMessageReplyMarkup getEditMessageReplyMarkup(CallbackQuery callbackQuery,
                                                            InlineKeyboardMarkup inlineKeyboardMarkup) {
        return EditMessageReplyMarkup.builder()
                .chatId(callbackQuery.getMessage().getChatId())
                .messageId(callbackQuery.getMessage().getMessageId())
                .replyMarkup(inlineKeyboardMarkup)
                .build();
    }
    public EditMessageMedia getEditMessageMedia(CallbackQuery callbackQuery,
                                                InputMedia media,
                                                InlineKeyboardMarkup inlineKeyboardMarkup){
        return EditMessageMedia.builder()
                .chatId(callbackQuery.getMessage().getChatId())
                .messageId(callbackQuery.getMessage().getMessageId())
                .media(media)
                .replyMarkup(inlineKeyboardMarkup)
                .build();

    }

}
