package com.example.backend.entity;


import com.example.backend.Enums.WorkShift;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "time_tables")
public class TimeTable {
    //расписание

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "time_id")
    private long timeId;

    @Enumerated(EnumType.STRING)
    private WorkShift monday;

    @Enumerated(EnumType.STRING)
    private WorkShift tuesday;

    @Enumerated(EnumType.STRING)
    private WorkShift wednesday;

    @Enumerated(EnumType.STRING)
    private WorkShift thursday;

    @Enumerated(EnumType.STRING)
    private WorkShift friday;

    @Enumerated(EnumType.STRING)
    private WorkShift saturday;

    @Enumerated(EnumType.STRING)
    private WorkShift sunday;
}
