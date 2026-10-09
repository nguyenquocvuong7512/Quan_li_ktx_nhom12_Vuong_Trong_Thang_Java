// package view;

// import model.Room;
// import java.util.List;
// import java.util.Scanner;

// public class RoomView {
//     private Scanner scanner = new Scanner(System.in);

//     public void showMenu() {
//         System.out.println("\n=== QUAN LY PHONG KI TUC XA ===");
//         System.out.println("1. Xem danh sach phong");
//         System.out.println("2. Them phong moi");
//         System.out.println("3. Sua thong tin phong");
//         System.out.println("4. Xoa phong");
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

//     public void showRooms(List<Room> rooms) {
//         System.out.println("\n=== DANH SACH PHONG KI TUC XA ===");
//         if (rooms == null || rooms.isEmpty()) {
//             System.out.println("Danh sach phong dang trong.");
//             return;
//         }
//         for (Room room : rooms) {
//             System.out.println(room);
//         }
//     }

//     public Room inputRoom() {
//         System.out.println("\n=== THEM PHONG MOI ===");
//         System.out.print("Nhap ma phong: ");
//         int maPhong = Integer.parseInt(scanner.nextLine());
//         System.out.print("Nhap ten phong: ");
//         String tenPhong = scanner.nextLine();
//         System.out.print("Nhap ma toa nha: ");
//         int maToaNha = Integer.parseInt(scanner.nextLine());
//         System.out.print("Nhap loai phong (Nam/Nu): ");
//         String loaiPhong = scanner.nextLine();
//         System.out.print("Nhap suc chua: ");
//         int sucChua = Integer.parseInt(scanner.nextLine());
//         System.out.print("Nhap so luong hien tai: ");
//         int soLuongHienTai = Integer.parseInt(scanner.nextLine());
//         System.out.print("Nhap gia thue: ");
//         double giaThue = Double.parseDouble(scanner.nextLine());
//         System.out.print("Nhap trang thai (Con trong/Day/Dang sua chua): ");
//         String trangThai = scanner.nextLine();
//         return new Room(maPhong, tenPhong, maToaNha, loaiPhong, sucChua, soLuongHienTai, giaThue, trangThai);
//     }

//     public int inputId(String actionName) {
//         System.out.print("\nNhap ma phong can " + actionName + ": ");
//         return Integer.parseInt(scanner.nextLine());
//     }

//     public Room inputRoomToUpdate(int maPhong) {
//         System.out.println("--- NHAP THONG TIN MOI ---");
//         System.out.print("Nhap ten phong moi: ");
//         String tenPhong = scanner.nextLine();
//         System.out.print("Nhap ma toa nha moi: ");
//         int maToaNha = Integer.parseInt(scanner.nextLine());
//         System.out.print("Nhap loai phong moi (Nam/Nu): ");
//         String loaiPhong = scanner.nextLine();
//         System.out.print("Nhap suc chua moi: ");
//         int sucChua = Integer.parseInt(scanner.nextLine());
//         System.out.print("Nhap so luong hien tai moi: ");
//         int soLuongHienTai = Integer.parseInt(scanner.nextLine());
//         System.out.print("Nhap gia thue moi: ");
//         double giaThue = Double.parseDouble(scanner.nextLine());
//         System.out.print("Nhap trang thai moi (Con trong/Day/Dang sua chua): ");
//         String trangThai = scanner.nextLine();
//         return new Room(maPhong, tenPhong, maToaNha, loaiPhong, sucChua, soLuongHienTai, giaThue, trangThai);
//     }

//     public void showMessage(String message) {
//         System.out.println(message);
//     }

//     public void closeScanner() {
//         scanner.close();
//     }
// }