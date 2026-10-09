package servlet;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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

@WebServlet("/AiRecipeServlet")
public class AiRecipeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

 // ▼環境変数からAPIキーを安全に取得する
    private static final String API_KEY = System.getenv("GEMINI_API_KEY");
    
    // ▼API_URLの構築
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.8-flash:generateContent?key=" + API_KEY;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. データベースから今の食材リストを全部取得する
        List<String> ingredientNames = new ArrayList<>();
        String sql = "SELECT name FROM items";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                ingredientNames.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        String recipeResult = "";
        
        // 食材が空の場合のガード
        if (ingredientNames.isEmpty()) {
            recipeResult = "現在、冷蔵庫に食材が登録されていません！まずは食材を登録してください。";
        } else {
            String ingredientsStr = String.join(", ", ingredientNames);
            
            // 2. AIへ送るプロンプトを作成
            String prompt = "以下の冷蔵庫にある食材を使って作れる料理を提案してください。\n"
                          + "・定番の美味しいレシピを2つ\n"
                          + "・AIオリジナルの斬新な創作料理を1つ\n"
                          + "それぞれ、料理名、材料、簡単な作り方を日本語でわかりやすく出力してください。\n\n"
                          + "【冷蔵庫の食材】\n" + ingredientsStr;
            
            // 3. JSONボディの組み立て
            String jsonBody = "{"
                    + "\"contents\": [{"
                    + "\"parts\": [{\"text\": \"" + escapeJson(prompt) + "\"}]"
                    + "}]"
                    + "}";
            
            try {
                // 4. JavaのHttpClientでAPIにリクエストを送信
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest apiRequest = HttpRequest.newBuilder()
                        .uri(URI.create(API_URL))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();
                
                HttpResponse<String> apiResponse = client.send(apiRequest, HttpResponse.BodyHandlers.ofString());
                
                if (apiResponse.statusCode() == 200) {
                    recipeResult = parseGeminiResponse(apiResponse.body());
                } else {
                    recipeResult = "AIとの通信に失敗しました。（ステータスコード: " + apiResponse.statusCode() + "）\n詳細: " + apiResponse.body();
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                recipeResult = "エラーが発生しました: " + e.getMessage();
            }
        }
        
        // 5. 結果をスコープに入れてJSPに渡す
        request.setAttribute("aiRecipeResult", recipeResult);
        request.getRequestDispatcher("/WEB-INF/ai_recipe.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
    
    // 簡易的なJSONエスケープ用メソッド
    ;
    private String escapeJson(String text) {
        return text.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
    }
    
    // GeminiからのJSONレスポンスから本文を抜き出す簡易パーサー
    private String parseGeminiResponse(String json) {
        try {
            int textIndex = json.indexOf("\"text\": \"");
            if (textIndex != -1) {
                int startIndex = textIndex + 9;
                int endIndex = json.indexOf("\"", startIndex);
                String text = json.substring(startIndex, endIndex);
                return text.replace("\\n", "\n").replace("\\\"", "\"");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }
}