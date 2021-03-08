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
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
@Slf4j
@RequestMapping("/api")
public class DataController {

    private BillService billService;
    private BillProps billProps;

    public DataController(BillService billService, BillProps billProps) {
        this.billService = billService;
        this.billProps = billProps;
    }

    @ModelAttribute
    public void addUserName(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("name", user.getFullName());
    }

    @GetMapping("")
    public String main(Model model, @Param("date") String date, @Param("pageNo") Integer pageNo,
                       @Param("chartNo") Integer chartNo, @AuthenticationPrincipal User user) {

        Page<Bill> page = billService.findBillsByDateAndUser(user, LocalDate.parse(date), pageNo, billProps.getPageSize());
        List<Bill> listBills = page.getContent();

        model.addAttribute("chartRange", getChartRange(chartNo));
        model.addAttribute("chartData", getChartData(user, chartNo));
        model.addAttribute("currentDate", date);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("currentChartNo", chartNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("bills", listBills);
        model.addAttribute("postBillModel", new Bill());

        return "data-transactions";
        //return "data";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute(name = "postBillModel") Bill bill, @Param(value = "cDate") String cDate,
                      @Param(value = "pageNo") Integer pageNo, @Param("chartNo") Integer chartNo,
                      @AuthenticationPrincipal User user) {

        bill.setDate(LocalDate.parse(cDate));
        bill.setUser(user);
        billService.saveBill(bill);

        UriComponents uri = UriComponentsBuilder.fromPath("/api")
                .queryParam("date", bill.getDate().toString())
                .queryParam("pageNo", pageNo)
                .queryParam("chartNo", chartNo)
                .build();


        return String.format("redirect:%s", uri);
    }

    @GetMapping("/delete")
    public String delete(@Param("id") Long id, @Param("date") String date,
                         @Param("pageNo") Integer pageNo, @Param("chartNo") Integer chartNo) {

        billService.deleteBill(id);

        UriComponents uri = UriComponentsBuilder.fromPath("/api")
                .queryParam("date", date)
                .queryParam("pageNo", pageNo)
                .queryParam("chartNo", chartNo)
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

    List<List<Object>> getChartData(User user, Integer chartNo) {
        LocalDate firstDayOfWeek = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1).plusDays(chartNo * 7);

        ArrayList<Double> weekSum = new ArrayList<>();
        for(LocalDate i = firstDayOfWeek; i.isBefore(firstDayOfWeek.plusDays(7)); i = i.plusDays(1)){
            Double sum = billService.findSumByEmailAndDate(user, i);
            weekSum.add(Objects.isNull(sum) ? 0 : sum);
        }

        return List.of(
                List.of("Day", "Sum", new AbstractMap.SimpleEntry<>("role", "style")),
                List.of("Monday", weekSum.get(0), "color: #76A7FA"),
                List.of("Tuesday", weekSum.get(1), "color: #76A7FA"),
                List.of("Wednesday", weekSum.get(2), "color: #76A7FA"),
                List.of("Thursday", weekSum.get(3), "color: #76A7FA"),
                List.of("Friday", weekSum.get(4), "color: #76A7FA"),
                List.of("Saturday", weekSum.get(5), "color: #76A7FA"),
                List.of("Sunday", weekSum.get(6), "color: #76A7FA")
        );
    }

    String getChartRange(Integer chartNo){
        LocalDate firstDayOfWeek = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1).plusDays(chartNo * 7);
        LocalDate lastDayOfWeek = firstDayOfWeek.plusDays(6);

        return String.format("%02d - %s", firstDayOfWeek.getDayOfMonth(), lastDayOfWeek.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
    }

}
