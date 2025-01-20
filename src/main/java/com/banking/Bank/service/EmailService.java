package com.banking.Bank.service;

import com.banking.Bank.dto.EmailDetails;

public interface EmailService {
    void sendEmailAlert(EmailDetails emailDetails);
}
