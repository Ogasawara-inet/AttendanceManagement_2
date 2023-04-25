/**
 * 従業員一覧から従業員を削除
 * 参考：https://oc-technote.com/javascript/javascript-post-params-move-to-page/
 */

function deleteEmployee(id){
	
	// 「削除」が押された時の処理
	// 確認ダイアログを表示し、「OK」なら実行
	if(window.confirm('本当に削除しますか？')){
		
		// formの作成
		var form = document.createElement("form");
		form.method = "post";
		form.action = "/admin/delete_employee";
		
		// form内のinputの作成
		var input = document.createElement('input');
		input.type = "hidden";
		input.name = "id";
		input.value = id;
		
		// 403エラー防止のためのCSRFトークン作成
		var csrf = document.createElement("input");
		csrf.type = "hidden";
		csrf.name = document.getElementById("csrfToken").name;
		csrf.value = document.getElementById("csrfToken").value;
		
		
		
		// 各要素をform内に作成
		form.appendChild(input);
		form.appendChild(csrf);
		
		// formを実行
		document.body.appendChild(form); // html内にformを作成
		form.submit(); // 実行
		
	}
	
}


