package com.app.billmanager.controller;

import com.app.billmanager.configuration.BillProps;
import com.app.billmanager.model.Bill;
import com.app.billmanager.model.User;
import com.app.billmanager.service.BillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/api/transactions")
public class TransactionsController {

    private BillService billService;
    private BillProps billProps;

    public TransactionsController(BillService billService, BillProps billProps) {
        this.billService = billService;
        this.billProps = billProps;
    }

    @ModelAttribute
    public void addUserName(Model model, @AuthenticationPrincipal User user) {
        UriComponents overviewLink = UriComponentsBuilder.fromPath("/api/overview")
                .queryParam("chartNo", 0)
                .queryParam("timePeriod", "M")
                .build();

        model.addAttribute("overviewLink", overviewLink.toUriString());
        model.addAttribute("name", user.getFullName());
    }

    @GetMapping("")
    public String main(Model model, @Param("date") String date, @Param("pageNo") Integer pageNo,
                       @AuthenticationPrincipal User user) {

        Page<Bill> page = billService.findBillsByDateAndUser(user, LocalDate.parse(date), pageNo, billProps.getPageSize());
        List<Bill> listBills = page.getContent();

        model.addAttribute("currentDate", date);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("bills", listBills);
        model.addAttribute("postBillModel", new Bill());

        return "data-transactions";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute(name = "postBillModel") Bill bill, @Param(value = "cDate") String cDate,
                      @Param(value = "pageNo") Integer pageNo, @AuthenticationPrincipal User user) {

        bill.setDate(LocalDate.parse(cDate));
        bill.setUser(user);
        billService.saveBill(bill);

        UriComponents uri = UriComponentsBuilder.fromPath("/api/transactions")
                .queryParam("date", bill.getDate().toString())
                .queryParam("pageNo", pageNo)
                .build();


        return String.format("redirect:%s", uri);
    }

    @GetMapping("/delete")
    public String delete(@Param("id") Long id, @Param("date") String date,
                         @Param("pageNo") Integer pageNo) {

        billService.deleteBill(id);

        UriComponents uri = UriComponentsBuilder.fromPath("/api/transactions")
                .queryParam("date", date)
                .queryParam("pageNo", pageNo)
                .build();

        return String.format("redirect:%s", uri);
    }

    @GetMapping("/getOne/{id}")
    public @ResponseBody
    Bill getOne(@PathVariable("id") Long id) {
        return billService.findBillById(id);
    }

    @GetMapping(value = "/sum")
    public @ResponseBody
    Double getSum(@Param(value = "date") String date, @AuthenticationPrincipal User user) {
        return billService.findSumByEmailAndDate(user, LocalDate.parse(date));
    }

}
