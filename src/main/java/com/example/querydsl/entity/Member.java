package com.example.querydsl.entity;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.Id;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

@Entity
@Getter
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String name;

    protected Member(){

    }

    public Member(String name){
        this.name = name;
    }
}
