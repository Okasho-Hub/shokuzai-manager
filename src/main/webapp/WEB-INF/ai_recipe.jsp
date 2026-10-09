<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>AIシェフからのレシピ提案</title>
    <!-- ▼ Markdownを綺麗なHTMLに変換するライブラリを読み込む -->
    <script src="https://cdn.jsdelivr.net/npm/marked/marked.min.js"></script>
    <style>
        body { font-family: sans-serif; margin: 20px; background: #f4f7f6; color: #333; }
        .container { max-width: 800px; margin: 0 auto; background: white; padding: 30px; border-radius: 8px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); }
        h1 { color: #2c3e50; font-size: 24px; border-bottom: 2px solid #4CAF50; padding-bottom: 10px; }
        
        /* レシピボックスの見た目を整える */
        .recipe-box { background: #e8f5e9; padding: 20px; border-radius: 6px; line-height: 1.6; margin-top: 20px; }
        .recipe-box h3 { color: #2e7d32; margin-top: 20px; border-bottom: 1px dashed #a5d6a7; padding-bottom: 5px; }
        .recipe-box ul, .recipe-box ol { padding-left: 20px; }
        .recipe-box li { margin-bottom: 6px; }
        
        .back-btn { display: inline-block; margin-top: 20px; padding: 10px 20px; background: #3498db; color: white; text-decoration: none; border-radius: 4px; }
        .back-btn:hover { background: #2980b9; }
    </style>
</head>
<body>
    <div class="container">
        <h1>🤖 AIシェフの特製レシピ提案</h1>
        <p>現在の冷蔵庫の食材をもとに、AIが定番レシピと創作料理を考えました！</p>
        
        <%
            String recipeResult = (String) request.getAttribute("aiRecipeResult");
            if (recipeResult == null) {
                recipeResult = "レシピを生成中、またはエラーが発生しました。";
            }
        %>
        
        <!-- Javaから受け取った生のテキストを隠し要素として保持 -->
        <div id="raw-recipe" style="display:none;"><%= recipeResult %></div>
        
        <!-- 変換後の綺麗なレシピが表示される場所 -->
        <div class="recipe-box" id="recipe-content">
            レシピを読み込んでいます...
        </div>
        
        <a href="./" class="back-btn">← 食材一覧に戻る</a>
    </div>

    <script>
        // ページが読み込まれたときに、マークダウンをHTMLに自動変換する
        window.addEventListener('DOMContentLoaded', () => {
            const rawText = document.getElementById('raw-recipe').innerText;
            // marked.parseでマークダウンをHTMLタグに変換して流し込む
            document.getElementById('recipe-content').innerHTML = marked.parse(rawText);
        });
    </script>
</body>
</html>