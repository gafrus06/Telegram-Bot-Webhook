package com.gafbank.rashop.service.handlers;

import com.gafbank.rashop.Repository.UserRepository;
import com.gafbank.rashop.service.factory.MethodFactory;
import com.gafbank.rashop.service.managers.go.GoManager;
import com.gafbank.rashop.telegram.Bot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class MessageHandler {
    private final GoManager goManager;
    private final UserRepository userRepository;
    private final MethodFactory methodFactory;

    @Autowired
    public MessageHandler(GoManager goManager, UserRepository userRepository, MethodFactory methodFactory) {
        this.goManager = goManager;
        this.userRepository = userRepository;
        this.methodFactory = methodFactory;
    }

    public BotApiMethod<?> answer(Message message, Bot bot){
        var user = userRepository.findUserByChatId(message.getChatId());
        switch (user.getAction()){
            case SENDING_MEDIA, SENDING_DESCRIPTION,
                    SENDING_PRICE, SENDING_TITLE -> {
                return goManager.answerMessage(message, bot);
            }
            case FREE, IN_CREATING, SENDING_CATEGORY-> {
                return methodFactory.getDeleteMessage(
                        message.getChatId(),
                        message.getMessageId()
                );
            }
        }
        return null;
    }
}
