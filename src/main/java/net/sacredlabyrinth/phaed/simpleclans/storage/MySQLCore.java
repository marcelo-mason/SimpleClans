package net.sacredlabyrinth.phaed.simpleclans.storage;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

/**
 * @author cc_madelg
 */
public class MySQLCore implements DBCore
{

    private Connection connection;
    private String host;
    private String username;
    private String password;
    private String database;

    /**
     * @param host
     * @param database
     * @param username
     * @param password
     */
    public MySQLCore(String host, String database, String username, String password)
    {
        this.database = database;
        this.host = host;
        this.username = username;
        this.password = password;

        initialize();
    }

    private void initialize()
    {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database, username, password);
        } catch (ClassNotFoundException e) {
            SimpleClans.debug("ClassNotFoundException! " + e.getMessage());
        } catch (SQLException e) {
            SimpleClans.debug("SQLException! " + e.getMessage());
        }
    }

    /**
     * @return connection
     */
    @Override
    public Connection getConnection()
    {
        try {
            if (connection == null || connection.isClosed()) {
                initialize();
            }
        } catch (SQLException e) {
            initialize();
        }

        return connection;
    }

    /**
     * @return whether connection can be established
     */
    @Override
    public Boolean checkConnection()
    {
        return getConnection() != null;
    }

    @Override
    public PreparedStatement prepareStatement(String statement)
    {
        try {
            return connection.prepareStatement(statement);
        } catch (SQLException ex) {
           SimpleClans.debug("Error at creating the statement: " + statement + "(" + ex.getMessage() + ")");
        }
        return null;
    }

    /**
     * Close connection
     */
    @Override
    public void close()
    {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            SimpleClans.debug("Failed to close database connection! " + e.getMessage());
        }
    }

    /**
     * Execute a select statement
     *
     * @param query
     * @return
     */
    @Override
    public ResultSet select(String query)
    {
        try {
            return getConnection().createStatement().executeQuery(query);
        } catch (SQLException ex) {
            SimpleClans.debug("Error at SQL Query: " + ex.getMessage());
            SimpleClans.debug("Query: " + query);
        }

        return null;
    }

    /**
     * Execute an insert statement
     *
     * @param query
     */
    @Override
    public void insert(String query)
    {
        try {
            getConnection().createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            if (!ex.toString().contains("not return ResultSet")) {
                SimpleClans.debug("Error at SQL INSERT Query: " + ex);
                SimpleClans.debug("Query: " + query);
            }
        }
    }

    /**
     * Execute an update statement
     *
     * @param query
     */
    @Override
    public void update(String query)
    {
        try {
            getConnection().createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            if (!ex.toString().contains("not return ResultSet")) {
                SimpleClans.debug("Error at SQL UPDATE Query: " + ex);
                SimpleClans.debug("Query: " + query);
            }
        }
    }

    /**
     * Execute a delete statement
     *
     * @param query
     */
    @Override
    public void delete(String query)
    {
        try {
            getConnection().createStatement().executeUpdate(query);
        } catch (SQLException ex) {
            if (!ex.toString().contains("not return ResultSet")) {
                SimpleClans.debug("Error at SQL DELETE Query: " + ex);
                SimpleClans.debug("Query: " + query);
            }
        }
    }

    /**
     * Execute a statement
     *
     * @param query
     * @return
     */
    @Override
    public Boolean execute(String query)
    {
        try {
            getConnection().createStatement().execute(query);
            return true;
        } catch (SQLException ex) {
            SimpleClans.debug(ex.getMessage());
            SimpleClans.debug("Query: " + query);
            return false;
        }
    }

    /**
     * Check whether a table exists
     *
     * @param table
     * @return
     */
    @Override
    public Boolean existsTable(String table)
    {
        try {
            ResultSet tables = getConnection().getMetaData().getTables(null, null, table, null);
            return tables.next();
        } catch (SQLException e) {
            SimpleClans.debug("Failed to check if table '" + table + "' exists: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check whether a colum exists
     *
     * @param column
     * @return
     */
    @Override
    public Boolean existsColumn(String tabell, String colum)
    {
        try {
            ResultSet colums = getConnection().getMetaData().getColumns(null, null, tabell, colum);
            return colums.next();
        } catch (SQLException e) {
            SimpleClans.debug("Failed to check if colum '" + colum + "' exists: " + e.getMessage(), e);
            return false;
        }
    }
}
