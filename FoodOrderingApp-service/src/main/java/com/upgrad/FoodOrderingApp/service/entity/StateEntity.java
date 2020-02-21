package com.upgrad.FoodOrderingApp.service.entity;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

//This Class represents the StateEntity table in the DB.

@Entity
@Table(name = "state",uniqueConstraints = {@UniqueConstraint(columnNames = {"uuid"})})
@NamedQueries({

        @NamedQuery(name = "getStateByUuid", query = "SELECT s from StateEntity s where s.stateUuid = :uuid"),
        @NamedQuery(name = "getAllStates",query = "SELECT s from StateEntity s"),
})
public class StateEntity implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(name = "uuid")
    @Size(max = 200)
    @NotNull
    private String stateUuid;


    @Column(name = "state_name")
    @Size(max = 30)
    private String stateName;

    public StateEntity(String stateUuid, String stateName) {
        this.stateUuid = stateUuid;
        this.stateName = stateName;
        return;
    }

    public StateEntity() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStateUuid() {
        return stateUuid;
    }

    public void setStateUuid(String stateUuid) {
        this.stateUuid = stateUuid;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }
}