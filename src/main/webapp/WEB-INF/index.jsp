<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.List, servlet.Item" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<!-- ▼ スマホの画面幅に自動調整させるための設定 -->
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>食材・賞味期限管理アプリ</title>
<style>
    body {
        font-family: sans-serif;
        margin: 20px;
        background: #f9f9f9;
        color: #333;
    }
    h1 {
        font-size: 22px;
        color: #2c3e50;
    }
    table {
        border-collapse: collapse;
        width: 100%;
        max-width: 900px;
    }
    th, td {
        border: 1px solid #ccc;
        padding: 8px 12px;
        text-align: left;
    }
    th {
        background-color: #f4f4f4;
    }
    .expired {
        background-color: #ffe6e6;
        color: #cc0000;
        font-weight: bold;
    }
    .expiring-soon {
        background-color: #fff9e6;
        color: #b38600;
    }
    .delete-btn {
        background-color: #ff4d4d;
        color: white;
        border: none;
        padding: 5px 10px;
        cursor: pointer;
        border-radius: 4px;
    }
    .delete-btn:hover {
        background-color: #cc0000;
    }

    /* ▼ スマホ向けレスポンシブ対応（画面幅768px以下の場合） */
    @media screen and (max-width: 768px) {
        body {
            margin: 10px;
        }
        .table-responsive {
            width: 100%;
            overflow-x: auto; /* スマホで表がはみ出たときに横スクロールさせる */
            -webkit-overflow-scrolling: touch;
        }
        table {
            font-size: 14px;
            white-space: nowrap; /* 文字が不自然に折れ曲がるのを防ぐ */
        }
        button, .delete-btn {
            padding: 6px 10px;
            font-size: 13px;
        }
    }
</style>
</head>
<body>
    <h1>冷蔵庫の食材一覧</h1>
    
    <!-- AIレシピ提案へのリンクボタン（クリック時にローディングを表示） -->
    <div style="margin-bottom: 15px;">
        <a href="AiRecipeServlet" onclick="startAiLoading()" style="background: #ff5722; color: white; padding: 10px 15px; text-decoration: none; border-radius: 4px; font-weight: bold; display: inline-block;">
            🤖 AIシェフに今ある食材で料理を考えてもらう！
        </a>
    </div>
    
    <p><a href="register.jsp">＋ 新しい食材を登録する</a></p>
        
    <!-- 検索・カテゴリ・ソート用の統合フォーム -->
    <form method="get" action="./" style="margin-bottom: 15px; background: #f9f9f9; padding: 10px; border-radius: 5px; max-width: 900px;">
        <div style="display: flex; gap: 10px; flex-wrap: wrap; align-items: center;">
            <div>
                <label for="keyword">キーワード:</label>
                <input type="text" name="keyword" id="keyword" value="<%= request.getParameter("keyword") != null ? request.getParameter("keyword") : "" %>" placeholder="食材名など">
            </div>
            <div>
                <label for="category">カテゴリ:</label>
                <input type="text" name="category" id="category" value="<%= request.getParameter("category") != null ? request.getParameter("category") : "" %>" placeholder="例: 乳製品">
            </div>
            <div>
                <label for="sort">並び替え:</label>
                <select name="sort" id="sort">
                    <option value="expiry" <%= "expiry".equals(request.getParameter("sort")) ? "selected" : "" %>>賞味期限が近い順</option>
                    <option value="new" <%= "new".equals(request.getParameter("sort")) ? "selected" : "" %>>登録が新しい順</option>
                    <option value="name" <%= "name".equals(request.getParameter("sort")) ? "selected" : "" %>>食材名順</option>
                    <option value="quantity" <%= "quantity".equals(request.getParameter("sort")) ? "selected" : "" %>>数量が多い順</option>
                </select>
            </div>
            <div>
                <button type="submit" style="padding: 5px 15px; background-color: #008CBA; color: white; border: none; border-radius: 4px; cursor: pointer;">絞り込む</button>
                <a href="./" style="margin-left: 5px; font-size: 14px;">リセット</a>
            </div>
        </div>
    </form>
    <hr>
    
    <!-- ▼ テーブルを横スクロール用 div で囲みました -->
    <div class="table-responsive">
        <table>
            <tr>
                <th>ID</th>
                <th>食材名</th>
                <th>カテゴリ</th>
                <th>数量</th>
                <th>賞味期限</th>
                <th>操作</th>
            </tr>
            
            <%
                List<Item> itemList = (List<Item>) request.getAttribute("itemList");
                if (itemList != null && !itemList.isEmpty()) {
                    for (Item item : itemList) {
                        String rowClass = "";
                        String alertText = "";
                        if (item.isExpired()) {
                            rowClass = "expired";
                            alertText = " 【期限切れ】";
                        } else if (item.isExpiringSoon()) {
                            rowClass = "expiring-soon";
                            alertText = " 【まもなく期限】";
                        }
            %>
            <tr class="<%= rowClass %>">
                <td><%= item.getId() %></td>
                <td><%= item.getName() %><%= alertText %></td>
                <td><%= item.getCategory() != null ? item.getCategory() : "" %></td>
                <td>
                    <!-- 数量調整フォーム -->
                    <form action="UpdateQuantityServlet" method="post" style="display: inline; margin: 0;">
                        <input type="hidden" name="id" value="<%= item.getId() %>">
                        <input type="hidden" name="action" value="minus">
                        <button type="submit" style="padding: 2px 6px; cursor: pointer;">-</button>
                    </form>
                    
                    <span style="margin: 0 8px; font-weight: bold;"><%= item.getQuantity() %></span>
                    
                    <form action="UpdateQuantityServlet" method="post" style="display: inline; margin: 0;">
                        <input type="hidden" name="id" value="<%= item.getId() %>">
                        <input type="hidden" name="action" value="plus">
                        <button type="submit" style="padding: 2px 6px; cursor: pointer;">+</button>
                    </form>
                </td>
                <td><%= item.getExpiryDate() %></td>
                <td>
                    <!-- 削除ボタン（フォーム送信） -->
                    <form action="DeleteServlet" method="post" style="margin: 0;" onsubmit="return confirm('本当に削除しますか？');">
                        <input type="hidden" name="id" value="<%= item.getId() %>">
                        <button type="submit" class="delete-btn">使い切った</button>
                    </form>
                </td>
            </tr>
            <% 
                    } 
                } else { 
            %>
            <tr>
                <td colspan="6" style="text-align: center;">まだ食材が登録されていません。</td>
            </tr>
            <% 
                } 
            %>
        </table>
    </div>

    <!-- ▼ AI生成待ちのときに表示するローディング画面（オーバーレイ） -->
    <div id="loading-overlay" style="display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.6); z-index: 9999; justify-content: center; align-items: center; flex-direction: column; color: white;">
        <div style="font-size: 48px; margin-bottom: 15px;">🍳</div>
        <div style="font-size: 18px; font-weight: bold;">AIシェフが冷蔵庫を覗いています...</div>
        <div style="font-size: 14px; margin-top: 8px; color: #ddd;">美味しいレシピを考え中ですので少々お待ちください！</div>
    </div>

    <script>
        // AIボタンが押されたときにローディング画面を表示する関数
        function startAiLoading() {
            document.getElementById('loading-overlay').style.display = 'flex';
        }
    </script>
</body>
</html>