package com.example.NguyenQuocVuong_chuong4.service;

import com.example.NguyenQuocVuong_chuong4.model.NguoiDung;
import com.example.NguyenQuocVuong_chuong4.repository.NguoiDungRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class NguoiDungService {
    private static final Pattern BCRYPT_HASH =
            Pattern.compile("^\\$2[aby]\\$\\d{2}\\$[./A-Za-z0-9]{53}$");

    private final NguoiDungRepository repository;
    private final BCryptPasswordEncoder passwordEncoder;

    public NguoiDungService(NguoiDungRepository repository, BCryptPasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<NguoiDung> getAll() {
        return repository.findAll();
    }

    public NguoiDung getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tài khoản"));
    }

    public NguoiDung save(NguoiDung account) {
        if (account.getMatKhau() == null || account.getMatKhau().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu không được để trống");
        }
        account.setMatKhau(hashPassword(account.getMatKhau()));
        return repository.save(account);
    }

    public NguoiDung update(Long id, NguoiDung account) {
        NguoiDung existing = getById(id);
        account.setId(id);
        String password = account.getMatKhau();
        account.setMatKhau(password == null || password.isBlank()
                ? existing.getMatKhau()
                : hashPassword(password));
        return repository.save(account);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tài khoản");
        }
        repository.deleteById(id);
    }

    public Optional<NguoiDung> authenticate(String identifier, String rawPassword) {
        return repository.findByEmailIgnoreCaseOrMaNguoiDung(identifier, identifier)
                .filter(account -> "hoat_dong".equalsIgnoreCase(account.getTrangThai()))
                .filter(account -> isBcryptHash(account.getMatKhau())
                        && passwordEncoder.matches(rawPassword, account.getMatKhau()));
    }

    private String hashPassword(String password) {
        return isBcryptHash(password) ? password : passwordEncoder.encode(password);
    }

    private boolean isBcryptHash(String value) {
        return value != null && BCRYPT_HASH.matcher(value).matches();
    }
}
