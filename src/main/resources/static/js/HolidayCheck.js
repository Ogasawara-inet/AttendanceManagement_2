/**
 * 土曜、日曜、祝日の日付の色を変更する
 * （controller側でファイルを読み込むならUrlResourceを使用する）
 */

/**
 * 祝日の取得には基本的に内閣府からダウンロードした
 * 祝日のcsvを使用する。
 * https://www8.cao.go.jp/chosei/shukujitsu/syukujitsu.csv
 */



// 祝日の一覧を格納する配列
//var holidays = [];



// ページを開いた時の処理
window.onload = function(){
	
	// 日付の色を変更
	holidayCheck();
	
	// ヘッダーの処理
	// （Header.jsのonloadが上書きされるためここに記述）
	header();
	
}



// 日付の色の変更
function holidayCheck(){
	
	// 祝日の一覧を取得（今年分のみ渡される）
	// 一覧は[]で囲われたカンマ+空白区切りの文字列として取得される
	var holidays = document.getElementById("holidays").value;
	
	
	// 日付の要素を取得、その要素の値を祝日の一覧を照らし合わせて
	// その日が祝日なら文字の色を赤くする
	// 祝日でない場合、土曜なら青、日曜なら赤にする
	// id名は「dateXX」（XXは日付、0埋めはしない）
	for(var i = 1; i <= 31; i++){
		
		var date =  document.getElementById("date" + i); // 日付の項目
		var dateInput = document.getElementById("dateInput" + i); // 日付のinput
		
		var dateValue = (dateInput != null) ? new Date(dateInput.value.replaceAll("/", "-")) : null;
		
		if(date != null && dateValue != null){

				// holidaysの文字列の中に指定された文字が入っていれば
				if(holidays.includes(dateInput.value.replaceAll("/", "-"))){ // 祝日
					date.style.color = "red";
				} else if(dateValue.getDay() == 0) { // 日曜
					date.style.color = "red";
				} else if(dateValue.getDay() == 6) { // 土曜
					date.style.color = "blue";
				}
			
		}
		
	}
	
	
	
	// 一覧が空か、一覧の最後の日が今日より前なら警告メッセージを表示する
	holidays.replace("[", "");
	holidays.replace("]", "");
	var arr = holidays.split(", ");
	if(holidays = "" || new Date(arr[arr.length - 1]) < new Date()){
		
		// 警告メッセージを表示
		var fileGetError = document.getElementById("fileGetError");
		if(fileGetError != null) fileGetError.style.display = block;
		
	}
	




//	
//	// 祝日のファイルのurl
//	// 内閣府HPからダウンロードできる
//	const url = "/data/syukujitsu.csv";
////	const url = "https://www8.cao.go.jp/chosei/shukujitsu/syukujitsu.csv":
//	
//	
//	
//	// XMLHttpRequestオブジェクトの生成（メソッドは下記）
//	const xhr = new XMLHttpRequestCreate();
//	
//	// 内閣府HPからダウンロードした祝日一覧のcsvを取得
//	// XMLHttpRequestを使用する
//	xhr.open('get', url); // 取得するファイルの設定
//	xhr.send(); // リクエストの送信
//	
//	// リクエスト受け取り
//	xhr.onreadystatechange = function(){
//		
//		// 通信が正常に終了したか確認
//		if(xhr.readyState === 4 && xhr.status === 200){
//			
//			// 祝日の一覧を取得
//			var lines = this.responseText.split(/\r\n|\n/);; // ファイルの中身を配列として取得
//			for(var i = 0; i < lines.length; i++){
//				if(i != 0){ // 最初の行はタイトルなので無視する
//					var holiday = new Date(lines[i].split(",")[0]); // 祝日の日付型に変換して取得
//					if(holiday != null) holidays.push(holiday); // 配列に格納
//				}
//				
//			}
//			
//		}
//		
//		
//		
//		// 祝日の最後の日が指定日より前なら、
//		// その最後の日以降の休日をアルゴリズムから生成
//		var end = new Date();
//		end.setFullYear(end.getFullYear() + 3); // 3年後
//		
//		if(holidays[holidays.length - 1] < end){
//			
////			generateHoliday();
//			
//		}
//		
//		
//		
//		// 日付の要素を取得、その要素の値を祝日の一覧を照らし合わせて
//		// その日が祝日なら文字の色を赤くする
//		// 祝日でない場合、土曜なら青、日曜なら赤にする
//		// id名は「dateXX」（XXは日付、0埋めはしない）
//		for(var i = 1; i <= 31; i++){
//			
//			var da document.getElementById("date" + i);
//			var dateInput = doment.getElementById("dateInput" + i);
//			
//			var dateValue = dateInput != null 
//			? new Date(dateInput.value) : null;
//			
//			if(date != null && dateValue != null){
//				
//				// csvで取得したDateの時間は00:00:00なのでそれに合わせる
//				dateValue.setHours(0);
//				
//				// Date型の「==」はインスタンスの比較なので
//				// indexOfではなくsomeを使用する。
//				if(holidays.some(h => h.getTime() === dateValue.getTime())){ // 祝日
//					date.style.color = "red";
//				} else if(dateValue.getDay() == 0) { // 日曜
//					date.style.color = "red";
//				} else if(dateValue.getDay() == 6) { // 土曜
//					date.style.color = "blue";
//				}
//				
//			}
//			
//		}
//		
//	}
	
}



//// XMLHttpRequestオブジェクトの生成
//function XMLHttpRequestCreate(){
//	
//	// オブジェクトの生成
//	try{
//		return new XMLHttpRequest();
//	}catch(e){}
//	
//	// IE6用
//	try{
//		return new ActiveXObject('MSXML2.XMLHTTP.6.0');
//	}catch(e){}
//	try{
//		return new ActiveXObject('MSXML2.XMLHTTP.3.0');
//	}catch(e){}
//	try{
//		return new ActiveXObject('MSXML2.XMLHTTP');
//	}catch(e){}
//	
//	// 未対応
//	return null;
//	
//}




