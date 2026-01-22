package com.course.error.custom;

/**
 * Exception login custom untuk pengecekan username atau email yang terdatar
 * saat proses login
 */
public class LoginException extends BusinessException {

    /**
     * Exception login custom dengan pesan custom.
     *
     * @param message pesan kesalahan
     */
    public LoginException(String message) {
        super(message);
    }

    /**
     * Exception login custom dengan pesan default.
     */
    public LoginException() {
        super("Email atau Username salah");
    }
    
}
