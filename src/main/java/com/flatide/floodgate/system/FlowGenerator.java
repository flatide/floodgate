/*
 * MIT License
 *
 * Copyright (c) 2022 FLATIDE LC.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.flatide.floodgate.system;

import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FlowGenerator {
    public Map generate(
            Map body, String src_trg) throws Exception {
        Map<String, Object> result = new LinkedHashMap<>();
   
        Map execInfo = (Map) body.get("EXEC_INFO");
   
        List<Map> mappingInfoList = (List) body.get("MAPPING_INFO");

        Map<String, Object> data = new LinkedHashMap<>();
        Map<String, Object> module = new LinkedHashMap<>();
        Map<String, Object> connect = new LinkedHashMap<>();
        if ("SRC".equals(src_trg) || "ALL".equals(src_trg)) {
            Map<String, Object> select = new LinkedHashMap<>();
            String prefix = "SRC";
   
            String db_type = (String) execInfo.get(prefix + "_DB_TYPE");
            String table = (String) execInfo.get(prefix + "_TABLE");
            String user = (String) execInfo.get(prefix + "_USER");
            String pwd = (String) execInfo.get(prefix + "_PWD");
            String url = (String) execInfo.get(prefix + "_URL");
            String ip = (String) execInfo.get(prefix + "_IP");
            String port = (String) execInfo.get(prefix + "_PORT");
            String sid = (String) execInfo.get(prefix + "_SID");
   
            if( db_type == null || db_type.isEmpty()) {
                result.put("success", false);
                result.put("error", "DB_TYPE is required.");
                return result;
            }
   
            if( table == null || table.isEmpty()) {
                result.put("success", false);
                result.put("error", "TABLE is required.");
                return result;
            }
   
            data.put("ENTRY", "SELECT");
   
            select.put("ACTION", "READ");
   
            connect.put("CONNECTOR", "JDBC");
            connect.put("DBTYPE", db_type);
            if( url == null || url.isEmpty()) {
                url = makeURL(db_type, ip, port, sid);
            }
            connect.put("URL", url);
            connect.put("USER", user);
            connect.put("PASSWORD", pwd);
            connect.put("IP", ip);
            connect.put("PORT", port);
            connect.put("SID", sid);
   
            select.put("CONNECT", connect);
   
            select.put("TARGET", table);
            select.put("RULE", "MAPPING");
            if( "ALL".equals(src_trg)) {
                select.put("CALL", "INSERT");
            }
   
            select.put("TEMPLATE", "JDBC");
   
            module.put("SELECT", select);
   
            data.put("MODULE", module);
   
        }

        if ("TRG".equals(src_trg) || "ALL".equals(src_trg)) {
            Map<String, Object> insert = new LinkedHashMap<>();
            String prefix = "TRG";
   
            String db_type = (String) execInfo.get(prefix + "_DB_TYPE");
            String table = (String) execInfo.get(prefix + "_TABLE");
            String user = (String) execInfo.get(prefix + "_USER");
            String pwd = (String) execInfo.get(prefix + "_PWD");
            String url = (String) execInfo.get(prefix + "_URL");
            String ip = (String) execInfo.get(prefix + "_IP");
            String port = (String) execInfo.get(prefix + "_PORT");
            String sid = (String) execInfo.get(prefix + "_SID");
   
            if (db_type == null || db_type.isEmpty()) {
                result.put("success", false);
                result.put("error", prefix + "_DB_TYPE is required.");
                return result;
            }

            if (table == null || table.isEmpty()) {
                result.put("success", false);
                result.put("error", prefix + "_TABLE is required.");
                return result;
            }
   
            if (!"ALL".equals(src_trg)) {
                data.put("ENTRY", "INSERT");
            }
   
            insert.put("ACTION", "CREATE");
   
            connect.put("CONNECTOR", "JDBC");
            connect.put("DBTYPE", db_type);
            if (url == null || url.isEmpty()) {
                url = makeURL(db_type, ip, port, sid);
            }
            connect.put("URL", url);
            connect.put("USER", user);
            connect.put("PASSWORD", pwd);
            connect.put("IP", ip);
            connect.put("PORT", port);
            connect.put("SID", sid);
   
            insert.put("CONNECT", connect);
   
            insert.put("TEMPLATE", "JDBC");
            insert.put("TARGET", table);
            insert.put("RULE", "MAPPING");
   
            module.put("INSERT", insert);
   
            data.put("MODULE", module);
   
        }
   
        Map<String, Object> rule = new LinkedHashMap<>();
        Map<String, String> mapping = new LinkedHashMap<>();

        try {
            // Item needed to process template
            Map<String, Object> item = new LinkedHashMap<>();
            for (Map row : mappingInfoList) {
                String targetColumn = (String) row.get("COL_ID");
                String sourceColumn = (String) row.get("MAP_SRC_COL_ID");
                String logic = (String) row.get("MAP_SRC_COL_LOGIC");

                if (targetColumn == null || targetColumn.isEmpty()) {
                    continue;
                }

                if (logic != null && !logic.isEmpty()) {
                    if (sourceColumn == null || sourceColumn.isEmpty()) {
                        // NOTE source column is mandatory
                    } else {
                        //item.put(sourceColumn, "");
                    }

                    // +sysdate, 'API', >IF_DATE, {REFERENCE}
                    char first = logic.charAt(0);
                    switch (first) {
                        case '{':
                            break;
                        case '>':
                            break;
                        case '#':
                            break;
                        case '+':
                            break;
                        case '\'':
                            break;
                        default:
                            //logic = "+" + logic;
                            item.put(sourceColumn, "");
                            break;
                    }
                    sourceColumn = logic;
                } else {
                    if (sourceColumn == null || sourceColumn.isEmpty()) {
                        sourceColumn = targetColumn;
                    }
                    item.put(sourceColumn, "");
                }

                mapping.put(targetColumn, sourceColumn);
            }
            rule.put("MAPPING", mapping);
            data.put("RULE", rule);

            long cur = System.currentTimeMillis();
            Timestamp current = new Timestamp(cur);

            data.put("CREATE_DATE", current);
            data.put("MODIFY_DATE", current);

            result.put("success", true);
            result.put("FLOW", data);
        } catch (Exception e) {
            result.put("success", false);
            result.put("reason", e.getClass().getName() + " : " + e.getMessage());
        }
        // result.put("QUERY_PARAM", query_param);

        //result.put("META", data);
        return result;
    }

    public String makeURL(String dbType, String ipList, String port, String sid) {
        String[] ipports = ipList.split("\\|");

        for (int i = 0; i < ipports.length; i++) {
            ipports[i] = ipports[i].trim() + ":" + port;
        }

        switch (dbType.trim().toUpperCase()) {
            case "ORACLE": {
                if (ipports.length == 1) {
                    if (sid.startsWith("/")) { // for ServiceName
                        return "jdbc:oracle:thin:@" + ipports[0].trim() + sid.trim();
                    }
                    return "jdbc:oracle:thin:@" + ipports[0].trim() + ":" + sid.trim();
                } else {
                    String url = "jdbc:oracle:thin:@(DESCRIPTION=(FAILOVER=ON)(LOAD_BALANCE=OFF)(ADDRESS_LIST=";
                    for (int i = 0; i < ipports.length; i++) {
                        String[] ip_port = ipports[i].split(":");
                        url += "(ADDRESS=(PROTOCOL=TCP)(HOST=" + ip_port[0] + ")(PORT=" + ip_port[1] + "))";
                    }

                    sid = sid.replace("/", "").trim();
                    return url + ")(CONNECT_DATA=(SERVICE_NAME=" + sid + ")(FAILOVER_MODE=(TYPE_SELECT)(METHOD=BASIC)(RETRIES=25)(DELAY=10))))";
                }
            }
            case "TIBERO": {
                if (ipports.length == 1) {
                    if (sid.startsWith("/")) { // for ServiceName
                        return "jdbc:tibero:thin:@" + ipports[0].trim() + sid.trim();
                    }
                    return "jdbc:tibero:thin:@" + ipports[0].trim() + ":" + sid.trim();
                } else {
                    String url = "jdbc:tibero:thin:@(DESCRIPTION=(FAILOVER=ON)(LOAD_BALANCE=OFF)(ADDRESS_LIST=";
                    for (int i = 0; i < ipports.length; i++) {
                        String[] ip_port = ipports[i].split(":");
                        url += "(ADDRESS=(PROTOCOL=TCP)(HOST=" + ip_port[0] + ")(PORT=" + ip_port[1] + "))";
                    }

                    sid = sid.replace("/", "").trim();
                    return url + ")(CONNECT_DATA=(SERVICE_NAME=" + sid + ")(FAILOVER_MODE=(TYPE_SELECT)(METHOD=BASIC)(RETRIES=25)(DELAY=10))))";
                }
            }
            case "POSTGRESQL":
                return "jdbc:postgresql://" + ipports[0] + "/" + sid;
            case "GREENPLUM":
                return "jdbc:postgresql://" + ipports[0] + "/" + sid;
            case "MSSQL":
                return "jdbc:sqlserver://" + ipports[0] + ";databaseName=" + sid;
            case "MYSQL_OLD":
                return "jdbc:mysql://" + ipports[0] + "/" + sid + "?characterEncoding=UTF-8&useConfigs=maxPerformance";
            case "MYSQL":
                return "jdbc:mysql://" + ipports[0] + "/" + sid + "?serverTimezone=UTC&characterEncoding=UTF-8&useConfigs=maxPerformance";
            case "MARIADB":
                return "jdbc:mysql://" + ipports[0] + "/" + sid + "?characterEncoding=UTF-8&useConfigs=maxPerformance";
            case "DB2":
                return "jdbc:db2://" + ipports[0] + "/" + sid;
            default:
                return "";
        }
    }
}
            
