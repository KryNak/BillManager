package com.app.billmanager.repository;

import com.app.billmanager.model.ConfirmationToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfirmationTokenRepository extends CrudRepository<ConfirmationToken, Long> {
    public ConfirmationToken findByConfirmationToken(String confirmationToken);
}
