package com.example.hanoicraftcorner.model;

import java.util.Random;

public class OtpManager {
    private static final int DEFAULT_OTP_LENGTH = 6;

    private String currentOtp;
    private int otpLength;

    public OtpManager() {
        this(DEFAULT_OTP_LENGTH);
    }

    public OtpManager(int otpLength) {
        this.otpLength = otpLength;
    }

    public String generateOtp() {
        StringBuilder otpBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < otpLength; i++) {
            otpBuilder.append(random.nextInt(10));
        }

        currentOtp = otpBuilder.toString();

        return currentOtp;
    }
    public String getCurrentOtp() {
        return currentOtp;
    }

    public void setCurrentOtp(String currentOtp) {
        this.currentOtp = currentOtp;
    }
}
