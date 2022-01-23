package ru.job4j.quartz;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class ConnectSQL implements AutoCloseable  {
    private Connection cn;
    private int times;

    public ConnectSQL(Connection cn, int times) {
        this.cn = cn;
        this.times = times;
    }

    public ConnectSQL() {
    }

    public int getTimes() {
        return times;
    }

    public void init() {
        try (InputStream in = ConnectSQL.class.getClassLoader()
                .getResourceAsStream("rabbit.properties")) {
            Properties config = new Properties();
            config.load(in);
            Class.forName(config.getProperty("driver"));
            times = Integer.parseInt(config.getProperty("rabbit.interval"));
            cn = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("login"),
                    config.getProperty("password")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void close() throws Exception {
        if (cn != null) {
            cn.close();
        }
    }
}

