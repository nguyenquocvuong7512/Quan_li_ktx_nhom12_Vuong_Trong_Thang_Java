package com.example.NguyenQuocVuong_chuong4.dto;

import java.math.BigDecimal;

public record DashboardStatsDTO(
        long totalStudents,
        long totalBuildings,
        long totalRooms,
        long totalRoomTypes,
        long availableRooms,
        long occupiedRooms,
        long totalAccounts,
        long totalContracts,
        long activeContracts,
        long totalInvoices,
        long unpaidInvoices,
        long overdueInvoices,
        BigDecimal totalRevenue,
        BigDecimal totalDebt
) {
}
