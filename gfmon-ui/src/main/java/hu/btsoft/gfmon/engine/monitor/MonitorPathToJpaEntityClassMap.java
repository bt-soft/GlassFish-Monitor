/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    MonitorPathToJpaEntityClassMap.java
 *  Created: 2018.01.06. 10:40:13
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor;

import hu.btsoft.gfmon.engine.model.entity.server.snapshot.SvrSnapshotBase;
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
import hu.btsoft.gfmon.engine.monitor.collector.server.httpservice.HttpServiceRequestCollector;
import hu.btsoft.gfmon.engine.monitor.collector.server.jvm.MemoryColletor;
import hu.btsoft.gfmon.engine.monitor.collector.server.jvm.ThreadSystemCollector;
import hu.btsoft.gfmon.engine.monitor.collector.server.network.ConnectionQueueCollector;
import hu.btsoft.gfmon.engine.monitor.collector.server.network.HttpListener1ConnectionQueueCollector;
import hu.btsoft.gfmon.engine.monitor.collector.server.network.HttpListener1KeepAliveCollector;
import hu.btsoft.gfmon.engine.monitor.collector.server.network.HttpListener1ThreadPoolCollector;
import hu.btsoft.gfmon.engine.monitor.collector.server.network.HttpListener2ConnectionQueueCollector;
import hu.btsoft.gfmon.engine.monitor.collector.server.network.HttpListener2KeepAliveCollector;
import hu.btsoft.gfmon.engine.monitor.collector.server.network.HttpListener2ThreadPoolCollector;
import hu.btsoft.gfmon.engine.monitor.collector.server.taservice.TransActionServiceColletor;
import hu.btsoft.gfmon.engine.monitor.collector.server.web.JspColletor;
import hu.btsoft.gfmon.engine.monitor.collector.server.web.RequestColletor;
import hu.btsoft.gfmon.engine.monitor.collector.server.web.ServletColletor;
import hu.btsoft.gfmon.engine.monitor.collector.server.web.SessionCollector;
import lombok.extern.slf4j.Slf4j;

/**
 * Monitorozott URL -> JPA entitás osztály típus map
 *
 * @author BT
 */
@Slf4j
public class MonitorPathToJpaEntityClassMap {

    /**
     * Monitor path
     *
     * @param path monitor path
     *
     * @return JPA entitás osztály típus
     */
    public static Class<? extends SvrSnapshotBase> getJpaEntityClass(String path) {

        Class<? extends SvrSnapshotBase> clazz = null;

        //A JPA entitás típusát attól függően azonosítjuk, hogy mely path-ról származik a mérés
        switch (path) {

            case HttpServiceRequestCollector.PATH:
                clazz = HttpServiceRequest.class;
                break;

            case MemoryColletor.PATH:
                clazz = JvmMemory.class;
                break;

            case ThreadSystemCollector.PATH:
                clazz = ThreadSystem.class;
                break;

            case ConnectionQueueCollector.PATH:
                clazz = ConnectionQueue.class;
                break;

            case HttpListener1ConnectionQueueCollector.PATH:
                clazz = HttpListener1ConnectionQueue.class;
                break;

            case HttpListener1KeepAliveCollector.PATH:
                clazz = HttpListener1KeepAlive.class;
                break;

            case HttpListener1ThreadPoolCollector.PATH:
                clazz = HttpListener1ThreadPool.class;
                break;

            case HttpListener2ConnectionQueueCollector.PATH:
                clazz = HttpListener2ConnectionQueue.class;
                break;

            case HttpListener2KeepAliveCollector.PATH:
                clazz = HttpListener2KeepAlive.class;
                break;

            case HttpListener2ThreadPoolCollector.PATH:
                clazz = HttpListener2ThreadPool.class;
                break;

            case TransActionServiceColletor.PATH:
                clazz = TransActionService.class;
                break;

            case JspColletor.PATH:
                clazz = Jsp.class;
                break;

            case RequestColletor.PATH:
                clazz = Request.class;
                break;

            case ServletColletor.PATH:
                clazz = Servlet.class;
                break;

            case SessionCollector.PATH:
                clazz = Session.class;
                break;

            default:
                log.error("A(z) '{}' monitor path-hoz nincs JPA entzitás osztály rendelve!", path);
        }

        return clazz;

    }
}
