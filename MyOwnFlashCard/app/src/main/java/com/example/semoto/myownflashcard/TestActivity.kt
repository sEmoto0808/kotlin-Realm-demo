package com.example.semoto.myownflashcard

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_test.*
import java.util.*
import kotlin.collections.ArrayList

class TestActivity : AppCompatActivity(), View.OnClickListener {

    var boolStatusMemory = false
    // 問題を暗記済みにするかどうか
    var boolMemorized = false

    // テストの状態
    var intStatus: Int = 0
    val BEFORE_START: Int = 1
    val RUNNING_QUESTION: Int = 2
    val RUNNING_ANSWER: Int = 3
    val TEST_FINISHED: Int = 4

    // Realm関連
    lateinit var realm: Realm
    lateinit var results: RealmResults<WordDB>
    lateinit var wordlist:ArrayList<WordDB>

    var intLength = 0  // 問題数
    var intCount = 0  // カウンター

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        // 画面が開いた時
        // 1.MainActivityからIntent（テスト条件）受け取り
        val bundle = intent.extras
        boolStatusMemory = bundle.getBoolean(getString(R.string.intent_key_memory_flag))

        // 2.前画面で設定した背景色を設定
        constraintLayoutTest.setBackgroundResource(intBacgroundColor)

        // 3.テスト状態を「開始前」に + カード画像非表示
        intStatus = BEFORE_START
        imageViewFlashQuestion.visibility = View.INVISIBLE
        imageViewFlashAnswer.visibility = View.INVISIBLE

        // 4.ボタン①を「テストを始める」に
        buttonNext.setBackgroundResource(R.drawable.image_button_test_start)

        // 5.ボタン②を「確認テストをやめる」に
        buttonEndTest.setBackgroundResource(R.drawable.image_button_end_test)

        // クリックリスナー
        buttonNext.setOnClickListener(this)
        buttonEndTest.setOnClickListener(this)
        checkBox.setOnClickListener {
            boolMemorized = checkBox.isChecked
        }
    }

    override fun onResume() {
        super.onResume()

        // Realmインスタンスの取得
        realm = Realm.getDefaultInstance()

        // 6.DBからテストデータ取得（テスト条件で分岐）
        if (boolStatusMemory) {
            // 暗記済みの単語を除外する
            results = realm.where(WordDB::class.java)
                    .equalTo(getString(R.string.db_field_memory_flag),
                            false)
                    .findAll()
        } else {
            // 暗記済みの単語を除外する
            results = realm.where(WordDB::class.java).findAll()
        }

        // 問題数を表示
        intLength = results.size
        textViewRemaining.text = intLength.toString()

        // 7.6.で取得したテストデータをシャッフル
        wordlist = ArrayList(results)
        Collections.shuffle(wordlist)
    }

    override fun onPause() {
        super.onPause()

        realm.close()
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.buttonNext ->
                // ボタン①を押した時（上のボタン）
                when (intStatus) {
                    BEFORE_START -> {
                        // 1.「テスト開始前」の場合
                        //      「問題を出した段階」に + 問題を表示（showQuestionメソッド）
                        intStatus = RUNNING_QUESTION
                        showQuestion()
                    }

                    RUNNING_QUESTION -> {
                        // 2.「問題を出した段階」の場合
                        //      「答えを出した段階」に + 答えを表示（showAnswerメソッド）
                        intStatus = RUNNING_ANSWER
                        showAnswer()
                    }

                    RUNNING_ANSWER -> {
                        // 3.「答えを出した段階」の場合
                        //      「問題を出した段階」に + 問題を表示（showQuestionメソッド）
                        intStatus = RUNNING_QUESTION
                        showQuestion()
                    }

                }
            R.id.buttonEndTest -> {

                // TODO ボタン②を押した時（下のボタン）

                val dialog = AlertDialog.Builder(this@TestActivity).apply {
                    setTitle("テスト終了")
                    setMessage("テストを終了してもいいですか？")
                    setPositiveButton("はい") {dialogInterface, i ->
                        //      テスト状態が「テスト終了」の場合
                        //      最後の問題の暗記済みフラグをDBに登録（更新）
                        if (intStatus == TEST_FINISHED) {
                            var selectedItem = realm.where(WordDB::class.java)
                                    .equalTo(getString(R.string.db_field_question),
                                            wordlist[intCount - 1].strQuestion).findFirst()
                            realm.beginTransaction()
                            selectedItem?.boolMemoryFlag = boolMemorized
                            realm.commitTransaction()
                        }
                        // 1.テストがめんを閉じてMainActivityに戻る
                        finish()
                    }
                    setNegativeButton("いいえ") {dialogInterface, i ->  }
                    show()
                }

            }

        }
    }

    private fun showAnswer() {
        // 答え表示処理（showAnswerメソッド）
        // 1.答えの表示（画像とテキスト）
        imageViewFlashAnswer.visibility = View.VISIBLE
        textViewFlashAnswer.text = wordlist[intCount - 1].strAnswer

        // 2.ボタン①を「次の問題に進む」ボタンに
        buttonNext.setBackgroundResource(R.drawable.image_button_go_next_question)

        // 3.最後の問題まできたら
        if (intCount == intLength) {
            //      3-1.テスト状態を「終了」にしてメッセージ表示
            intStatus = TEST_FINISHED
            textViewMessage.text = "テスト終了"

            //      3-2.ボタン①を見えない＆使えないように
            buttonNext.isEnabled = false
            buttonNext.visibility = View.INVISIBLE

            //      3-3.ボタン①を「戻る」ボタンに
            buttonEndTest.setBackgroundResource(R.drawable.image_button_back)
        }

    }

    private fun showQuestion() {
        // 問題表示処理（showQuestionメソッド）
        // 1.前の問題の暗記済みフラグをDBに登録（更新）
        if (intCount > 0) {
            var selectedItem = realm.where(WordDB::class.java)
                    .equalTo(getString(R.string.db_field_question),
                            wordlist[intCount - 1].strQuestion).findFirst()
            realm.beginTransaction()
            selectedItem?.boolMemoryFlag = boolMemorized
            realm.commitTransaction()
        }

        // 2.残りの問題数を１つ減らして表示
        intCount ++
        textViewRemaining.text = (intLength - intCount).toString()

        // 3.今回の問題表示 + 前の問題消去（画像とテキスト）
        imageViewFlashAnswer.visibility = View.INVISIBLE
        textViewFlashAnswer.text = ""

        imageViewFlashQuestion.visibility = View.VISIBLE
        textViewFlashQuestion.text = wordlist[intCount - 1].strQuestion

        // 4.ボタン①を「答えを見る」ボタンに
        buttonNext.setBackgroundResource(R.drawable.image_button_go_answer)

        // 5.問題の単語が暗記済みの場合はチェックを入れる
        checkBox.isChecked = wordlist[intCount - 1].boolMemoryFlag
        boolMemorized = checkBox.isChecked
    }
}








