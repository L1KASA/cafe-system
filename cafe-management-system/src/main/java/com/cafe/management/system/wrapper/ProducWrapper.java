package com.cafe.management.system.wrapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProducWrapper {

    private Integer id;

    private String name;

    private String description;

    private Integer price;

    private String status;

    private Integer categoryId;

    private String categoryName;

    public ProducWrapper(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public ProducWrapper(Integer id, String name, String description, Integer price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }


}
