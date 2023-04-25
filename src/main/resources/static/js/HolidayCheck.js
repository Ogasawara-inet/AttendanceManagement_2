/**
 * 土曜、日曜、祝日の日付の色を変更する
 * （controller側でファイルを読み込むならUrlResourceを使用する）
 */



/**
 * 祝日の取得には内閣府からダウンロードした
 * 祝日のcsvを使用する。
 * https://www8.cao.go.jp/chosei/shukujitsu/syukujitsu.csv
 */



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
	
}




