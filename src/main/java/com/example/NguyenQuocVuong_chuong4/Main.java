// import controller.RoomController;
// import controller.ToaNhaController;
// import controller.ThietBiController;
// import controller.SinhVienController;
// import controller.HopDongThueController;
// import java.util.Scanner;

// public class Main {
//     public static void main(String[] args) {
//         Scanner scanner = new Scanner(System.in);
//         RoomController roomController = new RoomController();
//         ToaNhaController toaNhaController = new ToaNhaController();
//         ThietBiController thietBiController = new ThietBiController();
//         SinhVienController sinhVienController = new SinhVienController();
//         HopDongThueController hopDongThueController = new HopDongThueController();

//         int mainChoice;
//         do {
//             System.out.println("\n==================================");
//             System.out.println("   HE THONG QUAN LY KI TUC XA");
//             System.out.println("==================================");
//             System.out.println("CHON DANH MUC BANG CAN QUAN LY:");
//             System.out.println("1. Quan ly Toa nha");
//             System.out.println("2. Quan ly Phong o");
//             System.out.println("3. Quan ly Thiet bi");
//             System.out.println("4. Quan ly Sinh vien");
//             System.out.println("5. Quan ly Hop dong thue");
//             System.out.println("0. Thoat chuong trinh");
//             System.out.print("Lua chon cua ban: ");

//             try {
//                 mainChoice = Integer.parseInt(scanner.nextLine());
//             } catch (NumberFormatException e) {
//                 mainChoice = -1;
//             }

//             switch (mainChoice) {
//                 case 1:
//                     toaNhaController.start();
//                     break;
//                 case 2:
//                     roomController.start();
//                     break;
//                 case 3:
//                     thietBiController.start();
//                     break;
//                 case 4:
//                     sinhVienController.start();
//                     break;
//                 case 5:
//                     hopDongThueController.start();
//                     break;
//                 case 0:
//                     System.out.println("Cam on ban da su dung chuong trinh!");
//                     break;
//                 default:
//                     System.out.println("Lua chon khong hop le, vui long thu lai!");
//             }
//         } while (mainChoice != 0);
//     }
// }