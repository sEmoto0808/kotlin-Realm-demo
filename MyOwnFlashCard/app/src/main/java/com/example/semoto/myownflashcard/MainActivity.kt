package com.example.semoto.myownflashcard

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


var intBacgroundColor = 0

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ボタンのクリック処理
        // 1.「単語を編集」を押した場合
        // 2.単語一覧画面へ
        buttonEdit.setOnClickListener {

            val intent = Intent(this@MainActivity, WordListActivity::class.java)
            startActivity(intent)
        }

        // 3.「色」ボタンを押した場合
        // 4.画面の背景色をボタンの色に設定

        button01.setOnClickListener {
            intBacgroundColor = R.color.color01
            constraintLayoutMain.setBackgroundResource(intBacgroundColor)
        }
        button02.setOnClickListener {
            intBacgroundColor = R.color.color02
            constraintLayoutMain.setBackgroundResource(intBacgroundColor)
        }
        button03.setOnClickListener {
            intBacgroundColor = R.color.color03
            constraintLayoutMain.setBackgroundResource(intBacgroundColor)
        }
        button04.setOnClickListener {
            intBacgroundColor = R.color.color04
            constraintLayoutMain.setBackgroundResource(intBacgroundColor)
        }
        button05.setOnClickListener {
            intBacgroundColor = R.color.color05
            constraintLayoutMain.setBackgroundResource(intBacgroundColor)
        }
        button06.setOnClickListener {
            intBacgroundColor = R.color.color06
            constraintLayoutMain.setBackgroundResource(intBacgroundColor)
        }

        // 「確認テスト」ボタンを押下した時
        buttonTest.setOnClickListener {
            // TODO 1.テスト画面（TestActivity）へ
            //      →　選択したテスト条件をIntentで渡す
            val intent = Intent(this@MainActivity, TestActivity::class.java)

            when (radioGroup.checkedRadioButtonId) {
                // 暗記済みの単語を除外する場合
                R.id.radioButton -> intent.putExtra(getString(R.string.intent_key_memory_flag), true)
                // 暗記済みの単語を除外しない場合
                R.id.radioButton2 -> intent.putExtra(getString(R.string.intent_key_memory_flag), false)
            }

            startActivity(intent)
        }
    }
}
