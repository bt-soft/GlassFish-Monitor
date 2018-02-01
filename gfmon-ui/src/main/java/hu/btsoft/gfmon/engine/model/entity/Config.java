/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    Config.java
 *  Created: 2017.12.23. 14:43:35
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.entity;

import hu.btsoft.gfmon.corelib.IGFMonCoreLibConstants;
import hu.btsoft.gfmon.corelib.model.colpos.ColumnPosition;
import hu.btsoft.gfmon.engine.config.ConfigValueType;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Beéllítások entitás
 *
 * @author BT
 */
@Entity
@Cacheable(false)
@Table(name = "CONFIG", catalog = "",
        schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME,
        uniqueConstraints = @UniqueConstraint(columnNames = {"KEYCLASS_NAME", "KEY_NAME", "VALUE_TYPE"}))
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@NamedQueries({
    @NamedQuery(name = "Config.findByKeyNames", query = "SELECT c FROM Config c WHERE c.keyClassName = :keyClassName AND c.keyName = :keyName"),//
})
public class Config extends ModifiableEntityBase {

    /**
     * Konfig class
     */
    @NotNull(message = "A keyClassName nem lehet null")
    @Column(name = "KEYCLASS_NAME", length = 30, nullable = false)
    @XmlElement(required = true)
    @ColumnPosition(position = 30)
    private String keyClassName;

    /**
     * Konfig neve
     */
    @NotNull(message = "A keyName nem lehet null")
    @Column(name = "KEY_NAME", length = 30, nullable = false)
    @XmlElement(required = true)
    @ColumnPosition(position = 31)
    private String keyName;

    /**
     * Konfig érték típusa
     */
    @NotNull(message = "A valueType nem lehet null")
    @Column(name = "VALUE_TYPE", length = 2, nullable = false)
    @XmlElement(required = true)
    @Enumerated(EnumType.STRING)
    @ColumnPosition(position = 32)
    private ConfigValueType valueType;

    /**
     * Konfig értéke
     */
    @Column(name = "KEY_VALUE", length = 1024, nullable = true)
    @XmlElement(required = false)
    @ColumnPosition(position = 33)
    private String keyValue;
}
