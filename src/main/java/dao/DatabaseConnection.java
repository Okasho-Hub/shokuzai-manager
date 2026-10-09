package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // H2データベースの接続URL（PCのホームディレクトリに shokuzai_db ファイルとして保存される）
    private static final String URL = "jdbc:h2:~/shokuzai_db;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASS = "";

    public static Connection getConnection() throws SQLException {
        try {
            // H2のJDBCドライバを読み込み
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("H2 JDBCドライバが見つかりません。", e);
        }
        return DriverManager.getConnection(URL, USER, PASS);
    }
}