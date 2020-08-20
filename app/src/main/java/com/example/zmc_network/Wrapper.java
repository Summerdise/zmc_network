package com.example.zmc_network;

import java.util.ArrayList;
import java.util.List;

public class Wrapper {
    public ArrayList<Person> personList;

    public Wrapper(List<Person> personList) {
        this.personList = new ArrayList<>(personList);
    }
}
