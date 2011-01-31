/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.era7.lib.bioinfo.bioinfoutil.db;

import com.era7.lib.era7jdbcapi.DataBaseException;
import com.era7.lib.era7jdbcapi.MysqlConnection;
import java.sql.Connection;

/**
 *
 * @author ppareja
 */
public class GenomesDBConnection {

    public static String URL = "jdbc:mysql://localhost:3306/genomesdb";
    public static String USERNAME = "root";
    public static String PASSWORD = "";
    public static int MAX_CONNECTIONS = 10;

    public static Connection getNewConnection() throws DataBaseException {

        MysqlConnection.MAXIMUM_ACTIVE_DB_CONNECTIONS = MAX_CONNECTIONS;
        MysqlConnection.SESSION_GUIDED_CONNECTIONS_FLAG = false;
        MysqlConnection.URL = URL;
        MysqlConnection.USERNAME = USERNAME;
        MysqlConnection.PASSWORD = PASSWORD;

        Connection connection = MysqlConnection.getNewConnection();
        return connection;
    }

}
