package com.app.billmanager.repository;

import com.app.billmanager.model.Bill;
import com.app.billmanager.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface BillRepository extends CrudRepository<Bill, Long> {
    Bill findBillById(Long id);
    Page<Bill> findBillsByDateAndUser(LocalDate date, User user, Pageable pageable);

    @Query(value = "SELECT SUM(b.price) FROM Bill b INNER JOIN b.user u WHERE u.email = :email AND b.date = :date")
    Double findSumByUserAndDate(@Param(value = "email") String email, @Param(value = "date") LocalDate date);

    @Query(value = "SELECT SUM(b.price) FROM Bill b INNER JOIN b.user u WHERE u.email = :email AND b.date BETWEEN :startDate AND :endDate")
    Double findMonthSumByEmailAndDateBetween(@Param(value = "email") String email, @Param(value = "startDate") LocalDate startDate, @Param(value = "endDate") LocalDate endDate);
}
