/**
 * 直前の打刻キャンセル
 */

function cancelStamping(status){
	
	if(window.confirm("直前の打刻をキャンセルしますか？")){
		
		// formの作成
		var form = document.createElement("form");
		form.method = "get";
		form.action = "/cancel_stamping";
		
		// form内のinputの作成
		var input = document.createElement("input");
		input.type = "hidden";
		input.name = "status";
		input.value = status;
		
		// 各要素をform内に作成
		form.appendChild(input);
		
		// formを実行
		document.body.appendChild(form); // html内にformを作成
		form.submit(); // 実行
		
	}
	
}

