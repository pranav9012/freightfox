package com.freightfox.pdf.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class Item {
    private String name;
    private String quantity;
    private String rate;
    private String amount;
}
