package com.example.semoto.myownflashcard

import io.realm.RealmObject

// モデルクラスの作成
open class WordDB : RealmObject() {

    // フィールドの設定
    // 問題
    var strQuestion: String  = ""
    // 答え
    var strAnswer: String  = ""
}