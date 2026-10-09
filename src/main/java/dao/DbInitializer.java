package dao;

import java.sql.Connection;
import java.sql.Statement;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class DbInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // アプリが起動したときに自動で実行される処理
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // まだテーブルがなかったら、自動で「items」テーブルを作るSQL
            String sql = "CREATE TABLE IF NOT EXISTS items ("
                       + "id INT AUTO_INCREMENT PRIMARY KEY, "
                       + "name VARCHAR(100) NOT NULL, "
                       + "category VARCHAR(50), "
                       + "quantity INT NOT NULL DEFAULT 1, "
                       + "barcode VARCHAR(50), "
                       + "expiry_date DATE NOT NULL, "
                       + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                       + ")";
            
            stmt.execute(sql);
            System.out.println(">>> データベースの初期化（テーブル作成）に成功しました！ <<<");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // アプリ終了時の処理（今回は何もしなくてOK）
    }
}