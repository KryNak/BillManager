package com.app.billmanager.service;

import com.app.billmanager.model.Bill;
import com.app.billmanager.model.User;
import com.app.billmanager.repository.BillRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class BillService {

    private final BillRepository billRepository;

    public BillService(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    public void saveBill(Bill bill) {
        billRepository.save(bill);
    }

    public void deleteBill(long id) {
        billRepository.deleteById(id);
    }

    public Bill findBillById(Long id) {
        return billRepository.findBillById(id);
    }

    public Page<Bill> findBillsByDateAndUser(User user, LocalDate date, int pageNo, int pageSize){
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return billRepository.findBillsByDateAndUser(date, user, pageable);
    }

    public Double findSumByEmailAndDate(User user, LocalDate date){
        return billRepository.findSumByUserAndDate(user.getEmail(), date);
    }

    public Double findMonthSumByEmailAndDateBetween(String email, LocalDate startDate, LocalDate endDate){
        return billRepository.findMonthSumByEmailAndDateBetween(email, startDate, endDate);
    }

}
