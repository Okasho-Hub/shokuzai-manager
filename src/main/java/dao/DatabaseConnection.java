package dao;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    public static Connection getConnection() throws SQLException {
        try {
            // Renderの環境変数 "DATABASE_URL" が存在するか確認
            String databaseUrl = System.getenv("DATABASE_URL");
            
            if (databaseUrl != null && !databaseUrl.isEmpty()) {
                // ========== 【本番環境（Render上のPostgreSQL）用】 ==========
                // RenderのDATABASE_URLは "postgres://user:password@host:port/dbname" の形式なので変換する
                URI dbUri = new URI(databaseUrl);
                String username = dbUri.getUserInfo().split(":")[0];
                String password = dbUri.getUserInfo().split(":")[1];
                String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
                
                Class.forName("org.postgresql.Driver");
                return DriverManager.getConnection(dbUrl, username, password);
                
            } else {
                // ========== 【ローカル環境（自分のPC）用】 ==========
                // 必要に応じてH2やローカルDBに接続する設定
                Class.forName("org.h2.Driver");
                String url = "jdbc:h2:~/shokuzai_db;AUTO_SERVER=TRUE";
                return DriverManager.getConnection(url, "sa", "");
            }
            
        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBCドライバが見つかりません。", e);
        } catch (URISyntaxException e) {
            throw new SQLException("データベースURLの形式が不正です。", e);
        }
    }
}