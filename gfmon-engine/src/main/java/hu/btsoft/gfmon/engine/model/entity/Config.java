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

import hu.btsoft.gfmon.engine.config.ConfigValueType;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import hu.btsoft.gfmon.engine.IGFMonEngineConstants;

/**
 * Beéllítások entitás
 *
 * @author BT
 */
@Entity
@Cacheable(false)
@Table(name = "CONFIG", catalog = "", schema = IGFMonEngineConstants.DATABASE_SCHEMAN_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@NamedQueries({
    @NamedQuery(name = "Config.findByKeyNames", query = "SELECT c FROM Config c WHERE c.keyName = :keyName AND c.subKeyName = :subKeyName"),//
})
public class Config extends ModifiableEntityBase {

    /**
     * Konfig class
     */
    @NotNull(message = "A keyName nem lehet null")
    @Column(name = "KEY_NAME", length = 30, nullable = false)
    @XmlElement(required = true)
    private String keyName;

    /**
     * Konfig neve
     */
    @NotNull(message = "A subKeyName nem lehet null")
    @Column(name = "SUBKEY_NAME", length = 30, nullable = false)
    @XmlElement(required = true)
    private String subKeyName;

    /**
     * Konfig érték típusa
     */
    @NotNull(message = "A valueType nem lehet null")
    @Column(name = "VALUE_TYPE", length = 2, nullable = false)
    @XmlElement(required = true)
    @Enumerated(EnumType.STRING)
    private ConfigValueType valueType;

    /**
     * Konfig értéke
     */
    @Column(name = "KEY_VALUE", length = 1024, nullable = true)
    @XmlElement(required = false)
    private String keyValue;
}
