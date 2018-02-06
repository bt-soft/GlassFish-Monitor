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
package hu.btsoft.gfmon.engine.model.entity.server;

import hu.btsoft.gfmon.corelib.IGFMonCoreLibConstants;
import hu.btsoft.gfmon.corelib.crypt.CryptUtil;
import hu.btsoft.gfmon.corelib.model.colpos.ColumnPosition;
import hu.btsoft.gfmon.corelib.network.NetworkUtils;
import hu.btsoft.gfmon.engine.model.entity.ModifiableEntityBase;
import hu.btsoft.gfmon.engine.model.entity.application.Application;
import hu.btsoft.gfmon.engine.model.entity.connpool.ConnPool;
import hu.btsoft.gfmon.engine.model.entity.server.snapshot.httpservice.HttpServiceRequest;
import hu.btsoft.gfmon.engine.model.entity.server.snapshot.jvm.JvmMemory;
import hu.btsoft.gfmon.engine.model.entity.server.snapshot.jvm.ThreadSystem;
import hu.btsoft.gfmon.engine.model.entity.server.snapshot.network.ConnectionQueue;
import hu.btsoft.gfmon.engine.model.entity.server.snapshot.network.HttpListener1ConnectionQueue;
import hu.btsoft.gfmon.engine.model.entity.server.snapshot.network.HttpListener1KeepAlive;
import hu.btsoft.gfmon.engine.model.entity.server.snapshot.network.HttpListener1ThreadPool;
import hu.btsoft.gfmon.engine.model.entity.server.snapshot.network.HttpListener2ConnectionQueue;
import hu.btsoft.gfmon.engine.model.entity.server.snapshot.network.HttpListener2KeepAlive;
import hu.btsoft.gfmon.engine.model.entity.server.snapshot.network.HttpListener2ThreadPool;
import hu.btsoft.gfmon.engine.model.entity.server.snapshot.taservice.TransActionService;
import hu.btsoft.gfmon.engine.model.entity.server.snapshot.web.Jsp;
import hu.btsoft.gfmon.engine.model.entity.server.snapshot.web.Request;
import hu.btsoft.gfmon.engine.model.entity.server.snapshot.web.Servlet;
import hu.btsoft.gfmon.engine.model.entity.server.snapshot.web.Session;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
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
@Cacheable(false)
@Table(name = "SERVER",
        catalog = "",
        schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME,
        uniqueConstraints = @UniqueConstraint(columnNames = {"HOST_NAME", "IP_ADDRESS", "PORT_NUM"})
)
@NamedQueries({
    @NamedQuery(name = "Server.findAll", query = "SELECT s FROM Server s ORDER BY s.hostName, s.portNumber"), //
    @NamedQuery(name = "Server.findAllActive", query = "SELECT s FROM Server s WHERE s.active = true ORDER BY s.hostName, s.portNumber"), //
    @NamedQuery(name = "Server.findByMandatoryProperties", query = "SELECT s FROM Server s WHERE s.hostName = :hostName AND s.ipAddress = :ipAddress AND s.portNumber = :portNumber"), //
})
@Data
@ToString(callSuper = true, of = {"hostName", "ipAddress", "portNumber", "active"})
@EqualsAndHashCode(callSuper = true, of = {"hostName", "ipAddress", "portNumber"}) //Az 'active' ne tartozzon bele, mert nem fogjuk megtalálni a UI felületen, ha billegtetjük az állapotát
@NoArgsConstructor
@Slf4j
public class Server extends ModifiableEntityBase {

    /**
     * A monitorozás aktív rá?
     */
    @Column(nullable = false)
    @ColumnPosition(position = 20)
    private Boolean active;

    /**
     * Szerver host neve
     */
    @NotNull(message = "A hostName nem lehet null")
    @Size(min = 5, max = 255, message = "A hostName mező hossza {min} és {max} között lehet")
    @Column(name = "HOST_NAME", length = 255, nullable = false)
    @ColumnPosition(position = 30)
    private String hostName;

