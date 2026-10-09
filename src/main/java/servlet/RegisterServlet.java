package servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.DatabaseConnection;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // フォームから送られてきた文字コードを日本語対応にする
        request.setCharacterEncoding("UTF-8");
        
        // 画面に入力された値を受け取る
        String barcode = request.getParameter("barcode");
        String name = request.getParameter("name");
        String category = request.getParameter("category");
        String quantityStr = request.getParameter("quantity");
        String expiryDateStr = request.getParameter("expiry_date");
        
        int quantity = 1;
        try {
            if (quantityStr != null && !quantityStr.isEmpty()) {
                quantity = Integer.parseInt(quantityStr);
            }
        } catch (NumberFormatException e) {
            quantity = 1;
        }
        
        LocalDate expiryDate = LocalDate.parse(expiryDateStr);
        
        // データベースに保存する処理
        String sql = "INSERT INTO items (name, category, quantity, barcode, expiry_date) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            stmt.setString(2, category);
            stmt.setInt(3, quantity);
            stmt.setString(4, barcode);
            stmt.setDate(5, java.sql.Date.valueOf(expiryDate));
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
     // 登録が終わったら一覧表示用のサーブレット（トップ）に移動する
        response.sendRedirect("./");
    }
}