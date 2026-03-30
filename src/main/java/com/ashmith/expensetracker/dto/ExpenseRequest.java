package com.ashmith.expensetracker.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ExpenseRequest {
    private String title;
    private Double amount;
    private String category;
    private LocalDate date;
    private String description;
}