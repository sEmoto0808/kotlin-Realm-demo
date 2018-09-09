package com.example.semoto.myownflashcard

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_word_list.*

class WordListActivity : AppCompatActivity(), AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {



    lateinit var realm: Realm
    lateinit var results: RealmResults<WordDB>

    lateinit var wordlist: ArrayList<String>
    lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_list)

        // 画面が開いた時



        // 2.前画面で設定した背景色を設定
        constraintLayoutWordlist.setBackgroundResource(intBacgroundColor)

        // ボタンクリック処理
        // 新しい単語追加をタップした場合
        // EditActivityを開く
        buttonAddNewWord.setOnClickListener {
            val intent = Intent(this@WordListActivity, EditActivity::class.java)
            intent.putExtra(getString(R.string.intent_key_status), getString(R.string.status_add))
            startActivity(intent)
        }

        // 戻るをタップした場合
        // 今の画面を閉じてMainActivityを開く
        buttonBack.setOnClickListener {
            finish()
        }

        // 「暗記済みは下に」ボタンを押した場合
        // 暗記済みフラグが立っている単語を下にソート
        buttonSort.setOnClickListener {

            wordlist.clear()

            results = realm.where(WordDB::class.java).findAll().sort(getString(R.string.db_field_memory_flag))

            results.forEach {
                if (it.boolMemoryFlag) {
                    wordlist.add(it.strAnswer + ":" + it.strQuestion + "【暗記済み】")
                } else {
                    wordlist.add(it.strAnswer + ":" + it.strQuestion)
                }
            }

            listView.adapter = adapter
        }
        
        // リストのクリックリスナー
        listView.onItemClickListener = this
        listView.onItemLongClickListener = this
    }

    override fun onResume() {
        super.onResume()

        // Realmインスタンスの取得
        realm = Realm.getDefaultInstance()

        // 1.DBに登録している単語一覧を表示する（ListVe¥iew）
        results = realm.where(WordDB::class.java).findAll().sort(getString(R.string.db_field_answer))

        //for文を使ってリストの表示形式を修正する
        // 暗記済みのものは「暗記済み」と表示
        wordlist = ArrayList<String>()
//        val length = results.size
//        for (i in 0 until length) {
//            if (results[i]?.boolMemoryFlag!!) {
//                wordlist.add(results[i]?.strAnswer + ":" + results[i]?.strQuestion + "【暗記済み】")
//            } else {
//                wordlist.add(results[i]?.strAnswer + ":" + results[i]?.strQuestion)
//            }
//        }
        results.forEach {
            if (it.boolMemoryFlag) {
                wordlist.add(it.strAnswer + ":" + it.strQuestion + "【暗記済み】")
            } else {
                wordlist.add(it.strAnswer + ":" + it.strQuestion)
            }
        }

        adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, wordlist)
        listView.adapter = adapter
    }

    override fun onPause() {
        super.onPause()

        // Realmインスタンス削除
        realm.close()
    }


    override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {

        // リスト内の単語をタップした場合
        // 1.タップした項目をDBから取得
        val selectedItem = results[position]
        val selectedQuestion = selectedItem?.strQuestion
        val selectedAnswer = selectedItem?.strAnswer

        // 2.EditActivityを開く
        //      -> 1.で取得した情報（問題/答え/行番号）とステータスをIntentで渡す
        val intent = Intent(this@WordListActivity, EditActivity::class.java)
        intent.putExtra(getString(R.string.intent_key_status), getString(R.string.status_change))
        intent.putExtra(getString(R.string.intent_key_question), selectedQuestion)
        intent.putExtra(getString(R.string.intent_key_answer), selectedAnswer)
        intent.putExtra(getString(R.string.intent_key_position), position)
        startActivity(intent)

    }

    override fun onItemLongClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long): Boolean {

        // リスト内の単語を長押しした場合
        // 1.長押しした項目をDBから取得
        val selectedItem = results[position]

        // リスト内の単語を長押しした場合確認ダイアログ
        val dialog = AlertDialog.Builder(this@WordListActivity).apply {
            setTitle(selectedItem?.strAnswer + "の削除")
            setMessage("削除してもいいですか？")

            setPositiveButton("はい") {dialogInterface, i ->
                // 2.1.で取得した内容をDBから削除
                realm.beginTransaction()
                selectedItem?.deleteFromRealm()
                realm.commitTransaction()

                // 3.1.で取得した内容をリストから削除
                wordlist.removeAt(position)

                // 4.DBから単語帳データを再取得して表示
                listView.adapter = adapter
            }

            setNegativeButton("いいえ") {dialogInterface, i ->  }
            show()
        }

        return true
    }








}
