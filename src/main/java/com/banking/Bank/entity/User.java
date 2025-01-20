//Customer of bank
package com.banking.Bank.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name="users")
public class User {

    @Id
    private Long id;
    private String firstName;
    private String lastName;
    private String gender;
    private String address;
    private String state;
    private String accountNumber;
    private String accountBalance;
    private String email;
    private String phoneNumber;
    private String status;

}
