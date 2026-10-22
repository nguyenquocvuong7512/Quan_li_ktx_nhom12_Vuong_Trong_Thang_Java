package com.example.NguyenQuocVuong_chuong4.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebViewController {

    @GetMapping({"/", "/dashboard"})
    public String index() {
        return "forward:/index.html";
    }

    @GetMapping("/login")
    public String login() {
        return "forward:/login.html";
    }

    @GetMapping("/sinh-vien")
    public String sinhVien() {
        return "forward:/sinh-vien.html";
    }

    @GetMapping("/tai-khoan")
    public String taiKhoan() {
        return "forward:/tai-khoan.html";
    }

    @GetMapping("/toa-nha")
    public String toaNha() {
        return "forward:/toa-nha.html";
    }

    @GetMapping("/phong")
    public String phong() {
        return "forward:/phong.html";
    }

    @GetMapping("/loai-phong")
    public String loaiPhong() {
        return "forward:/loai-phong.html";
    }

    @GetMapping("/hop-dong")
    public String hopDong() {
        return "forward:/hop-dong.html";
    }

    @GetMapping("/dang-ky-phong")
    public String dangKyPhong() {
        return "forward:/dang-ky-phong.html";
    }

    @GetMapping("/sinh-vien-phong")
    public String sinhVienPhong() {
        return "forward:/sinh-vien-phong.html";
    }
}
