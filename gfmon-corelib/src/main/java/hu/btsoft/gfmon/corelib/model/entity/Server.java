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
package hu.btsoft.gfmon.corelib.model.entity;

import hu.btsoft.gfmon.corelib.IGFMonCoreLibConstants;
import hu.btsoft.gfmon.corelib.crypt.CryptUtil;
import hu.btsoft.gfmon.corelib.model.colpos.ColumnPosition;
import hu.btsoft.gfmon.corelib.network.NetworkUtils;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PostLoad;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Monitorozott GF szerverek adatai
 *
 * @author BT
 */
@Entity
@Table(name = "SERVER",
        catalog = "",
        schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME,
        uniqueConstraints = @UniqueConstraint(columnNames = {"HOST_NAME", "IP_ADDRESS", "PORT_NUM"})
)
@NamedQueries({
    @NamedQuery(name = "Server.findAllActive", query = "SELECT s FROM Server s WHERE s.active = true"), //
})
@Cacheable(false)
@Data
@ToString(callSuper = false, of = {"hostName", "ipAddress", "portNumber", "active"})
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Slf4j
public class Server extends ModifiableEntityBase {

    /**
     * A monitorozás aktív rá?
     */
    @Column(nullable = false)
    @ColumnPosition(position = 10)
    private boolean active;

    /**
     * Szerver host neve
     */
    @NotNull(message = "A hostName nem lehet null")
    @Size(min = 5, max = 255, message = "A hostName mező hossza {min} és {max} között lehet")
    @Column(name = "HOST_NAME", length = 255, nullable = false)
    @ColumnPosition(position = 11)
    private String hostName;

    /**
     * A szerver IP címe, ezt programmatikusan töljük majd ki
     */
    @Size(min = 6, max = 255, message = "Az ipAddress mező hossza {min} és {max} között lehet")
    @Column(name = "IP_ADDRESS", length = 255, nullable = true)
    @ColumnPosition(position = 12)
    private String ipAddress;

    /**
     * Port száma
     */
    @NotNull(message = "A portNumber nem lehet null")
    @Min(value = 1024, message = "A portNumber minimális értéke {value}")
    @Max(value = 65535, message = "A portNumber maximális értéke {value}")
    @Column(name = "PORT_NUM", nullable = false)
    @ColumnPosition(position = 13)
    private int portNumber;

    /**
     * UserName
     */
    @Column(length = 80, nullable = true)
    @ColumnPosition(position = 14)
    private String userName;

    /**
     * UserName
     */
    @Column(name = "PASSWD", length = 80, nullable = true)
    @ColumnPosition(position = 15)
    private String encPasswd;

    /**
     * Leírás
     */
    @Size(max = 255, message = "A description mező hossza maximum {max} lehet")
    @ColumnPosition(position = 16)
    private String description;

    /**
     * Kiegészítő információk
     * (pl.: miért lett tiltva a szerver monitorozása, stb...)
     */
    @Column(name = "ADDITIONAL_INFO", nullable = true)
    @ColumnPosition(position = 17)
    private String additionalInformation;

    /**
     * REST HTTP token
     * Induláskor töröljük
     */
    @Column(name = "TMP_SESSION_TOKEN", nullable = true)
    @ColumnPosition(position = 18)
    private String sessionToken;

    /**
     * Be van kapcsolva a szerver MonitoringService szolgáltatása?
     * Induláskor töröljük
     */
    @Column(name = "TMP_MONSVCE_RDY", nullable = true)
    @ColumnPosition(position = 19)
    private Boolean monitoringServiceReady;

//    /**
//     * A szerver monitorozható moduljainak halmaza
//     * Csak runtime változó, nem tároljuk az adatbázisban
//     */
//    @Transient
//    private Set<String/*GF MonitoringService module name*/> monitorableModules;
//
//    /**
//     * A szerver mérési eredményei
//     */
//    @OneToMany(mappedBy = "server")
//    private List<SnapshotBase> snapshots;
//
    /**
     * A kódolatlan jelszó
     * Csak runtime változó, nem tároljuk az adatbázisban
     */
    @Transient
    private String plainPassword;

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

        this.ipAddress = NetworkUtils.getIpAddressByHostName(hostName);
    }

    /**
     * Jelszó kezelése
     * mentés előtt kódolunk egyet
     */
    @PrePersist
    @PreUpdate
    protected void pre() {
        this.encPasswd = CryptUtil.encrypt(plainPassword);
    }

    /**
     * Jelszó kezelése
     * beolvasás után dekódolunk egyet
     */
    @PostLoad
    @PostUpdate
    protected void post() {
        plainPassword = CryptUtil.decrypt(this.encPasswd);
    }

    /**
     * Ha ki van töltve az user, akkor biztosan HTTPS a protokol
     *
     * @return http/https
     */
    public String getProtocol() {
        return StringUtils.isEmpty(userName) ? IGFMonCoreLibConstants.PROTOCOL_HTTP : IGFMonCoreLibConstants.PROTOCOL_HTTPS;
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
