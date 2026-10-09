package com.example.NguyenQuocVuong_chuong4.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "toa_nha")
@Getter
@Setter
@NoArgsConstructor
public class ToaNha {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "khu_id", nullable = false)
    @JsonIgnoreProperties({"moTa"})
    private Khu khu;

    @Size(max = 20)
    @NotBlank
    @Column(name = "ma_toa", nullable = false, unique = true, length = 20)
    private String maToa;

    @NotBlank
    @Size(max = 100)
    @Column(name = "ten_toa", nullable = false, length = 100)
    private String tenToa;

    @NotNull
    @Positive
    @Column(name = "so_tang", nullable = false)
    private Integer soTang = 1;

    @Column(columnDefinition = "TEXT")
    private String moTa;
}
