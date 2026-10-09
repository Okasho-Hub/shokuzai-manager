package servlet;

import java.io.IOException;
import java.io.PrintWriter;
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

@WebServlet("/GetItemByBarcodeServlet")
public class GetItemByBarcodeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json; charset=UTF-8");
        String barcode = request.getParameter("barcode");
        
        String name = "";
        String category = "";
        
        if (barcode != null && !barcode.isEmpty()) {
            // 同じJANコードを持つデータのうち、最新の1件から名前とカテゴリを取得するSQL
            String sql = "SELECT name, category FROM items WHERE barcode = ? ORDER BY id DESC LIMIT 1";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, barcode);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        name = rs.getString("name");
                        category = rs.getString("category");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        // JSON形式で結果を返す
        PrintWriter out = response.getWriter();
        out.write("{\"name\": \"" + (name != null ? name : "") + "\", \"category\": \"" + (category != null ? category : "") + "\"}");
    }
}