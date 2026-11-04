package com.financetracker.finance.dto.response;

import java.math.BigDecimal;
import java.time.YearMonth;

public record MonthlyTrendResponse(
        YearMonth month,
        BigDecimal income,
        BigDecimal expenses
) {}