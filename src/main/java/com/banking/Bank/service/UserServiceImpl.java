package com.banking.Bank.service;

import com.banking.Bank.dto.*;
import com.banking.Bank.entity.User;
import com.banking.Bank.repository.UserRepository;
import com.banking.Bank.utils.AccountUtils;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class UserServiceImpl implements UserService{
    UserRepository userrepo;
    public BankResponse createAccount(UserRequest userRequest){
        if(userrepo.existsByEmail(userRequest.getEmail())){
            return new BankResponse("001","Alreay exists",null);
        }
        User newUser= User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .gender(userRequest.getGender())
                .state(userRequest.getState())
                .email(userRequest.getEmail())
                .address(userRequest.getAddress())
                .phoneNumber(userRequest.getPhoneNumber())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(0L)
                .status("ACTIVE")
                .build();

        User saveUser=userrepo.save(newUser);

        EmailDetails emailDetails=EmailDetails.builder()
                .recipient(saveUser.getEmail())
                .subject("Account Creation")
                .message("Congratulations your account has been successfully created")
                .build();

        return new BankResponse("002","Account Created",null);
    }

    //balance enguiry, name,credit,debit,transfer
    public BankResponse balanceEnquiry(EnquiryRequest enq){
        boolean isAccountExist=userrepo.existsByAccountNumber(enq.getAccountNumber());
        if(!isAccountExist){
            return BankResponse.builder()
                    .responseCode("003")
                    .responseMessage("Account does not exist")
                    .accountInfo(null)
                    .build();
        }
        User user=userrepo.findByAccountNumber(enq.getAccountNumber());
        return BankResponse.builder()
                .responseCode("001")
                .responseMessage("Account found for this number")
                .accountInfo(AccountInfo.builder()
                        .accountBalance(user.getAccountBalance())
                        .accountNumber(enq.getAccountNumber())
                        .accountName(user.getFirstName() + " "+user.getLastName())
                        .build())
                .build();

    }

    public String nameEnquiry(EnquiryRequest request) {
        boolean isAccountExist = userrepo.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist){
            return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;
        }
        User foundUser = userrepo.findByAccountNumber(request.getAccountNumber());
        return foundUser.getFirstName() + " " + foundUser.getLastName();
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest enq) {
        //check if account request
        boolean isAccountExist=userrepo.existsByAccountNumber(enq.getAccountNumber());
        if(!isAccountExist){
            return BankResponse.builder()
                    .responseCode("003")
                    .responseMessage("Account does not exist")
                    .accountInfo(null)
                    .build();
        }
        User user=userrepo.findByAccountNumber(enq.getAccountNumber());
        user.setAccountBalance(user.getAccountBalance()+enq.getAmount());
        userrepo.save(user);
        return BankResponse.builder()
                .responseCode("004")
                .responseMessage("Balance Added")
                .accountInfo(AccountInfo.builder()
                        .accountBalance(user.getAccountBalance())
                        .accountNumber(enq.getAccountNumber())
                        .accountName(user.getFirstName() + " "+user.getLastName())
                        .build())
                .build();
    }
    public BankResponse debitAccount(CreditDebitRequest request) {
        //check if the account exists
        //check if the amount you intend to withdraw is not more than the current account balance
        boolean isAccountExist = userrepo.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User userToDebit = userrepo.findByAccountNumber(request.getAccountNumber());
        BigInteger availableBalance =userToDebit.getAccountBalance().toBigInteger();
        BigInteger debitAmount = request.getAmount().toBigInteger();
        if ( availableBalance.intValue() < debitAmount.intValue()){
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        else {
            userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
            userrepo.save(userToDebit);
            TransacctionDto transacctionDto = TransacctionDto.builder()
                    .accountNumber(userToDebit.getAccountNumber())
                    .transactionType("CREDIT")
                    .amount(request.getAmount())
                    .build();

            transactionService.saveTransaction(transacctionDto);
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS)
                    .responseMessage(AccountUtils.ACCOUNT_DEBITED_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountNumber(request.getAccountNumber())
                            .accountName(userToDebit.getFirstName() + " " + userToDebit.getLastName() + " " + userToDebit.getOtherName())
                            .accountBalance(userToDebit.getAccountBalance())
                            .build())
                    .build();


        }
}


