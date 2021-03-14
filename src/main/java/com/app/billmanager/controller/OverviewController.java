package com.app.billmanager.controller;

import com.app.billmanager.model.User;
import com.app.billmanager.service.BillService;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("api/overview")
public class OverviewController {

    private BillService billService;

    public OverviewController(BillService billService){
        this.billService = billService;
    }

    @ModelAttribute
    public void user(Model model, @AuthenticationPrincipal User user){
        UriComponents transactionsLink= UriComponentsBuilder.fromPath("/api/transactions")
                .queryParam("date", LocalDate.now().toString())
                .queryParam("pageNo", 1)
                .build();

        model.addAttribute("transactionsLink", transactionsLink.toUriString());
        model.addAttribute("name", user.getFullName());
    }

    @GetMapping("")
    public String main(Model model, @Param("chartNo") Integer chartNo, @Param("timePeriod") Character timePeriod,
                       @AuthenticationPrincipal User user){

        switch (timePeriod){
            case 'M':
                model.addAttribute("label", "Month");
                model.addAttribute("chartRange", getMonthRange(chartNo));
                model.addAttribute("chartData", getMonthData(user, chartNo));
                break;
            case 'y':
                model.addAttribute("label", "Year");
                model.addAttribute("chartRange", getYearRange(chartNo));
                model.addAttribute("chartData", getYearData(user, chartNo));
                break;
            case 'd':
                break;
            case 'w':
                model.addAttribute("label", "Week");
                model.addAttribute("chartRange", getWeekRange(chartNo));
                model.addAttribute("chartData", getWeekData(user, chartNo));
                break;
            default:
                throw new IllegalArgumentException();
        }

        return "data-overview";
    }

    List<List<Object>> getWeekData(User user, Integer chartNo) {
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

    String getWeekRange(Integer chartNo){
        LocalDate firstDayOfWeek = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1).plusDays(chartNo * 7);
        LocalDate lastDayOfWeek = firstDayOfWeek.plusDays(6);

        return String.format("%02d - %s", firstDayOfWeek.getDayOfMonth(), lastDayOfWeek.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
    }

    List<List<Object>> getMonthData(User user, Integer chartNo) {

        LocalDate initial = LocalDate.now().plusMonths(chartNo);
        LocalDate start = initial.withDayOfMonth(1);
        LocalDate end = initial.withDayOfMonth(initial.lengthOfMonth()).plusDays(1);

        List<List<Object>> resultList = new ArrayList<>();
        resultList.add(List.of("Day", "Sum", new AbstractMap.SimpleEntry<>("role", "style")));

        for(LocalDate i = start; i.isBefore(end); i = i.plusDays(1)){
            double sum = Optional.ofNullable(billService.findSumByEmailAndDate(user, i)).orElse(0.0);
            resultList.add(List.of(i.getDayOfMonth(), sum, "color: #76A7FA"));
        }

        return resultList;
    }

    String getMonthRange(Integer chartNo){
        LocalDate initial = LocalDate.now().plusMonths(chartNo);
        LocalDate firstDayOfMonth = initial.withDayOfMonth(1);
        LocalDate lastDayOfMonth = initial.withDayOfMonth(initial.lengthOfMonth());

        return String.format("%02d - %s", firstDayOfMonth.getDayOfMonth(), lastDayOfMonth.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
    }

    List<List<Object>> getYearData(User user, Integer chartNo){

        int year = LocalDate.now().getYear() + chartNo;
        LocalDate start = LocalDate.of(year, Month.JANUARY, 1);
        LocalDate end = LocalDate.of(year, Month.DECEMBER, 1).plusMonths(1);

        List<List<Object>> resultList = new ArrayList<>();
        resultList.add(List.of("Month", "Sum", new AbstractMap.SimpleEntry<>("role", "style")));

        for(LocalDate i = start; i.isBefore(end); i = i.plusMonths(1)){
            double sum = Optional.ofNullable(billService.findMonthSumByEmailAndDateBetween(user.getEmail(), i, i.withDayOfMonth(i.lengthOfMonth()))).orElse(0.0);
            resultList.add(List.of(i.getMonth(), sum, "color: #76A7FA"));
        }

        return resultList;
    }

    String getYearRange(Integer chartNo){
        int year = LocalDate.now().getYear() + chartNo;
        String firstDayOfYear = LocalDate.of(year, Month.JANUARY, 1).format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        String lastDayOfYear = LocalDate.of(year, Month.DECEMBER, 31).format(DateTimeFormatter.ofPattern("dd MMM yyyy"));

        return String.format("%s - %s", firstDayOfYear, lastDayOfYear);
    }

}
