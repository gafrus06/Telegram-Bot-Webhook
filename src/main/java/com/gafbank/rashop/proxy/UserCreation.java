package com.gafbank.rashop.proxy;

import com.gafbank.rashop.Repository.UserRepository;
import com.gafbank.rashop.entity.Action;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDateTime;

@Aspect
@Component
@Order(10)
public class UserCreation {
    private final UserRepository userRepository;
    @Autowired
    public UserCreation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }




    @Pointcut("execution(* com.gafbank.rashop.service.UpdateDispatcher.distribute(..))")
    public void distributeMethodPointcut(){}

    @Around("distributeMethodPointcut()")
    public Object distributeMethodAdvice(ProceedingJoinPoint joinPoint)  throws Throwable{
        Update update = (Update) joinPoint.getArgs()[0];
        User telegramUser;
        if(update.hasMessage()){
            telegramUser = update.getMessage().getFrom();

        }else if(update.hasCallbackQuery()){
            telegramUser = update.getCallbackQuery().getFrom();
        }else{
            return joinPoint.proceed();
        }
        if(userRepository.existsById(telegramUser.getId())){
            return joinPoint.proceed();
        }
        com.gafbank.rashop.entity.User newUser =
                com.gafbank.rashop.entity.User.builder()
                        .chatId(telegramUser.getId())
                        .action(Action.FREE)
                        .username(telegramUser.getUserName())
                        .products(null)
                        .build();
        userRepository.save(newUser);
        return joinPoint.proceed();
    }
}
