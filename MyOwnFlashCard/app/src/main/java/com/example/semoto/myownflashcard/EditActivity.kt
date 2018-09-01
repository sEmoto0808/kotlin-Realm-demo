package com.example.semoto.myownflashcard

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_edit.*

class EditActivity : AppCompatActivity() {

    lateinit var realm: Realm

    var strQuestion:String = ""
    var strAnswer:String = ""
    var poisition:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        // 画面が開いた時
        // 1.WordlistActivityから渡されたIntent受け取り
        val bundle = intent.extras
        val strStatus = bundle.getString(getString(R.string.intent_key_status))
        textViewStatus.text = strStatus

        //     → 修正の場合は問題・答えの表示も
        if (strStatus == getString(R.string.status_change)) {
            strQuestion = bundle.getString(getString(R.string.intent_key_question))
            strAnswer = bundle.getString(getString(R.string.intent_key_answer))
            poisition = bundle.getInt(getString(R.string.intent_key_position))

            editTextQuestion.setText(strQuestion)
            editTextAnswer.setText(strAnswer)
        }

        // 2.前画面で設定した背景色を設定
        constraintLayoutEdit.setBackgroundResource(intBacgroundColor)

        // ボタンのクリック処理
        // 登録ボタンを押した時
        buttonRegister.setOnClickListener {

            if (strStatus == getString(R.string.status_add)) {
                // 3.「新しい単語の追加」の場合
                //      →　単語の登録処理（addNewWordメソッド）
                addNewWord()
            } else {
                // 4.「登録した単語の修正」の場合
                //      →　単語の修正処理（changeWordメソッド）
                changeWord()
            }
        }

        // 戻るボタンを押した時
        buttonBackEdit.setOnClickListener {
            // 14.今の画面を閉じて単語一覧画面に戻る
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        // Realmインスタンスの取得
        realm = Realm.getDefaultInstance()
    }

    override fun onPause() {
        super.onPause()

        // Realmインスタンス削除
        realm.close()
    }

    private fun changeWord() {
        // 登録した単語の修正処理
        // 8.選択した行番号のレコードをDBから取得
        val results: RealmResults<WordDB> = realm.where(WordDB::class.java).findAll().sort(getString(R.string.db_field_question))
        val selectedItem = results[poisition]

        // 9.入力した問題・答えで1.のレコードを更新
        realm.beginTransaction()  // 開始処理
        val wordDB = realm.createObject(WordDB::class.java)
        wordDB.strQuestion = editTextQuestion.text.toString()
        wordDB.strAnswer = editTextAnswer.text.toString()
        realm.commitTransaction()  // 終了処理

        // 10.入力した文字を入力欄から消す
        editTextQuestion.setText("")
        editTextAnswer.setText("")

        // 11.登録完了メッセージを表示（Toast）
        Toast.makeText(this@EditActivity,
                "修正が完了しました",
                Toast.LENGTH_SHORT)
                .show()

        // 12.今の画面を閉じて単語一覧画面に戻る
        finish()
    }

    private fun addNewWord() {
        // 新しい単語の登録処理
        // 5.入力した問題・答えをDBに登録
        realm.beginTransaction()  // 開始処理
        val wordDB = realm.createObject(WordDB::class.java)
        wordDB.strQuestion = editTextQuestion.text.toString()
        wordDB.strAnswer = editTextAnswer.text.toString()
        realm.commitTransaction()  // 終了処理

        // 6.入力した文字を入力欄から消す
        editTextQuestion.setText("")
        editTextAnswer.setText("")

        // 7.登録完了メッセージを表示（Toast）
        Toast.makeText(this@EditActivity,
                "登録が完了しました",
                Toast.LENGTH_SHORT)
                .show()
    }
}










