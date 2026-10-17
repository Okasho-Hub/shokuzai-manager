package dao;

import java.net.URI;
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
                URIHelper helper = parseDatabaseUrl(databaseUrl);
                Class.forName("org.postgresql.Driver");
                return DriverManager.getConnection(helper.jdbcUrl, helper.username, helper.password);
                
            } else {
                // ========== 【ローカル環境（自分のPC）用】 ==========
                Class.forName("org.h2.Driver");
                String url = "jdbc:h2:~/shokuzai_db;AUTO_SERVER=TRUE";
                return DriverManager.getConnection(url, "sa", "");
            }
            
        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBCドライバが見つかりません。", e);
        } catch (Exception e) {
            throw new SQLException("データベース接続エラー: " + e.getMessage(), e);
        }
    }
    
    // 補助クラス：URLを安全に分解する
    private static class URIHelper {
        String jdbcUrl;
        String username;
        String password;
        
        URIHelper(String urlStr) throws Exception {
            URI uri = new URI(urlStr);
            String userInfo = uri.getUserInfo();
            this.username = userInfo != null ? userInfo.split(":")[0] : "";
            this.password = userInfo != null && userInfo.contains(":") ? userInfo.split(":")[1] : "";
            
            String host = uri.getHost();
            int port = uri.getPort();
            String path = uri.getPath();
            
            // ポートが取得できない（-1など）場合はデフォルトの5432を設定
            if (port <= 0) {
                port = 5432;
            }
            
            this.jdbcUrl = "jdbc:postgresql://" + host + ":" + port + path;
        }
    }
    
    private static URIHelper parseDatabaseUrl(String urlStr) throws Exception {
        return new URIHelper(urlStr);
    }
}