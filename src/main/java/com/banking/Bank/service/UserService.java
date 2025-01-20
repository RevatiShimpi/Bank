package com.banking.Bank.service;

import com.banking.Bank.dto.*;

public interface UserService {
    BankResponse createAccount(UserRequest userrequest);

    BankResponse balanceEnquiry(EnquiryRequest request);
    String nameEnquiry(EnquiryRequest request);
    BankResponse creditAccount(CreditDebitRequest enq);
    BankResponse debitAccount(CreditDebitRequest request);
    BankResponse transfer(TransferRequest request);


}
