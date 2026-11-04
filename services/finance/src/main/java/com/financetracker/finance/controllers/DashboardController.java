
package com.financetracker.finance.controllers;

import java.time.YearMonth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.financetracker.finance.dto.response.ApiResponseDto;
import com.financetracker.finance.dto.response.DashboardResponse;
import com.financetracker.finance.services.DashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/finance/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<ApiResponseDto<DashboardResponse>> getDashboard(
            @RequestParam YearMonth month) {

        DashboardResponse response = dashboardService.getDashboard(month);

        return ResponseEntity.ok(ApiResponseDto.<DashboardResponse>builder().data(response).build());
    }
}
