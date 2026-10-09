// package view;

// import model.HopDongThue;
// import java.util.List;
// import java.util.Scanner;

// public class HopDongThueView {
//     private Scanner scanner = new Scanner(System.in);

//     public void showMenu() {
//         System.out.println("\n=== QUAN LY HOP DONG THUE ===");
//         System.out.println("1. Xem danh sach hop dong");
//         System.out.println("2. Them hop dong moi");
//         System.out.println("3. Sua thong tin hop dong");
//         System.out.println("4. Xoa hop dong");
//         System.out.println("0. Quay lai menu chinh");
//         System.out.print("Chon: ");
//     }

//     public int getChoice() {
//         try {
//             return Integer.parseInt(scanner.nextLine());
//         } catch (NumberFormatException e) {
//             return -1;
//         }
//     }

//     public void showList(List<HopDongThue> list) {
//         System.out.println("\n=== DANH SACH HOP DONG THUE ===");
//         if (list == null || list.isEmpty()) {
//             System.out.println("Danh sach hop dong dang trong.");
//             return;
//         }
//         for (HopDongThue item : list) {
//             System.out.println(item);
//         }
//     }

//     public HopDongThue inputHopDongThue() {
//         System.out.println("\n=== THEM HOP DONG MOI ===");
//         System.out.print("Nhap ma hop dong: ");
//         int maHD = Integer.parseInt(scanner.nextLine());

//         System.out.print("Nhap ma sinh vien: ");
//         String maSV = scanner.nextLine();

//         System.out.print("Nhap ma phong: ");
//         int maPhong = Integer.parseInt(scanner.nextLine());

//         System.out.print("Nhap ngay bat dau (dd/mm/yyyy): ");
//         String ngayBD = scanner.nextLine();

//         System.out.print("Nhap ngay ket thuc (dd/mm/yyyy): ");
//         String ngayKT = scanner.nextLine();

//         System.out.print("Nhap tien coc: ");
//         double tienCoc = Double.parseDouble(scanner.nextLine());

//         System.out.print("Nhap trang thai (Hoc ky hien tai/Da thanh ly/Huy): ");
//         String trangThai = scanner.nextLine();

//         return new HopDongThue(maHD, maSV, maPhong, ngayBD, ngayKT, tienCoc, trangThai);
//     }

//     public int inputMaHD(String actionName) {
//         System.out.print("\nNhap ma hop dong can " + actionName + ": ");
//         return Integer.parseInt(scanner.nextLine());
//     }

//     public HopDongThue inputHopDongThueToUpdate(int maHD) {
//         System.out.println("--- NHAP THONG TIN MOI ---");
//         System.out.print("Nhap ma sinh vien moi: ");
//         String maSV = scanner.nextLine();

//         System.out.print("Nhap ma phong moi: ");
//         int maPhong = Integer.parseInt(scanner.nextLine());

//         System.out.print("Nhap ngay bat dau moi (dd/mm/yyyy): ");
//         String ngayBD = scanner.nextLine();

//         System.out.print("Nhap ngay ket thuc moi (dd/mm/yyyy): ");
//         String ngayKT = scanner.nextLine();

//         System.out.print("Nhap tien coc moi: ");
//         double tienCoc = Double.parseDouble(scanner.nextLine());

//         System.out.print("Nhap trang thai moi (Hoc ky hien tai/Da thanh ly/Huy): ");
//         String trangThai = scanner.nextLine();

//         return new HopDongThue(maHD, maSV, maPhong, ngayBD, ngayKT, tienCoc, trangThai);
//     }

//     public void showMessage(String message) {
//         System.out.println(message);
//     }
// }