// package view;

// import model.ToaNha;
// import java.util.List;
// import java.util.Scanner;

// public class ToaNhaView {
//     private Scanner scanner = new Scanner(System.in);

//     public void showMenu() {
//         System.out.println("\n=== QUAN LY TOA NHA ===");
//         System.out.println("1. Xem danh sach toa nha");
//         System.out.println("2. Them toa nha moi");
//         System.out.println("3. Sua thong tin toa nha");
//         System.out.println("4. Xoa toa nha");
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

//     public void showList(List<ToaNha> list) {
//         System.out.println("\n=== DANH SACH TOA NHA ===");
//         if (list == null || list.isEmpty()) {
//             System.out.println("Danh sach toa nha dang trong.");
//             return;
//         }
//         for (ToaNha item : list) {
//             System.out.println(item);
//         }
//     }

//     public ToaNha inputToaNha() {
//         System.out.println("\n=== THEM TOA NHA MOI ===");
//         System.out.print("Nhap ma toa nha: ");
//         int ma = Integer.parseInt(scanner.nextLine());
//         System.out.print("Nhap ten toa nha: ");
//         String ten = scanner.nextLine();
//         System.out.print("Nhap so tang: ");
//         int soTang = Integer.parseInt(scanner.nextLine());
//         System.out.print("Nhap ghi chu: ");
//         String ghiChu = scanner.nextLine();
//         return new ToaNha(ma, ten, soTang, ghiChu);
//     }

//     public int inputId(String actionName) {
//         System.out.print("\nNhap ma toa nha can " + actionName + ": ");
//         return Integer.parseInt(scanner.nextLine());
//     }

//     public ToaNha inputToaNhaToUpdate(int ma) {
//         System.out.println("--- NHAP THONG TIN MOI ---");
//         System.out.print("Nhap ten toa nha moi: ");
//         String ten = scanner.nextLine();
//         System.out.print("Nhap so tang moi: ");
//         int soTang = Integer.parseInt(scanner.nextLine());
//         System.out.print("Nhap ghi chu moi: ");
//         String ghiChu = scanner.nextLine();
//         return new ToaNha(ma, ten, soTang, ghiChu);
//     }

//     public void showMessage(String message) {
//         System.out.println(message);
//     }
// }