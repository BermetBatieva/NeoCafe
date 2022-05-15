package com.example.backend.entity;

import com.example.backend.Enums.Status;
import com.example.backend.Enums.TableStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data

@Entity
@javax.persistence.Table(name = "tables")
public class Tables {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TableStatus tableStatus;

    @Column(name = "qr_code")
    private String qrCode;
    
    @ManyToOne
    private Branches branches;
    
    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private Status status;
}
