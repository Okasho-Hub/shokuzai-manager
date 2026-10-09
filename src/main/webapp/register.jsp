<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>食材登録 - 食材・賞味期限管理アプリ</title>
<!-- html5-qrcode ライブラリの読み込み -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/html5-qrcode/2.3.8/html5-qrcode.min.js"></script>
<style>
    body {
        font-family: sans-serif;
        padding: 20px;
        max-width: 600px;
        margin: 0 auto;
    }
    .form-group {
        margin-bottom: 15px;
    }
    label {
        display: block;
        margin-bottom: 5px;
        font-weight: bold;
    }
    input[type="text"], input[type="number"], input[type="date"], select {
        width: 100%;
        padding: 8px;
        box-sizing: border-box;
    }
    .btn {
        background-color: #4CAF50;
        color: white;
        padding: 10px 15px;
        border: none;
        cursor: pointer;
        border-radius: 4px;
        font-size: 16px;
    }
    .btn:hover {
        background-color: #45a049;
    }
    /* カメラ映像を表示するエリアのスタイル */
    #reader {
        width: 100%;
        max-width: 400px;
        margin: 10px 0;
    }
    .camera-btn {
        background-color: #008CBA;
        color: white;
        padding: 8px 12px;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        margin-bottom: 10px;
    }
</style>
</head>
<body>
    <h1>新しい食材を登録</h1>
    
    <!-- カメラ起動ボタン -->
    <button type="button" class="camera-btn" onclick="startScanner()">📷 バーコードをカメラで読み取る</button>
    
    <!-- カメラ映像が映るボックス -->
    <div id="reader"></div>

    <form action="RegisterServlet" method="post">
        <div class="form-group">
            <label for="barcode">JANコード (バーコード):</label>
            <input type="text" id="barcode" name="barcode">
        </div>
        
        <div class="form-group">
            <label for="name">食材名:</label>
            <input type="text" id="name" name="name" required>
        </div>
        
        <div class="form-group">
            <label for="category">カテゴリ:</label>
            <input type="text" id="category" name="category">
        </div>
        
        <div class="form-group">
            <label for="quantity">数量:</label>
            <input type="number" id="quantity" name="quantity" value="1" min="1">
        </div>
        
        <div class="form-group">
            <label for="expiry_date">賞味期限:</label>
            <input type="date" id="expiry_date" name="expiry_date" required>
        </div>
        
        <button type="submit" class="btn">登録する</button>
    </form>
    
    <p><a href="./">← 一覧画面に戻る</a></p>

	<!-- バーコード読み取りのJavaScript処理 -->
	    <script>
	        let html5QrCode = null;

	        function startScanner() {
	            const readerElement = document.getElementById("reader");
	            readerElement.style.display = "block";
	            
	            html5QrCode = new Html5Qrcode("reader");
	            
	            // スマホの背面カメラを優先して起動する設定
	            const config = { fps: 10, qrbox: { width: 250, height: 100 } };
	            
	            html5QrCode.start(
	                { facingMode: "environment" }, 
	                config,
	                (decodedText, decodedResult) => {
	                    // 読み取りに成功したときの処理
	                    document.getElementById("barcode").value = decodedText;
	                    
	                    // ★【追加】カメラで読み取った直後にも自動補完を実行する
	                    fetchItemData(decodedText);
	                    
	                    alert("バーコードを読み取りました: " + decodedText);
	                    
	                    // 読み取ったらカメラを自動停止する
	                    stopScanner();
	                },
	                (errorMessage) => {
	                    // 読み取り中のエラー（フレームごとのスキャン失敗などは無視してOK）
	                }
	            ).catch((err) => {
	                alert("カメラを起動できませんでした。権限が許可されているか確認してください。");
	                console.error(err);
	            });
	        }

	        function stopScanner() {
	            if (html5QrCode) {
	                html5QrCode.stop().then(() => {
	                    document.getElementById("reader").style.display = "none";
	                }).catch((err) => {
	                    console.error("カメラの停止に失敗しました", err);
	                });
	            }
	        }

	        // ==========================================
	        // ★【新規追加】自動補完（オートフィル）の処理
	        // ==========================================
	        
	        // 共通でデータを取得して入力欄にセットする関数
	        function fetchItemData(barcode) {
	            if (!barcode) return;
	            
	            fetch('GetItemByBarcodeServlet?barcode=' + encodeURIComponent(barcode))
	                .then(response => response.json())
	                .then(data => {
	                    if (data.name) {
	                        // 過去のデータが見つかったら名前とカテゴリを自動入力
	                        document.getElementById("name").value = data.name;
	                        document.getElementById("category").value = data.category;
	                        console.log("過去のデータを自動補完しました: " + data.name);
	                    }
	                })
	                .catch(error => {
	                    console.error('自動補完の取得に失敗しました:', error);
	                });
	        }

	        // 手動でJANコードを入力してフォーカスが外れた（blur）ときのイベント
	        document.getElementById("barcode").addEventListener("blur", function() {
	            fetchItemData(this.value);
	        });
	    </script>
</body>
</html>