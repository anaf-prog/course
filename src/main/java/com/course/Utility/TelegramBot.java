package com.course.Utility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final String adminChatId;

    public TelegramBot(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.username}") String botUsername,
            @Value("${telegram.admin.chatid}") String adminChatId) {
        super(botToken);
        this.botUsername = botUsername;
        this.adminChatId = adminChatId;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Long chatId = update.getMessage().getChatId();

            // 1. Jika yang dikirim adalah VIDEO
            if (update.getMessage().hasVideo()) {
                String fileId = update.getMessage().getVideo().getFileId();
                Integer duration = update.getMessage().getVideo().getDuration();
                Long size = update.getMessage().getVideo().getFileSize();

                String info = "<b>🎬 VIDEO DETECTED</b>\n\n" +
                        "Simpan kode ini di kolom <b>telegram_file_id</b>:\n" +
                        "<code>" + fileId + "</code>\n\n" +
                        "Durasi: " + duration + " detik\n" +
                        "Size: " + size + " bytes";

                send(chatId, info);
            }
            // 2. Jika yang dikirim hanya TEXT biasa
            else if (update.getMessage().hasText()) {
                send(chatId, "Bot Aktif ✅\nKirim video ke sini untuk mendapatkan File ID.");
            }
        }
    }

    private void send(Long chatId, String text) {
        try {
            execute(SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .parseMode("HTML") // Menambah ini agar format <code> bisa diklik/copy
                .build());
        } catch (TelegramApiException e) {
            log.error("Gagal kirim : " + e.getMessage());
        }
    }
}