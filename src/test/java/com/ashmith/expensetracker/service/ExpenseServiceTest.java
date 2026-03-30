package com.ashmith.expensetracker.service;

import com.ashmith.expensetracker.dto.ExpenseRequest;
import com.ashmith.expensetracker.dto.ExpenseResponse;
import com.ashmith.expensetracker.model.Expense;
import com.ashmith.expensetracker.model.User;
import com.ashmith.expensetracker.repository.ExpenseRepository;
import com.ashmith.expensetracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ExpenseService expenseService;

    private User testUser;
    private Expense testExpense;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("ashmith");
        testUser.setEmail("ashmith@test.com");
        testUser.setPassword("encoded");
        testUser.setRole("USER");

        testExpense = new Expense();
        testExpense.setId(1L);
        testExpense.setTitle("Groceries");
        testExpense.setAmount(45.50);
        testExpense.setCategory("Food");
        testExpense.setDate(LocalDate.of(2026, 3, 30));
        testExpense.setDescription("Weekly shopping");
        testExpense.setUser(testUser);
    }

    @Test
    void addExpense_Success() {
        ExpenseRequest request = new ExpenseRequest();
        request.setTitle("Groceries");
        request.setAmount(45.50);
        request.setCategory("Food");
        request.setDate(LocalDate.of(2026, 3, 30));
        request.setDescription("Weekly shopping");

        when(userRepository.findByUsername("ashmith")).thenReturn(Optional.of(testUser));
        when(expenseRepository.save(any(Expense.class))).thenReturn(testExpense);

        ExpenseResponse response = expenseService.addExpense("ashmith", request);

        assertNotNull(response);
        assertEquals("Groceries", response.getTitle());
        assertEquals(45.50, response.getAmount());
        assertEquals("Food", response.getCategory());
        assertEquals("ashmith", response.getUsername());
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    void getUserExpenses_ReturnsOnlyUserExpenses() {
        when(userRepository.findByUsername("ashmith")).thenReturn(Optional.of(testUser));
        when(expenseRepository.findByUser(testUser)).thenReturn(List.of(testExpense));

        List<ExpenseResponse> result = expenseService.getUserExpenses("ashmith");

        assertEquals(1, result.size());
        assertEquals("Groceries", result.get(0).getTitle());
        assertEquals("ashmith", result.get(0).getUsername());
    }

    @Test
    void deleteExpense_Success() {
        when(userRepository.findByUsername("ashmith")).thenReturn(Optional.of(testUser));
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(testExpense));

        assertDoesNotThrow(() -> expenseService.deleteExpense("ashmith", 1L));
        verify(expenseRepository, times(1)).delete(testExpense);
    }

    @Test
    void deleteExpense_Unauthorized_ThrowsException() {
        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setUsername("other");

        when(userRepository.findByUsername("other")).thenReturn(Optional.of(otherUser));
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(testExpense));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> expenseService.deleteExpense("other", 1L));

        assertEquals("Unauthorized", ex.getMessage());
        verify(expenseRepository, never()).delete(any());
    }

    @Test
    void addExpense_UserNotFound_ThrowsException() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> expenseService.addExpense("ghost", new ExpenseRequest()));

        assertEquals("User not found", ex.getMessage());
    }
}
