package com.imeepwni.android.greendaodemo.app

import android.app.Application
import android.database.sqlite.SQLiteDatabase
import com.imeepwni.android.greendaodemo.BuildConfig
import com.imeepwni.android.greendaodemo.db.MigrationHelper
import com.imeepwni.android.greendaodemo.model.DaoMaster
import com.imeepwni.android.greendaodemo.model.DaoSession
import com.imeepwni.android.greendaodemo.model.NoteDao


class App : Application() {

    private lateinit var mDaoSession: DaoSession

    override fun onCreate() {
        super.onCreate()

        initGreenDaoDB()
    }

    /**
     * 初始化greenDao数据库配置
     */
    private fun initGreenDaoDB() {
        val db = if (BuildConfig.DEBUG) {
            DaoMaster.DevOpenHelper(this, "note-db").writableDb
        } else {
            object : DaoMaster.OpenHelper(this, "note-db") {
                /**
                 * 升级数据库
                 */
                override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
                    super.onUpgrade(db, oldVersion, newVersion)
                    MigrationHelper.migrate(
                        db!!,
                        NoteDao::class.java
                    )
                }
            }.writableDb
        }
        mDaoSession = DaoMaster(db).newSession()
    }

    fun getDaoSession(): DaoSession {
        return mDaoSession;
    }
}