package com.epam.esm.model;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Objects;

@javax.persistence.Entity
@Table(name = "tag")
public class Tag extends Entity {
    @Column(name = "name")
    private String name;

    public Tag() {
    }

    public Tag(long id, String name) {
        super(id);
        this.name = name;
    }

    public Tag(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Tag tag = (Tag) o;
        return Objects.equals(name, tag.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }

    @Override
    public String toString() {
        return "Tag{" +
                "name='" + name + '\'' +
                '}';
    }
}
