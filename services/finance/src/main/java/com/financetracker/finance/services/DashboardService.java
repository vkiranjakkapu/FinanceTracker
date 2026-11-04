package com.financetracker.finance.services;

import java.time.YearMonth;

import com.financetracker.finance.dto.response.DashboardResponse;

public interface DashboardService {

    DashboardResponse getDashboard(YearMonth month);
}