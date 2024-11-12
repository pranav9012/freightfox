package com.freightfox.pdf.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.freightfox.pdf.model.Item;

@Service
public class ItemValidator {

    public boolean checkDuplicates(List<Item> items){
        Set<String> st = new HashSet<>();

        List<String> duplicates = items.stream()
                                    .filter(item -> !st.add(item.getName()))
                                    .map(item -> item.getName())
                                    .collect(Collectors.toList());

        return !duplicates.isEmpty();
    }
}
