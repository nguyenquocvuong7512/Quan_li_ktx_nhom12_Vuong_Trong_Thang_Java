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
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tang", uniqueConstraints = @UniqueConstraint(
        name = "uq_tang_toa_so", columnNames = {"toa_nha_id", "so_tang"}))
@Getter
@Setter
@NoArgsConstructor
public class Tang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "toa_nha_id", nullable = false)
    @JsonIgnoreProperties({"khu"})
    private ToaNha toaNha;

    @NotNull
    @Positive
    @Column(name = "so_tang", nullable = false)
    private Integer soTang;
}
