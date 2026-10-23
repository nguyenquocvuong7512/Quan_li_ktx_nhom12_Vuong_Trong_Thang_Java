package com.example.NguyenQuocVuong_chuong4.dto;

public record DashboardStatsDTO(
        long totalStudents,
        long totalBuildings,
        long totalRooms,
        long totalRoomTypes,
        long availableRooms,
        long occupiedRooms,
        long totalAccounts,
        long totalContracts,
        long activeContracts
) {
}
