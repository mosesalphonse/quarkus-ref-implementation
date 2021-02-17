package org.sash.hibernate.orm;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "known_countries")
@NamedQuery(name = "Countries.findAll", query = "SELECT f FROM Country f ORDER BY f.name", hints = @QueryHint(name = "org.hibernate.cacheable", value = "true"))
@Cacheable
public class Country {

    @Id
    @SequenceGenerator(name = "countriesSequence", sequenceName = "known_countries_id_seq", allocationSize = 1, initialValue = 10)
    @GeneratedValue(generator = "countriesSequence")
    private Integer id;

    @Column(length = 40, unique = true)
    private String name;

    public Country() {
    }

    public Country(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
