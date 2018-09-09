package com.example.semoto.myownflashcard

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

// モデルクラスの作成
open class WordDB : RealmObject() {

    // フィールドの設定
    // 問題
    @PrimaryKey
    open var strQuestion: String  = ""
    // 答え
    open var strAnswer: String  = ""
    // 暗記済みフラグ
    open var boolMemoryFlag: Boolean = false

    // 単語帳DBに暗記済みフラグフィールドと主キーを追加
}