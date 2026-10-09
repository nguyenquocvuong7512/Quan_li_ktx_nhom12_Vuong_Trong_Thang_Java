package com.example.NguyenQuocVuong_chuong4;

import com.example.NguyenQuocVuong_chuong4.model.HopDong;
import com.example.NguyenQuocVuong_chuong4.model.Khu;
import com.example.NguyenQuocVuong_chuong4.model.NguoiDung;
import com.example.NguyenQuocVuong_chuong4.model.Phong;
import com.example.NguyenQuocVuong_chuong4.model.SinhVien;
import com.example.NguyenQuocVuong_chuong4.model.Tang;
import com.example.NguyenQuocVuong_chuong4.model.ToaNha;
import com.example.NguyenQuocVuong_chuong4.repository.HopDongRepository;
import com.example.NguyenQuocVuong_chuong4.repository.KhuRepository;
import com.example.NguyenQuocVuong_chuong4.repository.PhongRepository;
import com.example.NguyenQuocVuong_chuong4.repository.SinhVienRepository;
import com.example.NguyenQuocVuong_chuong4.repository.TangRepository;
import com.example.NguyenQuocVuong_chuong4.repository.ToaNhaRepository;
import com.example.NguyenQuocVuong_chuong4.service.HopDongService;
import com.example.NguyenQuocVuong_chuong4.service.NguoiDungService;
import com.example.NguyenQuocVuong_chuong4.service.PhongService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:ky_tuc_xa_test;MODE=MySQL;DB_CLOSE_DELAY=-1",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.jpa.hibernate.ddl-auto=create-drop"
})
class NguyenQuocVuongChuong4ApplicationTests {
	@Autowired
	private NguoiDungService nguoiDungService;
	@Autowired
	private KhuRepository khuRepository;
	@Autowired
	private ToaNhaRepository toaNhaRepository;
	@Autowired
	private TangRepository tangRepository;
	@Autowired
	private PhongRepository phongRepository;
	@Autowired
	private SinhVienRepository sinhVienRepository;
	@Autowired
	private HopDongRepository hopDongRepository;
	@Autowired
	private PhongService phongService;
	@Autowired
	private HopDongService hopDongService;

	@Test
	void contextLoads() {
	}

	@Test
	void savesRelatedEntitiesAndAuthenticatesWithHashedPassword() {
		NguoiDung account = new NguoiDung();
		account.setMaNguoiDung("TEST-ND");
		account.setHoTen("Sinh vien kiem thu");
		account.setEmail("student@example.com");
		account.setMatKhau("password123");
		account.setTrangThai("hoat_dong");
		account = nguoiDungService.save(account);

		Khu khu = new Khu();
		khu.setMaKhu("TEST-KHU");
		khu.setTenKhu("Khu kiem thu");
		khu = khuRepository.save(khu);

		ToaNha toaNha = new ToaNha();
		toaNha.setKhu(khu);
		toaNha.setMaToa("TEST-TOA");
		toaNha.setTenToa("Toa kiem thu");
		toaNha.setSoTang(2);
		toaNha = toaNhaRepository.save(toaNha);

		Tang tang = new Tang();
		tang.setToaNha(toaNha);
		tang.setSoTang(1);
		tang = tangRepository.save(tang);

		Phong phong = new Phong();
		phong.setTang(tang);
		phong.setMaPhong("TEST-PHONG");
		phong.setSucChua(2);
		phong.setSoNguoiHienTai(0);
		phong.setLoaiPhong("2 nguoi");
		phong.setGiaPhong(new BigDecimal("1000000.00"));
		phong = phongService.save(phong);

		SinhVien sinhVien = new SinhVien();
		sinhVien.setNguoiDung(account);
		sinhVien.setMaSinhVien("TEST-SV");
		sinhVien = sinhVienRepository.save(sinhVien);

		HopDong hopDong = new HopDong();
		hopDong.setSinhVien(sinhVien);
		hopDong.setPhong(phong);
		hopDong.setNgayBatDau(LocalDate.of(2026, 9, 1));
		hopDong.setNgayKetThuc(LocalDate.of(2027, 6, 30));
		hopDong.setTienPhong(new BigDecimal("1000000.00"));
		hopDong = hopDongService.save(hopDong);

		assertTrue(nguoiDungService.authenticate("student@example.com", "password123").isPresent());
		assertTrue(nguoiDungService.authenticate("TEST-ND", "wrong-password").isEmpty());

		NguoiDung accountUpdate = new NguoiDung();
		accountUpdate.setMaNguoiDung("TEST-ND");
		accountUpdate.setHoTen("Sinh vien da cap nhat");
		accountUpdate.setEmail("student@example.com");
		accountUpdate.setTrangThai("hoat_dong");
		nguoiDungService.update(account.getId(), accountUpdate);
		assertTrue(nguoiDungService.authenticate("student@example.com", "password123").isPresent());

		assertEquals("TEST-SV", hopDongRepository.findById(hopDong.getId())
				.orElseThrow().getSinhVien().getMaSinhVien());
		assertEquals("2 nguoi", phongRepository.findById(phong.getId())
				.orElseThrow().getLoaiPhong());
	}

	@Test
	void rejectsRoomCapacityAndInvalidContractDates() {
		Phong fullRoom = new Phong();
		fullRoom.setSucChua(1);
		fullRoom.setSoNguoiHienTai(2);
		assertThrows(ResponseStatusException.class, () -> phongService.save(fullRoom));

		HopDong invalidContract = new HopDong();
		invalidContract.setNgayBatDau(LocalDate.of(2027, 1, 1));
		invalidContract.setNgayKetThuc(LocalDate.of(2026, 1, 1));
		assertThrows(ResponseStatusException.class, () -> hopDongService.save(invalidContract));
	}
}
