package com.example.payments.repository;

import com.example.payments.model.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select account from Account account where account.accountNumber in :numbers order by account.accountNumber")
    List<Account> lockByAccountNumbers(@Param("numbers") List<String> numbers);
}
