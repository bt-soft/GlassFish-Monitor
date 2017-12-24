/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    Server.java
 *  Created: 2017.12.23. 15:33:50
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.entity;

import hu.btsoft.gfmon.engine.IGFMonEngineConstants;
import java.util.List;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Monitorozott GF szerverek adatai
 *
 * @author BT
 */
@Entity
@Cacheable(false)
@Table(name = "SERVER", catalog = "", schema = IGFMonEngineConstants.DATABASE_SCHEMAN_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Slf4j
public class Server extends ModifiableEntityBase {

    /**
     * Szerver host neve
     */
    @NotNull(message = "A hostName nem lehet null")
    @Size(min = 5, max = 255, message = "A hostName mező hossza {min} és {max} között lehet")
    @Column(name = "HOST_NAME", length = 255, nullable = false, unique = true)
    @XmlElement(required = true)
    private String hostName;

    /**
     * A szerver IP címe, ezt programmatikusan töljük majd ki
     */
    @Size(min = 5, max = 255, message = "A ipAddress mező hossza {min} és {max} között lehet")
    @Column(name = "IP_ADDRESS", length = 255, nullable = true, unique = true)
    @XmlElement(required = true)
    private String ipAddress;

    /**
     * Port száma
     */
    @NotNull(message = "A portNumber nem lehet null")
    @Min(value = 1024, message = "A portNumber minimális értéke {value}")
    @Max(value = 65535, message = "A portNumber maximális értéke {value}")
    @Column(name = "PORT_NUM", nullable = false)
    private int portNumber;

    /**
     * Leírás
     */
    @Size(max = 255, message = "A description mező hossza maximum {max} lehet")
    private String description;

    /**
     * UserName
     */
    @Column(length = 80, nullable = true)
    @XmlElement(required = true)
    private String userName;

    /**
     * UserName
     */
    @Column(name = "PASSWD", length = 80, nullable = true)
    @XmlElement(required = true)
    private String encPasswd;

    /**
     * A monitorozás aktív rá?
     */
    @Column(nullable = false)
    @XmlElement(required = true)
    private boolean active;

    /**
     * Kiegészítő információk
     * (pl.: miért lett tiltva a szerver monitorozása, stb...)
     */
    @Column(name = "comment")
    @XmlElement(required = false)
    private String comment;

    /**
     * A kódolatlan jelszó
     * Csak runtime változó, nem tároljuk az adatbázisban
     */
    @Transient
    @XmlTransient
    private String plainPassword;

    /**
     * REST HTTP token
     * Csak runtime változó, nem tároljuk az adatbázisban
     */
    @Transient
    @XmlTransient
    private String sessionToken;

    /**
     * A szerver mérési eredményei
     */
    @OneToMany(mappedBy = "server")
    private List<Snapshot> snapshots;

    /**
     * Konstruktor - csak a sima jelszót lehet kezelni
     *
     * @param hostName      a monitorozandó szerver host neve
     * @param portNumber    IP címe
     * @param description   leírása
     * @param userName      a monitorozó user
     * @param plainPassword kódolatlan jelszava
     * @param active        monitorozás aktív rá?
     */
    public Server(String hostName, int portNumber, String description, String userName, String plainPassword, boolean active) {
        this.hostName = hostName;
        this.portNumber = portNumber;
        this.description = description;
        this.userName = userName;
        this.plainPassword = plainPassword;
        this.active = active;
    }

    /**
     * Jelszó kezelése
     * mentés előtt kódolunk egyet
     */
    @PrePersist
    @PreUpdate
    protected void pre() {
        this.encPasswd = plainPassword; //Crypter.cryptPasswd(plainPassword);
    }

    /**
     * Jelszó kezelése
     * beolvasás után dekódolunk egyet
     */
    @PostLoad
    @PostUpdate
    protected void post() {
        plainPassword = encPasswd; //Crypter.deCryptPasswd(this.encPasswd);
    }

    /**
     * Ha ki van töltve az user, akkor biztosan HTTPS a protokol
     *
     * @return http/https
     */
    public String getProtocol() {
        return StringUtils.isEmpty(userName) ? IGFMonEngineConstants.PROTOCOL_HTTP : IGFMonEngineConstants.PROTOCOL_HTTPS;
    }

    /**
     * URL lekérése
     *
     * @return http(s)://server:port
     */
    public String getUrl() {
        return String.format("%s%s:%d", getProtocol(), hostName, portNumber);
    }

    /**
     * Protokoll nélküli URL elkérése
     *
     * @return
     */
    public String getSimpleUrl() {
        return String.format("%s:%d", hostName, portNumber);
    }

}
