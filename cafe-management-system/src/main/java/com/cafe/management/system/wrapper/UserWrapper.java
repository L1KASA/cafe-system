package com.cafe.management.system.wrapper;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserWrapper {

    private Integer id;

    private String name;

    private String contactNumber;

    private String email;

    private String status;

}
