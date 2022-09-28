package com.flatide.floodgate.agent.connector;

import java.util.Map;

public class ConnectorFactory {
    //Logger logger = LogManager.getLogger(ConnectorFactory.class);

    private static final ConnectorFactory instance = new ConnectorFactory();

    public static ConnectorFactory shared() {
        return instance;
    }

    private ConnectorFactory() {
    }

    public ConnectorBase getConnector(Map<String, Object> info) throws Exception {
        ConnectorBase con;

        String method = (String) info.get(ConnectorTag.CONNECTOR.name());

        switch( method ) {
            case "JDBC":
                String dbType = (String) info.get(ConnectorTag.JDBCTag.DBTYPE.name());
                loadDriver(dbType);
                con = new ConnectorDB();
                break;
            case "FILE":
                con = new ConnectorFile();
                break;
            default:
                return null;
        }

        return con;
    }

    private void loadDriver(String dbType) throws Exception {
        switch(dbType.trim().toLowerCase()) {
            case "oracle":
                Class.forName("oracle.jdbc.driver.OracleDriver");
                break;
            case "mysql":
                Class.forName("com.mysql.cj.jdbc.Driver");
                break;
            case "mysql_old":
                Class.forName("com.mysql.jdbc.Driver");
                break;
            default:
                break;
        }
    }

}
