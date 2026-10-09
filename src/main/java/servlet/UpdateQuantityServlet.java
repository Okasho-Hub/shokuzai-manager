package servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.DatabaseConnection;

@WebServlet("/UpdateQuantityServlet")
public class UpdateQuantityServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        String idStr = request.getParameter("id");
        String action = request.getParameter("action"); // "plus" または "minus"
        
        if (idStr != null && action != null) {
            int id = Integer.parseInt(idStr);
            
            try (Connection conn = DatabaseConnection.getConnection()) {
                // 1. 現在の数量を取得する
                int currentQuantity = 1;
                String selectSql = "SELECT quantity FROM items WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
                    stmt.setInt(1, id);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            currentQuantity = rs.getInt("quantity");
                        }
                    }
                }
                
                // 2. アクションに応じて数量を計算
                int newQuantity = currentQuantity;
                if ("plus".equals(action)) {
                    newQuantity++;
                } else if ("minus".equals(action)) {
                    newQuantity--;
                }
                
                // 3. 数量が0未満にならないようにする（0になったら削除するか、1で止めるか）
                if (newQuantity <= 0) {
                    // 0以下になったらデータを削除する仕様にする場合
                    String deleteSql = "DELETE FROM items WHERE id = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
                        stmt.setInt(1, id);
                        stmt.executeUpdate();
                    }
                } else {
                    // 数量を更新する
                    String updateSql = "UPDATE items SET quantity = ? WHERE id = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                        stmt.setInt(1, newQuantity);
                        stmt.setInt(2, id);
                        stmt.executeUpdate();
                    }
                }
                
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        // 処理が終わったら一覧画面に戻る
        response.sendRedirect("./");
    }
}