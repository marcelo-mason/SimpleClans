package net.sacredlabyrinth.phaed.simpleclans.storage;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 *
 * @author phaed
 */
public interface DBCore
{
    /**
     * @return connection
     */
    Connection getConnection();

    /**
     * @return whether connection can be established
     */
    Boolean checkConnection();

    /**
     * Close connection
     */
    void close();

    /**
     * Execute a select statement
     * @param query
     * @return
     */
    ResultSet select(String query);

    /**
     * Execute an insert statement
     * @param query
     */
    void insert(String query);

    /**
     * Execute an update statement
     * @param query
     */
    void update(String query);

    /**
     * Execute a delete statement
     * @param query
     */
    void delete(String query);

    /**
     * Execute a statement
     * @param query
     * @return
     */
    Boolean execute(String query);

    /**
     * Check whether a table exists
     * @param table
     * @return
     */
    Boolean existsTable(String table);
    
    /**
     * Check whether a colum exists
     *
     * @param tabell
     * @param colum
     * @return
     */
    Boolean existsColumn(String tabell, String colum);
}
