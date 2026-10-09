// package controller;

// import model.ThietBi;
// import service.ThietBiService;
// import view.ThietBiView;
// import java.util.List;

// public class ThietBiController {
//     private ThietBiService service = new ThietBiService();
//     private ThietBiView view = new ThietBiView();

//     public void start() {
//         int choice;
//         do {
//             view.showMenu();
//             choice = view.getChoice();
//             switch (choice) {
//                 case 1:
//                     List<ThietBi> list = service.getAll();
//                     view.showList(list);
//                     break;
//                 case 2:
//                     ThietBi item = view.inputThietBi();
//                     service.add(item);
//                     view.showMessage("Them thiet bi thanh cong!");
//                     break;
//                 case 3:
//                     int updateId = view.inputId("SUA");
//                     ThietBi updatedItem = view.inputThietBiToUpdate(updateId);
//                     if (service.update(updatedItem)) {
//                         view.showMessage("Cap nhat thiet bi thanh cong!");
//                     } else {
//                         view.showMessage("Khong tim thay thiet bi voi ma tren!");
//                     }
//                     break;
//                 case 4:
//                     int deleteId = view.inputId("XOA");
//                     if (service.delete(deleteId)) {
//                         view.showMessage("Xoa thiet bi thanh cong!");
//                     } else {
//                         view.showMessage("Khong tim thay thiet bi voi ma tren!");
//                     }
//                     break;
//                 case 0:
//                     break;
//                 default:
//                     view.showMessage("Lua chon khong hop le!");
//             }
//         } while (choice != 0);
//     }
// }