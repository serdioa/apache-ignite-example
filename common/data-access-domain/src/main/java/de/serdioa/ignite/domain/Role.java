package de.serdioa.ignite.domain;

import java.io.Serializable;
import java.util.Set;

import de.serdioa.ignite.spring.annotation.IgniteCache;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;


@Data
@Entity(name = "Role")
@Table(name = "t_Role")
@IgniteCache(externalPersistency = true)
public class Role implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 6110457516070749671L;

    @Id
    @Column(name = "Role_Id")
    private Integer id;

    @Column(name = "Role_Name")
    private String roleName;

    @Column(name = "Description")
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @CollectionTable(name = "t_Role_Right", joinColumns =
            @JoinColumn(name = "Role_Id"))
    @Column(name = "Right_Id")
    private Set<Integer> rightIds;
}