    /**
     * A szerver IP címe, ezt programmatikusan töljük majd ki
     */
    @Size(min = 6, max = 255, message = "Az ipAddress mező hossza {min} és {max} között lehet")
    @Column(name = "IP_ADDRESS", length = 255, nullable = true)
    @ColumnPosition(position = 31)
    private String ipAddress;

    /**
     * Port száma
     */
    @NotNull(message = "A portNumber nem lehet null")
    @Min(value = 1024, message = "A portNumber minimális értéke {value}")
    @Max(value = 65535, message = "A portNumber maximális értéke {value}")
    @Column(name = "PORT_NUM", nullable = false)
    @ColumnPosition(position = 32)
    private int portNumber;

    /**
     * Alkalmazások regExp szűrője
     */
    @NotNull(message = "A appRegExpFilter nem lehet null")
    @Column(name = "APP_REGEXP_FILTER", length = 80, nullable = false)
    @ColumnPosition(position = 33)
    private String appRegExpFilter;

    /**
     * UserName
     */
    @Column(name = "USER_NAME", length = 80, nullable = true)
    @ColumnPosition(position = 34)
    private String userName;

    /**
     * UserName
     */
    @Column(name = "PASSWD", length = 80, nullable = true)
    @ColumnPosition(position = 35)
    private String encPasswd;

    /**
     * Leírás
     */
    @Size(max = 255, message = "A description mező hossza maximum {max} lehet")
    @Column(name = "DESCRIPTION")
    @ColumnPosition(position = 36)
    private String description;

    /**
     * Kiegészítő információk
     * (pl.: miért lett tiltva a szerver monitorozása, stb...)
     */
    @Column(name = "TMP_ADDITIONAL_INFO", nullable = true)
    @ColumnPosition(position = 37)
    private String additionalInformation;

    /**
     * REST HTTP token
     * Induláskor töröljük
     */
    @Column(name = "TMP_SESSION_TOKEN", nullable = true)
    @ColumnPosition(position = 38)
    private String sessionToken;

    /**
     * Be van kapcsolva a szerver MonitoringService szolgáltatása?
     * Induláskor töröljük
     */
    @Column(name = "TMP_MONSVCE_RDY", nullable = true)
    @ColumnPosition(position = 39)
    private Boolean monitoringServiceReady;

