package de.serdioa.ignite.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
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
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;


@Data
@Entity(name = "User")
@Table(name = "t_User")
@IgniteCache(externalPersistency = true)
public class User implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 3425798827221779688L;

    @Id
    @Column(name = "User_Id")
    private Integer id;

    @Column(name = "Username")
    @QuerySqlField(index = true)
    private String username;

    @Column(name = "Password")
    private String password;

    @Column(name = "Password_Changed_On")
    private ZonedDateTime passwordChangedOn;

    @Column(name = "Expire_On")
    private LocalDate expireOn;

    @Column(name = "Locked")
    @QuerySqlField
    private Boolean locked;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @CollectionTable(name = "t_User_Role", joinColumns =
            @JoinColumn(name = "User_Id"))
    @Column(name = "Role_Id")
    @QuerySqlField
    private Set<Integer> roleIds;
}
