package net.sacredlabyrinth.phaed.simpleclans.threads;

import java.sql.Connection;
import java.sql.SQLException;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

/**
 *
 * @author NeT32
 */
public class ThreadUpdateSQL extends Thread {

    Connection connection;
    String query;
    String sqlType;

    public ThreadUpdateSQL(Connection connection, String query, String sqlType)
    {
        this.query = query;
        this.connection = connection;
        this.sqlType = sqlType;
    }

    @Override
    public void run()
    {
        try
        {
        	if (!connection.isClosed()) {
        		this.connection.createStatement().executeUpdate(this.query);
        	}
        }
        catch (SQLException ex)
        {
            if (!ex.toString().contains("not return ResultSet"))
            {
                SimpleClans.getLog().severe("[Thread] Error at SQL " + this.sqlType + " Query: " + ex);
                SimpleClans.getLog().severe("[Thread] Query: " + this.query);
            }
        }
    }
}