    /**
     * A szerver mérendő adatai
     * - eager: mindig kell -> mindig felolvassuk
     * - cascade: update, merge menjen rájuk is, ha a szervert töröljük, akkor törlődjönenek az alkalmazások is
     * - orphanRemoval: izomból törlés lesz
     */
    @OneToMany(mappedBy = "server", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServerSvrCollDataUnitJoiner> joiners = new LinkedList<>();

    /**
     * A szerveren milyen alkalmazásk vannak?
     * - eager: mindig kell -> mindig felolvassuk
     * - cascade: update, merge menjen rájuk is, ha a szervert töröljük, akkor törlődjönenek az alkalmazások is
     * - orphanRemoval: izomból törlés lesz
     */
    @OrderBy("appRealName ASC") //JPA nevet kell megadni
    @OneToMany(mappedBy = "server", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @ColumnPosition(position = 70)
    private List<Application> applications = new LinkedList<>();

    /**
     * A szerveren milyen JDBC ConnectionPool-ok vannak?
     * - eager: mindig kell -> mindig felolvassuk
     * - cascade: update, merge menjen rájuk is, ha a szervert töröljük, akkor törlődjönenek az alkalmazások is
     * - orphanRemoval: izomból törlés lesz
     */
    @OrderBy("poolName DESC")//JPA nevet kell megadni
    @OneToMany(mappedBy = "server", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @ColumnPosition(position = 71)
    private List<ConnPool> connPools = new LinkedList<>();

    /**
     * Szerver statisztika mérési eredméynek, visszairány nem kell
     */
    @OneToMany(mappedBy = "server", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HttpServiceRequest> httpServiceRequests = new LinkedList<>();

    @OneToMany(mappedBy = "server", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JvmMemory> jvmMemories = new LinkedList<>();

    @OneToMany(mappedBy = "server", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ThreadSystem> chreadSystems = new LinkedList<>();

    @OneToMany(mappedBy = "server", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConnectionQueue> connectionQueues = new LinkedList<>();

    @OneToMany(mappedBy = "server", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HttpListener1ConnectionQueue> httpListener1ConnectionQueues = new LinkedList<>();

    @OneToMany(mappedBy = "server", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HttpListener1KeepAlive> httpListener1KeepAlives = new LinkedList<>();

    @OneToMany(mappedBy = "server", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HttpListener1ThreadPool> httpListener1ThreadPools = new LinkedList<>();

    @OneToMany(mappedBy = "server", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HttpListener2ConnectionQueue> httpListener2ConnectionQueues = new LinkedList<>();

    @OneToMany(mappedBy = "server", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HttpListener2KeepAlive> httpListener2KeepAlives = new LinkedList<>();

    @OneToMany(mappedBy = "server", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HttpListener2ThreadPool> httpListener2ThreadPools = new LinkedList<>();

    @OneToMany(mappedBy = "server", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransActionService> transActionServices = new LinkedList<>();

    @OneToMany(mappedBy = "server", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Jsp> jsps = new LinkedList<>();

    @OneToMany(mappedBy = "server", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Request> requests = new LinkedList<>();

    @OneToMany(mappedBy = "server", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Servlet> servlets = new LinkedList<>();

    @OneToMany(mappedBy = "server", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Session> sessions = new LinkedList<>();

    /**
     * A kódolatlan jelszó
     * Csak runtime változó, nem tároljuk az adatbázisban
     */
    @Transient
    private String plainPassword;

    /**
     * Konstruktor - csak a sima jelszót lehet kezelni
     *
     * @param hostName        a monitorozandó szerver host neve
     * @param portNumber      IP címe
     * @param description     leírása
     * @param userName        a monitorozó user
     * @param plainPassword   kódolatlan jelszava
     * @param appRegExpFilter alkalmazások reguláris kifejezés filtere
     * @param active          monitorozás aktív rá?
     */
    public Server(String hostName, int portNumber, String description, String userName, String plainPassword, String appRegExpFilter, boolean active) {
        this.hostName = hostName;
        this.portNumber = portNumber;
        this.description = description;
        this.userName = userName;
        this.plainPassword = plainPassword;
        this.appRegExpFilter = appRegExpFilter;
        this.active = active;

        this.ipAddress = NetworkUtils.getIpAddressByHostName(hostName);
    }

    /**
     * Insert előtt
     * - jelszó kódolása
     */
    @PrePersist
    @Override
    protected void prePersist() {
        super.prePersist();
        this.encPasswd = CryptUtil.encrypt(plainPassword);
    }

    /**
     * Update előtt
     * - jelszó kódolása
     */
    @PreUpdate
    @Override
    protected void preUpdate() {
        super.preUpdate();
        if (!StringUtils.isEmpty(plainPassword)) {
            this.encPasswd = CryptUtil.encrypt(plainPassword);
        }
    }

    /**
     * Betöltés után
     * - jelszó dekódolása
     */
    @PostLoad
    @PostUpdate
    protected void post() {
        if (!StringUtils.isEmpty(encPasswd)) {
            plainPassword = CryptUtil.decrypt(this.encPasswd);
        }
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
        return String.format("%s%s:%d", this.getProtocol(), hostName, portNumber);
    }

    /**
     * Protokoll nélküli URL elkérése
     *
     * @return
     */
    public String getSimpleUrl() {
        return String.format("%s:%d", hostName, portNumber);
    }

    /**
     * Van join tábla bejegyzése?
     *
     * @return true -> igen
     */
    public boolean hasJoiners() {
        return joiners != null && !joiners.isEmpty();
    }
}
