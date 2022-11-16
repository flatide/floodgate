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

package com.flatide.floodgate.agent.meta;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flatide.floodgate.ConfigurationManager;
import com.flatide.floodgate.agent.Config;
import com.flatide.floodgate.agent.Configuration;
import com.flatide.floodgate.system.datasource.FDataSource;
import com.flatide.floodgate.system.datasource.FDataSourceDB;
import com.flatide.floodgate.system.datasource.FDataSourceDefault;
import com.flatide.floodgate.system.datasource.FDataSourceFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import org.hibernate.transform.ToListResultTransformer;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/*
    TODO Meta의 생성, 삭제, 변경등은 반드시 DataSource에 먼저 반영하고 캐시를 업데이트 하는 순서로 진행할 것!
    캐시 업데이트 타이밍은 실시간일 필요는 없다.
 */
public final class MetaManager {
    // NOTE spring boot의 logback을 사용하려면 LogFactory를 사용해야 하나, 이 경우 log4j 1.x와 충돌함(SoapUI가 사용)
    private static final Logger logger = LogManager.getLogger(MetaManager.class);

    private static final MetaManager instance = new MetaManager();

    // cache - permanent 구조
    // TODO datasource에서 caching하도록 수정할 것
    Map<String, MetaTable> cache;
    Map<String, String> tableKeyMap;

    // data source
    FDataSource dataSource;

    // config
    Config config = ConfigurationManager.shared().getConfig();

    public static MetaManager shared() {
        return instance;
    }

