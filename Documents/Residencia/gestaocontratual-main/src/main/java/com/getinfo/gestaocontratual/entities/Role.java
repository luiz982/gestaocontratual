package com.getinfo.gestaocontratual.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @Column(name = "roleid")
    private  Long roleID;

    private String name;

    public Long getRoleID() {
        return roleID;
    }

    public void setRoleID(Long roleID) {
        this.roleID = roleID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public enum Values {
        BASIC("BASIC"),
        ADMIN("ADMIN");

        private final String name;

        Values(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
