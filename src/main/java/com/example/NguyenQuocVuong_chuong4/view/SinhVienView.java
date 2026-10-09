// package com.example.NguyenQuocVuong_chuong4.view;

// import com.example.NguyenQuocVuong_chuong4.model.SinhVien;
// import java.util.List;
// import java.util.Scanner;

// public class SinhVienView {
//     private Scanner scanner = new Scanner(System.in);

//     public void showMenu() {
//         System.out.println("\n=== QUAN LY SINH VIEN ===");
//         System.out.println("1. Xem danh sach sinh vien");
//         System.out.println("2. Them sinh vien moi");
//         System.out.println("3. Sua thong tin sinh vien");
//         System.out.println("4. Xoa sinh vien");
//         System.out.println("0. Thoat");
//         System.out.print("Chon: ");
//     }

//     public int getChoice() {
//         try {
//             return Integer.parseInt(scanner.nextLine());
//         } catch (NumberFormatException e) {
//             return -1;
//         }
//     }

//     public void showList(List<SinhVien> list) {
//         System.out.println("\n=== DANH SACH SINH VIEN ===");
//         if (list == null || list.isEmpty()) {
//             System.out.println("Danh sach sinh vien dang trong.");
//             return;
//         }
//         for (SinhVien item : list) {
//             System.out.println(item);
//         }
//     }

//     public SinhVien inputSinhVien() {
//         System.out.println("\n=== THEM SINH VIEN MOI ===");
//         System.out.print("Nhap ma sinh vien: ");
//         String maSV = scanner.nextLine();
//         System.out.print("Nhap ho ten: ");
//         String hoTen = scanner.nextLine();
//         System.out.print("Nhap ngay sinh (dd/mm/yyyy): ");
//         String ngaySinh = scanner.nextLine();
//         System.out.print("Nhap gioi tinh: ");
//         String gioiTinh = scanner.nextLine();
//         System.out.print("Nhap CCCD: ");
//         String cccd = scanner.nextLine();
//         System.out.print("Nhap so dien thoai: ");
//         String sdt = scanner.nextLine();
//         System.out.print("Nhap email: ");
//         String email = scanner.nextLine();
//         System.out.print("Nhap lop: ");
//         String lop = scanner.nextLine();
//         System.out.print("Nhap khoa: ");
//         String khoa = scanner.nextLine();
//         System.out.print("Nhap que quan: ");
//         String queQuan = scanner.nextLine();
//         return new SinhVien(maSV, hoTen, ngaySinh, gioiTinh, cccd, sdt, email, lop, khoa, queQuan);
//     }

//     public String inputMaSV(String actionName) {
//         System.out.print("\nNhap ma sinh vien can " + actionName + ": ");
//         return scanner.nextLine();
//     }

//     public SinhVien inputSinhVienToUpdate(String maSV) {
//         System.out.println("--- NHAP THONG TIN MOI ---");
//         System.out.print("Nhap ho ten moi: ");
//         String hoTen = scanner.nextLine();
//         System.out.print("Nhap ngay sinh moi: ");
//         String ngaySinh = scanner.nextLine();
//         System.out.print("Nhap gioi tinh moi: ");
//         String gioiTinh = scanner.nextLine();
//         System.out.print("Nhap CCCD moi: ");
//         String cccd = scanner.nextLine();
//         System.out.print("Nhap so dien thoai moi: ");
//         String sdt = scanner.nextLine();
//         System.out.print("Nhap email moi: ");
//         String email = scanner.nextLine();
//         System.out.print("Nhap lop moi: ");
//         String lop = scanner.nextLine();
//         System.out.print("Nhap khoa moi: ");
//         String khoa = scanner.nextLine();
//         System.out.print("Nhap que quan moi: ");
//         String queQuan = scanner.nextLine();
//         return new SinhVien(maSV, hoTen, ngaySinh, gioiTinh, cccd, sdt, email, lop, khoa, queQuan);
//     }

//     public void showMessage(String message) {
//         System.out.println(message);
//     }
// }