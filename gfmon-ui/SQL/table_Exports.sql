call SYSCS_UTIL.SYSCS_EXPORT_QUERY('SELECT t.REST_PATH_MASK, t.ENTITY_NAME, t.DATA_NAME, t.UNIT, t.DESCRIPTION FROM GFMON.APP_CDU t', '\work\GlassFish-Monitor\gfmon-ui\SQL\app_cdu.export', ';', null, 'UTF-8');
call SYSCS_UTIL.SYSCS_EXPORT_QUERY('SELECT t.REST_PATH_MASK, t.ENTITY_NAME, t.DATA_NAME, t.UNIT, t.DESCRIPTION FROM GFMON.CONNPOOL_CDU t', '\work\GlassFish-Monitor\gfmon-ui\SQL\connpool_cdu.export', ';', null, 'UTF-8');
call SYSCS_UTIL.SYSCS_EXPORT_QUERY('SELECT t.REST_PATH, t.ENTITY_NAME, t.DATA_NAME, t.UNIT, t.DESCRIPTION FROM GFMON.SRV_CDU t', '\work\GlassFish-Monitor\gfmon-ui\SQL\srv_cdu.export', ';', null, 'UTF-8');
