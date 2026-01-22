package com.course.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import com.course.Utility.TelegramBot;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class TelegramConfig {

    @Autowired
    private TelegramBot telegramBot;

    @PostConstruct
    public void registerBot() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

            botsApi.registerBot(telegramBot);
            log.debug("Telegram Bot registered");
        } catch (Exception e) {
            throw new RuntimeException("Gagal register Telegram bot", e);
        }
    }
    
}
