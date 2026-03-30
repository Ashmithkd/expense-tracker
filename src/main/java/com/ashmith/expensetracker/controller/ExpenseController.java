package com.ashmith.expensetracker.controller;

import com.ashmith.expensetracker.dto.*;
import com.ashmith.expensetracker.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @GetMapping("/expenses")
    public ResponseEntity<List<ExpenseResponse>> getExpenses(Principal principal) {
        return ResponseEntity.ok(expenseService.getUserExpenses(principal.getName()));
    }

    @PostMapping("/expenses")
    public ResponseEntity<ExpenseResponse> addExpense(Principal principal,
                                                      @RequestBody ExpenseRequest request) {
        return ResponseEntity.ok(expenseService.addExpense(principal.getName(), request));
    }

    @PutMapping("/expenses/{id}")
    public ResponseEntity<ExpenseResponse> updateExpense(Principal principal,
                                                         @PathVariable Long id,
                                                         @RequestBody ExpenseRequest request) {
        return ResponseEntity.ok(expenseService.updateExpense(principal.getName(), id, request));
    }

    @DeleteMapping("/expenses/{id}")
    public ResponseEntity<String> deleteExpense(Principal principal,
                                                @PathVariable Long id) {
        expenseService.deleteExpense(principal.getName(), id);
        return ResponseEntity.ok("Expense deleted");
    }

    @GetMapping("/expenses/summary")
    public ResponseEntity<Map<String, Double>> getSummary(Principal principal) {
        return ResponseEntity.ok(expenseService.getSummary(principal.getName()));
    }

    @GetMapping("/admin/expenses")
    public ResponseEntity<List<ExpenseResponse>> getAllExpenses() {
        return ResponseEntity.ok(expenseService.getAllExpenses());
    }
}