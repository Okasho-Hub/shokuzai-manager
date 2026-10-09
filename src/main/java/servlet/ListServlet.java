package servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.DatabaseConnection;

@WebServlet("")
public class ListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        String keyword = request.getParameter("keyword");
        String category = request.getParameter("category");
        String sort = request.getParameter("sort");
        
        // 基本のSQL（条件に応じて後ろを組み立てる）
        StringBuilder sql = new StringBuilder("SELECT id, name, category, quantity, barcode, expiry_date, created_at FROM items WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        // キーワード検索の条件追加（食材名またはバーコード、カテゴリに部分一致）
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (name LIKE ? OR barcode LIKE ?)");
            params.add("%" + keyword.trim() + "%");
            params.add("%" + keyword.trim() + "%");
        }
        
        // カテゴリ絞り込みの条件追加
        if (category != null && !category.trim().isEmpty()) {
            sql.append(" AND category LIKE ?");
            params.add("%" + category.trim() + "%");
        }
        
        // ソート条件の追加
        if ("new".equals(sort)) {
            sql.append(" ORDER BY created_at DESC");
        } else if ("name".equals(sort)) {
            sql.append(" ORDER BY name ASC");
        } else if ("quantity".equals(sort)) {
            sql.append(" ORDER BY quantity DESC");
        } else {
            sql.append(" ORDER BY expiry_date ASC");
        }
        
        List<Item> itemList = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            // プレースホルダー（?）にパラメータをセット
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Item item = new Item();
                    item.setId(rs.getInt("id"));
                    item.setName(rs.getString("name"));
                    item.setCategory(rs.getString("category"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setBarcode(rs.getString("barcode"));
                    item.setExpiryDate(rs.getString("expiry_date"));
                    
                    itemList.add(item);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        request.setAttribute("itemList", itemList);
        request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}