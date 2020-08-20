package com.example.zmc_network;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Person {
    @NonNull
    @PrimaryKey
    public String name;

    public String avatar;
}
