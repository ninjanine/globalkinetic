package com.globalkinetic.assessment.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "ROLE")
public class Role {
    @NotNull
    @Size(max = 50)
    @Id
    @Column(length = 50)
    private String name;

    public Role() {}

    public String getName() {
        return name;
    }

    public void setName(final String role) {
        this.name = role;
    }
}
