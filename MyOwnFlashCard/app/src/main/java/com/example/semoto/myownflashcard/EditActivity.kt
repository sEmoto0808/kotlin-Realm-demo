package com.example.semoto.myownflashcard

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import io.realm.Realm
import io.realm.RealmResults
import io.realm.exceptions.RealmPrimaryKeyConstraintException
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

            // 修正の場合は問題を修正できないようにする
            editTextQuestion.isEnabled = false
        } else {
            editTextQuestion.isEnabled = true
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

        // 確認ダイアログ
        val dialog = AlertDialog.Builder(this@EditActivity).apply {
            setTitle(selectedItem?.strQuestion + "の変更")
            setMessage("変更してもいいですか？")
            setPositiveButton("はい") {dialogInterface, i ->
                // 9.入力した問題・答えで1.のレコードを更新
                realm.beginTransaction()  // 開始処理

                //           1-1-1.DBの更新処理（主キー設定に伴う変更）
                val wordDB = realm.createObject(WordDB::class.java)
//        wordDB.strQuestion = editTextQuestion.text.toString()
                wordDB.strAnswer = editTextAnswer.text.toString()
                wordDB.boolMemoryFlag = false
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
            setNegativeButton("いいえ") {dialogInterface, i ->  }
            show()
        }


    }

    private fun addNewWord() {

        // 確認ダイアログ
        val  dialog = AlertDialog.Builder(this@EditActivity).apply {
            setTitle("登録")
            setMessage("登録していいですか？")
            setPositiveButton("はい") {dialogInterface, i ->


                // 1.単語の重複チェック
                //      1-1.重複していない場合
                //      1-2.重複している場合
                //          1-1-2.登録不可メッセージ（Toast）
                try {

                    // 新しい単語の登録処理
                    // 5.入力した問題・答えをDBに登録
                    realm.beginTransaction()  // 開始処理
                    //           1-1-1.DBの更新処理（主キー設定に伴う変更）
                    val wordDB = realm.createObject(WordDB::class.java, editTextQuestion.text.toString())
//        wordDB.strQuestion = editTextQuestion.text.toString()
                    wordDB.strAnswer = editTextAnswer.text.toString()
                    wordDB.boolMemoryFlag = false

                    // 7.登録完了メッセージを表示（Toast）
                    Toast.makeText(this@EditActivity,
                            "登録が完了しました",
                            Toast.LENGTH_SHORT)
                            .show()

                } catch (e: RealmPrimaryKeyConstraintException) {
                    // 7.登録完了メッセージを表示（Toast）
                    Toast.makeText(this@EditActivity,
                            "その単語はすでに登録されています",
                            Toast.LENGTH_SHORT)
                            .show()
                } finally {
                    realm.commitTransaction()  // 終了処理

                    // 6.入力した文字を入力欄から消す
                    editTextQuestion.setText("")
                    editTextAnswer.setText("")
                }
            }
            setNegativeButton("いいえ") {dialogInterface, i ->  }
            show()
        }


    }
}










