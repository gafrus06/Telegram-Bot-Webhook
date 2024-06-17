package com.gafbank.rashop.service.handlers;

import com.gafbank.rashop.Repository.UserRepository;
import com.gafbank.rashop.entity.Action;
import com.gafbank.rashop.service.factory.MethodFactory;
import com.gafbank.rashop.service.managers.box.BoxManager;
import com.gafbank.rashop.service.managers.feedback.FeedbackManager;
import com.gafbank.rashop.service.managers.go.GoManager;
import com.gafbank.rashop.service.managers.help.HelpManager;
import com.gafbank.rashop.service.managers.menu.MenuManager;
import com.gafbank.rashop.service.managers.productInfo.ProductInfoManager;
import com.gafbank.rashop.service.managers.start.StartManager;
import com.gafbank.rashop.telegram.Bot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import static com.gafbank.rashop.service.data.Commands.*;

@Component
public class CommandHanlder {
    private final StartManager startManager;
    private final HelpManager helpManager;
    private final FeedbackManager feedbackManager;
    private final MenuManager menuManager;
    private final ProductInfoManager productInfoManager;
    private final BoxManager boxManager;
    private final GoManager goManager;
    private final UserRepository userRepository;
    private final MethodFactory methodFactory;

    @Autowired
    public CommandHanlder(StartManager startManager,
                          HelpManager helpManager,
                          FeedbackManager feedbackManager,
                          MenuManager menuManager, ProductInfoManager productInfoManager, BoxManager boxManager, GoManager goManager, UserRepository userRepository, MethodFactory methodFactory) {
        this.startManager = startManager;
        this.helpManager = helpManager;
        this.feedbackManager = feedbackManager;
        this.menuManager = menuManager;
        this.productInfoManager = productInfoManager;
        this.boxManager = boxManager;
        this.goManager = goManager;
        this.userRepository = userRepository;
        this.methodFactory = methodFactory;
    }

    public BotApiMethod<?> answer(Message message, Bot bot) {
        var user = userRepository.findUserByChatId(message.getChatId());
        if(user.getAction().equals(Action.FREE)){
            String command = message.getText();
            switch (command) {
                case START_COMMAND -> {
                    return startManager.answerCommand(message, bot);
                }
                case HELP_COMMAND -> {
                    return helpManager.answerCommand(message, bot);
                }
                case MENU_COMMAND -> {
                    return menuManager.answerCommand(message, bot);
                }
                case FEEDBACK_COMMAND -> {
                    return feedbackManager.answerCommand(message, bot);
                }
                case INFO_COMMAND -> {
                    return productInfoManager.answerCommand(message, bot);
                }
                case BOX_COMMAND -> {
                    return boxManager.answerCommand(message, bot);
                }
                case GO_COMMAND -> {
                    return goManager.answerCommand(message, bot);
                }

                default -> {
                    return defaultAnswer(message);
                }

            }
        }
        else return methodFactory.getDeleteMessage(
                message.getChatId(),
                message.getMessageId()
        );

    }

    private BotApiMethod<?> defaultAnswer(Message message) {
        return SendMessage.builder()
                .chatId(message.getChatId())
                .text("Неподдерживаемая команда")
                .build();
    }
}
