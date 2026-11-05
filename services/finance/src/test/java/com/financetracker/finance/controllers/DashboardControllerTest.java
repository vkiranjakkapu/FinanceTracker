package com.financetracker.finance.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.financetracker.finance.dto.response.ApiResponseDto;
import com.financetracker.finance.dto.response.DashboardResponse;
import com.financetracker.finance.services.DashboardService;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    private DashboardService dashboardService;

    @InjectMocks
    private DashboardController dashboardController;

    private DashboardResponse dashboardResponse() {
        return new DashboardResponse(
                LocalDate.of(2026, 11, 1),
                new BigDecimal("25000.00"),
                new BigDecimal("10000.00"),
                new BigDecimal("15000.00"),
                List.of(),
                List.of(),
                List.of(),
                List.of());
    }

    @Test
    void getDashboard_ShouldReturnDashboardForRequestedMonth() {

        YearMonth month = YearMonth.of(2026, 11);
        DashboardResponse expected = dashboardResponse();

        when(dashboardService.getDashboard(month))
                .thenReturn(expected);

        ResponseEntity<ApiResponseDto<DashboardResponse>> response =
                dashboardController.getDashboard(month);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody().data());

        verify(dashboardService).getDashboard(month);
    }

    @Test
    void getDashboard_ShouldReturnDashboardForDifferentMonth() {

        YearMonth month = YearMonth.of(2026, 10);

        DashboardResponse expected = new DashboardResponse(
                LocalDate.of(2026, 10, 1),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                List.of(),
                List.of(),
                List.of(),
                List.of());

        when(dashboardService.getDashboard(month))
                .thenReturn(expected);

        ResponseEntity<ApiResponseDto<DashboardResponse>> response =
                dashboardController.getDashboard(month);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody().data());

        verify(dashboardService).getDashboard(month);
    }
}