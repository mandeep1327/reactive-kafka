package com.example.demo.spring;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;

import javax.persistence.Table;
import javax.persistence.Transient;

@Table
@Data
@Builder
@AllArgsConstructor
public class Shipment implements Persistable<Integer> {

    @Id
    @Column("id")
    private Integer id;

    private String name;
    @Override
    public Integer getId() {
        return this.id;
    }

    @Override
    @Transient
    public boolean isNew() {
        return id==null;
    }
}
