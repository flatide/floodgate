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

package com.flatide.floodgate.system.datasource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flatide.floodgate.ConfigurationManager;
import com.flatide.floodgate.agent.meta.MetaManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.Reader;
import java.sql.*;
import java.util.*;

public class FDataSourceDB extends FDataSourceDefault {
    private static final Logger logger = LogManager.getLogger(MetaManager.class);

    Connection connection = null;

    String url;
    String user;
    String password;

    public FDataSourceDB(String name) {
        super(name);

        this.url = (String) ConfigurationManager.shared().getConfig().get("datasource." + name + ".url");
        this.user = (String) ConfigurationManager.shared().getConfig().get("datasource." + name + ".user");
        this.password = (String) ConfigurationManager.shared().getConfig().get("datasource." + name + ".password");
    }

    @Override
    public boolean connect() throws Exception {
        try {
            this.connection = DriverManager.getConnection(this.url, this.user, this.password);
            return true;
        } catch( Exception e ) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public String getName() {
        return "DB";
    }

    @Override
    public List<String> getAllKeys(String tableName, String keyColumn) {
        String query = "SELECT " + keyColumn + " FROM " + tableName;
        logger.info(query);

        ArrayList<String> result = new ArrayList<>();
        try ( PreparedStatement ps = this.connection.prepareStatement(query); ResultSet rs = ps.executeQuery() ){
            //ResultSetMetaData rsmeta = rs.getMetaData();

            while(rs.next()) {
                String key = rs.getString(1);
                result.add(key);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public boolean create(String key) {
        return false;
    }

    /*
    @Override
    public Map<String, Object> readData(String tableName, String keyColumn, String key) throws Exception {
        String query = "SELECT DATA FROM " + tableName + " WHERE " + keyColumn + " = ?";
        logger.debug(query);

        try (PreparedStatement ps = this.connection.prepareStatement(query) ){
            ps.setString(1, key);

            try (ResultSet rs = ps.executeQuery() ) {
                //ResultSetMetaData rsmeta = rs.getMetaData();

                if (rs.next()) {
                    String data = rs.getString(1);
                    ObjectMapper mapper = new ObjectMapper();

                    @SuppressWarnings("unchecked")
                    Map<String, Object> row = (Map<String, Object>) mapper.readValue(data, LinkedHashMap.class);
                    return row;
                }
            }
            return null;
        } catch(SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public boolean insertData(String tableName, String keyColumn, String key, Map<String, Object> row) {
        String query = "INSERT INTO " + tableName + " ( " + keyColumn + ", DATA ) VALUES ( ?, ?)";

        logger.debug(query);

        try (PreparedStatement ps = this.connection.prepareStatement(query)) {

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(row);

            ps.setString(1, key);
            ps.setString(2, json);

            ps.executeUpdate();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean updateData(String tableName, String keyColumn, String key, Map<String, Object> row) throws Exception {
        String query = "UPDATE " + tableName + " SET DATA = ? WHERE " + keyColumn + " = ?";

        logger.debug(query);

        try (PreparedStatement ps = this.connection.prepareStatement(query)) {

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(row);
            ps.setString(1, json);
            ps.setString(2, key);

            int count = ps.executeUpdate();
            return count != 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean deleteData(String tableName, String keyColumn, String key, boolean backup) {
        String query = "DELETE " + tableName + " WHERE " + keyColumn + " = ?";

        try (PreparedStatement ps = this.connection.prepareStatement(query)) {

            ps.setString(1, key);

            ps.execute();
        } catch (SQLException execption) {
            execption.printStackTrace();
        }

        return false;
    }*/

    @Override
    public Map<String, Object> read(String tableName, String keyColumn, String key) throws Exception {
        String query = "SELECT * FROM " + tableName + " WHERE " + keyColumn + " = ?";
        logger.info(query);

        try (PreparedStatement ps = this.connection.prepareStatement(query) ){
            ps.setString(1, key);

            try (ResultSet rs = ps.executeQuery() ) {

                if (rs.next()) {
                    Map<String, Object> row = new HashMap<>();

                    ResultSetMetaData rsmeta = rs.getMetaData();
                    int count = rsmeta.getColumnCount();
                    for( int i = 1; i <= count; i++ ) {
                        String name = rsmeta.getColumnName(i);
                        Object obj = rs.getObject(i);

			if(obj instanceof Clob) {
			    final StringBuilder sb = new StringBuilder();
			    try {
			        final Reader reader = ((Clob) obj).getCharacterStream();
				final BufferedReader br = new BufferedReader(reader);

				int b;
				while(-1 != (b = br.read())) {
				    sb.append((char) b);
				}

				br.close();
				row.put(name, sb.toString());
			    } catch(Exception e) {
			        e.printStackTrace();
				throw e;
			    }
			} else {
				row.put(name, obj);
			}
                        /*
                        if( obj instanceof String) {
                            // check whether it is JSON or not
                            ObjectMapper mapper = new ObjectMapper();
                            try {
                                Map<String, Object> json = (Map<String, Object>) mapper.readValue((String) obj, Map.class);
                                row.put(name, json);
                            } catch(Exception e) {
                                // it is not JSON
                                row.put(name, obj);
                            }
                        } else {
                            row.put(name, obj);
                        }*/
                    }
                    //String data = rs.getString(1);
                    //ObjectMapper mapper = new ObjectMapper();

                    //@SuppressWarnings("unchecked")
                    //Map<String, Object> row = (Map<String, Object>) mapper.readValue(data, LinkedHashMap.class);
                    return row;
                }
            }
            return null;
        } catch(SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public boolean insert(String tableName, String keyColumn, Map<String, Object> row) throws Exception {
        //String query = "INSERT INTO " + tableName + " ( " + keyColumn + ", DATA ) VALUES ( ?, ?)";
        List<String> colList = new ArrayList<>();

        StringBuilder query = new StringBuilder();
        query.append("INSERT INTO ");
        query.append(tableName);
        query.append(" ( ");

        StringBuilder param = new StringBuilder();
        param.append(" ) VALUES ( ");
        int i = 0;
        for( String col : row.keySet() ) {
            if( i > 0 ) {
                query.append(", ");
            }
            query.append(col);
            colList.add(col);

            if( i > 0 ) {
                param.append(", ");
            }
            param.append("?");
            i++;
        }
        param.append(" ) ");
        query.append(param);

        logger.info(query.toString());

        try (PreparedStatement ps = this.connection.prepareStatement(query.toString())) {
            i = 1;
            for(String col : colList ) {
                Object data = row.get(col);
                if( data == null ) {
                    ps.setNull(i++, Types.NULL);
                } else {
                    if( data instanceof String ) {
                        ps.setString(i++, (String) data);
                    } else if( data instanceof Integer) {
                        ps.setInt(i++, (Integer) data);
                    } else if( data instanceof java.sql.Date) {
                        ps.setDate(i++, (java.sql.Date) data);
                    } else if( data instanceof java.sql.Time) {
                        ps.setTime(i++, (java.sql.Time) data);
                    } else if( data instanceof java.sql.Timestamp) {
                        ps.setTimestamp(i++, (java.sql.Timestamp) data);
                    } else {
                        ObjectMapper mapper = new ObjectMapper();
                        String json = mapper.writeValueAsString(data);

                        ps.setString(i++, json);
                    }
                }
            }

            ps.executeUpdate();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean update(String tableName, String keyColumn, Map<String, Object> row) throws Exception {
        //String query = "UPDATE " + tableName + " SET DATA = ? WHERE " + keyColumn + " = ?";
        List<String> colList = new ArrayList<>();

        String key = (String) row.remove(keyColumn);

        StringBuilder query = new StringBuilder();
        query.append("UPDATE ");
        query.append(tableName);
        query.append(" SET ");

        int i = 0;
        for( String col : row.keySet() ) {
            if( i > 0 ) {
                query.append(", ");
            }
            query.append( col );
            colList.add(col);
            query.append( " = ?");
            i++;
        }
        query.append(" WHERE ");
        query.append(keyColumn);
        query.append(" = ? ");

        logger.info(query.toString());

        try (PreparedStatement ps = this.connection.prepareStatement(query.toString())) {
            i = 1;
            for(String col : colList ) {
                Object data = row.get(col);
                if( data == null ) {
                    ps.setNull(i++, Types.NULL);
                } else {
                    if( data instanceof String ) {
                        ps.setString(i++, (String) data);
                    } else if( data instanceof Integer) {
                        ps.setInt(i++, (Integer) data);
                    } else if( data instanceof java.sql.Date) {
                        ps.setDate(i++, (java.sql.Date) data);
                    } else if( data instanceof java.sql.Time) {
                        ps.setTime(i++, (java.sql.Time) data);
                    } else if( data instanceof java.sql.Timestamp) {
                        ps.setTimestamp(i++, (java.sql.Timestamp) data);
                    }
                }
            }
            ps.setString(i, key);

            int count = ps.executeUpdate();
            return count != 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean delete(String tableName, String keyColumn, String key, boolean backup) throws Exception {
        return false;
    }

    @Override
    public int deleteAll() {
        return 0;
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() {
        try {
            if( this.connection != null ) {
                this.connection.close();
                this.connection = null;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
