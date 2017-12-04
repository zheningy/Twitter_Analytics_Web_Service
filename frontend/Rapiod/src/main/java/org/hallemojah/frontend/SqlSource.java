package org.hallemojah.frontend;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.sql.SQLException;

public class SqlSource {
    String URL = "localhost";
    String DB_USER = "cc";
    String DB_PWD = "awesome";
    String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    HikariDataSource dataSource;

    public SqlSource() throws IOException, SQLException, ClassNotFoundException {
            HikariConfig config = new HikariConfig();
            try {
                Class.forName(JDBC_DRIVER);
            } catch (ClassNotFoundException e) {
                System.out.println("Ohhhhh");
            }
            config.setJdbcUrl("jdbc:mysql://"+URL+"/twitter?useUnicode=true&character_set_server=utf8mb4&characterEncoding=utf-8");
            config.setUsername(DB_USER);
            config.setPassword(DB_PWD);
            config.setMaximumPoolSize(100);
            config.setConnectionTimeout(500);
            config.setConnectionInitSql("SET NAMES utf8mb4");
            dataSource = new HikariDataSource(config);
    }
    public HikariDataSource getSource(){
        return dataSource;
    }
}
