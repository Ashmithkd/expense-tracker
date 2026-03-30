package com.ashmith.expensetracker.service;

import com.ashmith.expensetracker.dto.*;
import com.ashmith.expensetracker.model.Expense;
import com.ashmith.expensetracker.model.User;
import com.ashmith.expensetracker.repository.ExpenseRepository;
import com.ashmith.expensetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private ExpenseResponse toResponse(Expense expense) {
        ExpenseResponse res = new ExpenseResponse();
        res.setId(expense.getId());
        res.setTitle(expense.getTitle());
        res.setAmount(expense.getAmount());
        res.setCategory(expense.getCategory());
        res.setDate(expense.getDate());
        res.setDescription(expense.getDescription());
        res.setUsername(expense.getUser().getUsername());
        return res;
    }

    public List<ExpenseResponse> getUserExpenses(String username) {
        User user = getUser(username);
        return expenseRepository.findByUser(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ExpenseResponse addExpense(String username, ExpenseRequest request) {
        User user = getUser(username);
        Expense expense = new Expense();
        expense.setTitle(request.getTitle());
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setDate(request.getDate());
        expense.setDescription(request.getDescription());
        expense.setUser(user);
        return toResponse(expenseRepository.save(expense));
    }

    public ExpenseResponse updateExpense(String username, Long id, ExpenseRequest request) {
        User user = getUser(username);
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        if (!expense.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        expense.setTitle(request.getTitle());
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setDate(request.getDate());
        expense.setDescription(request.getDescription());
        return toResponse(expenseRepository.save(expense));
    }

    public void deleteExpense(String username, Long id) {
        User user = getUser(username);
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        if (!expense.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        expenseRepository.delete(expense);
    }

    public Map<String, Double> getSummary(String username) {
        User user = getUser(username);
        List<Object[]> results = expenseRepository.findSummaryByUser(user);
        Map<String, Double> summary = new LinkedHashMap<>();
        for (Object[] row : results) {
            summary.put((String) row[0], (Double) row[1]);
        }
        return summary;
    }

    public List<ExpenseResponse> getAllExpenses() {
        return expenseRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }
}