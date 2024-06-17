package com.gafbank.rashop.service;

import com.gafbank.rashop.service.handlers.CallbackHandler;
import com.gafbank.rashop.service.handlers.CommandHanlder;
import com.gafbank.rashop.service.handlers.MessageHandler;
import com.gafbank.rashop.telegram.Bot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@Slf4j
public class UpdateDispatcher {
    private final CallbackHandler callbackHandler;
    private final MessageHandler messageHandler;
    private final CommandHanlder commandHanlder;

    @Autowired
    public UpdateDispatcher(CallbackHandler callbackHandler,
                            MessageHandler messageHandler,
                            CommandHanlder commandHanlder) {
        this.callbackHandler = callbackHandler;
        this.messageHandler = messageHandler;
        this.commandHanlder = commandHanlder;
    }


    public BotApiMethod<?> distribute(Update update, Bot bot){
        if(update.hasCallbackQuery()){
            return callbackHandler.answer(update.getCallbackQuery(), bot);
        }
        if(update.hasMessage()){
            Message message = update.getMessage();
            if(message.hasText()){
                if(message.getText().charAt(0) == '/'){
                    return commandHanlder.answer(message, bot);
                }
            }
            return messageHandler.answer(message, bot);
        }
        log.error("Unsupported update: " + update);
        return null;
    }
}
