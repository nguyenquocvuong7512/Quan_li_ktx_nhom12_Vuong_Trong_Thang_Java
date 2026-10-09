// package repository;

// import model.ThietBi;
// import java.util.ArrayList;
// import java.util.List;

// public class ThietBiRepository {
//     private List<ThietBi> listThietBi = new ArrayList<>();

//     public List<ThietBi> getAll() {
//         return listThietBi;
//     }

//     public void save(ThietBi thietBi) {
//         listThietBi.add(thietBi);
//     }

//     public boolean update(ThietBi updatedThietBi) {
//         for (int i = 0; i < listThietBi.size(); i++) {
//             if (listThietBi.get(i).getMaThietBi() == updatedThietBi.getMaThietBi()) {
//                 listThietBi.set(i, updatedThietBi);
//                 return true;
//             }
//         }
//         return false;
//     }

//     public boolean delete(int maThietBi) {
//         for (int i = 0; i < listThietBi.size(); i++) {
//             if (listThietBi.get(i).getMaThietBi() == maThietBi) {
//                 listThietBi.remove(i);
//                 return true;
//             }
//         }
//         return false;
//     }
// }