package de.serdioa.ignite.domain;

import java.io.Serializable;

import de.serdioa.ignite.spring.annotation.IgniteCache;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;


@Data
@Entity(name = "Right")
@Table(name = "t_Right")
@IgniteCache(externalPersistency = true)
public class Right implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -3293170784199716727L;

    @Id
    @Column(name = "Right_Id")
    private Integer id;

    @Column(name = "Right_Name")
    private String rightName;

    @Column(name = "Description")
    private String description;
}