    private MetaManager() {
        try {
            setDataSource(new FDataSourceDefault());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public FDataSource changeSource(String source, boolean reset) throws Exception {
        if( !source.equals(getMetaSourceName()) ) {
            String type = (String) config.get("datasource."+ source + ".type");
            if (type.equals("FILE")) {
                dataSource = new FDataSourceFile(source);
            } else if (type.equals("DB")) {
                dataSource = new FDataSourceDB(source);
            } else {
                dataSource = new FDataSourceDefault();
            }

            setMetaSource(dataSource, reset);
        }

        return this.dataSource;
    }


    public void close() {
        if( this.cache != null ) {
            // TODO store cache to permanent storage
            this.cache = null;
            this.tableKeyMap = null;
        }

        if( this.dataSource != null ) {
            this.dataSource.close();
            this.dataSource = null;
        }
    }

    public String getMetaSourceName() {
        return this.dataSource.getName();
    }

    public Map<String, Object> getInfo() {
        Map<String, Object> info = new HashMap<>();

        info.put("MetaSource", this.dataSource.getName());

        for( Map.Entry<String, MetaTable> entry : this.cache.entrySet() ) {
            info.put(entry.getKey(), (entry.getValue()).size());
        }

        return info;
    }

    public void setDataSource(FDataSource dataSource) throws Exception {
        setMetaSource(dataSource, true);
    }

    public void setMetaSource(FDataSource FGDataSource, boolean reset) throws Exception {
        logger.info("Set MetaSource as " + FGDataSource.getName() + " with reset = " + reset);
        if( reset ) {
            if (this.cache != null) {
                // TODO store cache to permanent storage
                this.cache = null;
            }

            this.cache = new HashMap<>();
        }
        this.tableKeyMap = new HashMap<>();
        this.dataSource = FGDataSource;
        FGDataSource.connect();
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public MetaTable getTable(String tableName ) {
        return this.cache.get(tableName);
    }

    /*public void addKeyName(String table, String key) {
        this.tableKeyMap.put(table, key);
    }*/

    public boolean create(String table, String key) {
        return create(key);
    }

    // 메타 생성
    private boolean create(String key) {
        if (this.cache.get(key) != null) {
            return false;
        }

        if (dataSource.create(key)) {
            this.cache.put(key, null);
            return true;
        }

        return false;
    }

    // 메타 조회
    public Map<String, Object> read(String tableName, String key ) {
        return read(tableName, key, false);
    }

    // 메타 조회
    public Map<String, Object> read(String tableName, String key, boolean fromSource ) {
        // TODO for testing
        fromSource = true;

        MetaTable table = this.cache.get(tableName);
        if( table == null ) {
            table = new MetaTable();
            this.cache.put(tableName, table);
        }

        String keyName = this.tableKeyMap.get(tableName);
        if( keyName == null ) {
            keyName = "ID";
        }

        if( /*output.get(key) == null || */fromSource) {
            try {
                Map<String, Object> result = this.dataSource.read(tableName, keyName, key);

                table.put(key, result );
            } catch(Exception e ) {
                e.printStackTrace();
            }
        }

        //return table.get(key);

        // 테이블 구조를 ID, DATA로 통합
        Map<String, Object> temp = table.get(key);
        if( temp == null ) {
            temp = table.get(key.toLowerCase(Locale.ROOT));
        }

        if( temp == null )
            return null;

        /*Map<String, Object> result = (Map<String, Object>) temp.get("DATA");
        if( result == null ) {
            result = (Map<String, Object>) temp.get("data");   // for PostgreSQL
        }

        return result;

         */

        String result = (String) temp.get("DATA");
        if( result == null ) {
            result = (String) temp.get("data");   // for PostgreSQL
        }

        if( result == null )
            return null;

        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, Object> json = (Map<String, Object>) mapper.readValue(result, Map.class);
            return json;
        } catch(IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Map<String, Object>> readList(String tableName, String key) {
        return readList(tableName, key, false);
    }

    public List<Map<String, Object>> readList(String tableName, String key, boolean fromSource ) {
        fromSource = true;

        List resultList = null;
        MetaTable table = this.cache.get(tableName);
        if( table == null ) {
            table = new MetaTable();
            this.cache.put(tableName, table);
        }

        String keyName = this.tableKeyMap.get(tableName);
        if( keyName == null ) {
            keyName = "ID";
        }

        if( fromSource ) {
            try {
                resultList = this.dataSource.readList(tableName, keyName, key);

                //table.put(key, result);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        return resultList;
    }

    // 메타 수정
    public boolean insert(String tableName, String keyName, Map<String, Object> data, boolean toSource) throws Exception {
        // TODO Thread Safe
        MetaTable table = this.cache.get(tableName);

        if (table == null) {
            table = new MetaTable();
            this.cache.put(tableName, table);
        }

        /*String keyName = this.tableKeyMap.get(tableName);
        if( keyName == null ) {
            keyName = "ID";
        }*/

        try {
            boolean result = true;
            if( toSource ) {
                result = dataSource.insert(tableName, keyName, data);
            }
            if( result ) {
                table.put(keyName, data);
            }

            return result;
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public boolean update(String tableName, String keyName, Map<String, Object> data) {
        return update(tableName, keyName, data, false);
    }

    // 메타 수정
    public boolean update(String tableName, String keyName, Map<String, Object> data, boolean toSource) {
        // TODO Thread Safe
        MetaTable table = this.cache.get(tableName);

        if (table == null) {
            return false;
        }

        /*String keyName = this.tableKeyMap.get(tableName);
        if( keyName == null ) {
            keyName = "ID";
        }*/

        try {
            boolean result = true;
            if( toSource ) {
                result = dataSource.update(tableName, keyName, data);
            }
            if( result ) {
                table.put(keyName, data);
            }

            return result;
        } catch(Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // 메타 삭제
    public boolean delete(String tableName, String key, boolean toSource, boolean backup) {
        MetaTable table = this.cache.get(tableName);

        if (table == null) {
            return false;
        }

        String keyName = this.tableKeyMap.get(tableName);
        if( keyName == null ) {
            keyName = "ID";
        }

        try {
            if (this.cache.get(tableName) != null) {
                boolean result = true;
                if (toSource) {
                    result = dataSource.delete(tableName, keyName, key, backup);
                }
                if (result) {
                    table.remove(key);
                }
                return true;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean load(String tableName) throws Exception {
        MetaTable table = this.cache.get(tableName);
        if( table == null ) {
            table = new MetaTable();//HashMap<>();
            this.cache.put(tableName, table);
        }

        String keyName = this.tableKeyMap.get(tableName);
        if( keyName == null ) {
            keyName = "ID";
        }


        List<String> keyList = this.dataSource.getAllKeys(tableName, keyName);

        for( String key : keyList ) {
            read(tableName, key, true);
        }

        return true;
    }

    // 메타 저장 cache -> metaSource
    public boolean store(String tableName, boolean all) throws Exception {
        MetaTable table = this.cache.get(tableName);

        if( table == null ) {
            return false;
        }

        String keyName = this.tableKeyMap.get(tableName);
        if( keyName == null ) {
            keyName = "ID";
        }

        Map<String, Map<String, Object>> rows = table.getRows();

        for( Map.Entry<String, Map<String, Object>> e : rows.entrySet() ) {
            String key = e.getKey();
            Map<String, Object> data = e.getValue();
            if( !dataSource.update(tableName, keyName, data) ) {
                dataSource.insert(tableName, keyName, data);
            }
        }

        return true;
    }
}
