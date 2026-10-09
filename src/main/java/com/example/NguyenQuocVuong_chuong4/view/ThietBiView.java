// package view;

// import model.ThietBi;
// import java.util.List;
// import java.util.Scanner;

// public class ThietBiView {
//     private Scanner scanner = new Scanner(System.in);

//     public void showMenu() {
//         System.out.println("\n=== QUAN LY THIET BI ===");
//         System.out.println("1. Xem danh sach thiet bi");
//         System.out.println("2. Them thiet bi moi");
//         System.out.println("3. Sua thong tin thiet bi");
//         System.out.println("4. Xoa thiet bi");
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

//     public void showList(List<ThietBi> list) {
//         System.out.println("\n=== DANH SACH THIET BI ===");
//         if (list == null || list.isEmpty()) {
//             System.out.println("Danh sach thiet bi dang trong.");
//             return;
//         }
//         for (ThietBi item : list) {
//             System.out.println(item);
//         }
//     }

//     public ThietBi inputThietBi() {
//         System.out.println("\n=== THEM THIET BI MOI ===");
//         System.out.print("Nhap ma thiet bi: ");
//         int ma = Integer.parseInt(scanner.nextLine());
//         System.out.print("Nhap ten thiet bi: ");
//         String ten = scanner.nextLine();
//         System.out.print("Nhap don gia: ");
//         double donGia = Double.parseDouble(scanner.nextLine());
//         return new ThietBi(ma, ten, donGia);
//     }

//     public int inputId(String actionName) {
//         System.out.print("\nNhap ma thiet bi can " + actionName + ": ");
//         return Integer.parseInt(scanner.nextLine());
//     }

//     public ThietBi inputThietBiToUpdate(int ma) {
//         System.out.println("--- NHAP THONG TIN MOI ---");
//         System.out.print("Nhap ten thiet bi moi: ");
//         String ten = scanner.nextLine();
//         System.out.print("Nhap don gia moi: ");
//         double donGia = Double.parseDouble(scanner.nextLine());
//         return new ThietBi(ma, ten, donGia);
//     }

//     public void showMessage(String message) {
//         System.out.println(message);
//     }
// }