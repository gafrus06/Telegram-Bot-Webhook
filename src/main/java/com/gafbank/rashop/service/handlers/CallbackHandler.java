package com.gafbank.rashop.service.handlers;

import com.gafbank.rashop.service.managers.box.BoxManager;
import com.gafbank.rashop.service.managers.buy.BuyManager;
import com.gafbank.rashop.service.managers.feedback.FeedbackManager;
import com.gafbank.rashop.service.managers.go.GoManager;
import com.gafbank.rashop.service.managers.help.HelpManager;
import com.gafbank.rashop.service.managers.menu.MenuManager;
import com.gafbank.rashop.service.managers.productInfo.ProductInfoManager;
import com.gafbank.rashop.service.managers.start.StartManager;
import com.gafbank.rashop.telegram.Bot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static com.gafbank.rashop.service.data.CallbackData.*;

@Component
@Slf4j
public class CallbackHandler {
    private final HelpManager helpManager;
    private final FeedbackManager feedbackManager;
    private final MenuManager menuManager;
    private final GoManager goManager;
    private final BoxManager boxManager;
    private final ProductInfoManager productInfoManager;
    private final BuyManager buyManager;

    @Autowired
    public CallbackHandler(HelpManager helpManager,
                           FeedbackManager feedbackManager,
                           MenuManager menuManager,
                           GoManager goManager,
                           BoxManager boxManager,
                           ProductInfoManager productInfoManager,
                           BuyManager buyManager) {
        this.helpManager = helpManager;
        this.feedbackManager = feedbackManager;
        this.menuManager = menuManager;
        this.goManager = goManager;
        this.boxManager = boxManager;
        this.productInfoManager = productInfoManager;
        this.buyManager = buyManager;
    }

    public BotApiMethod<?> answer(CallbackQuery callbackQuery, Bot bot){
        String callbackData = callbackQuery.getData();
        String keyWord = callbackData.split("_")[0];
        switch (keyWord){
            case HELP -> {
                return helpManager.answerCallbackQuery(callbackQuery, bot);
            }
            case FEEDBACK -> {
                return feedbackManager.answerCallbackQuery(callbackQuery, bot);
            }
            case MENU -> {
                return menuManager.answerCallbackQuery(callbackQuery, bot);
            }
            case GO -> {
                return goManager.answerCallbackQuery(callbackQuery, bot);
            }
            case BOX ->{
                return boxManager.answerCallbackQuery(callbackQuery, bot);
            }
            case PRODUCT -> {
                return productInfoManager.answerCallbackQuery(callbackQuery, bot);
            }
            case BUY -> {
                return buyManager.answerCallbackQuery(callbackQuery, bot);
            }

        }
        return null;
    }
}
