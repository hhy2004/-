package org.example;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import java.io.IOException;
public class HBaseUtil { private static Connection connection;
    static { try { Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum", "192.168.56.101");
        config.set("hbase.zookeeper.property.clientPort", "2181");
        connection = ConnectionFactory.createConnection(config); }
    catch (IOException e) { e.printStackTrace(); } }
    public static Connection getConnection() { return connection; }
    public static void closeConnection() { if (connection != null) { try { connection.close(); }
    catch (IOException e) { e.printStackTrace(); } } } }
