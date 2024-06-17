package com.gafbank.rashop.telegram;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("telegram-bot")
public class TelegramProperties {
    private String token;
    private String username;
    private String path;
}
