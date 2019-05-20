package com.imeepwni.android.greendaodemo.viewmodel

import android.content.Context
import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.ObservableField
import com.imeepwni.android.greendaodemo.model.DaoSession
import com.imeepwni.android.greendaodemo.model.Note
import com.imeepwni.android.greendaodemo.model.NoteDao

class DBActionViewModel(val context: Context, daoSession: DaoSession) : BaseObservable() {

    private var mNoteDao: NoteDao = daoSession.noteDao
    var mActionResult: ObservableField<String> = ObservableField()

    /**
     * 数插入
     */
    @Suppress("UNUSED_PARAMETER")
    fun insertNote(view: View) {
        val note = Note()
        mNoteDao.insert(note)
        mActionResult.set(note.toString())
        notifyChange()
    }

    /**
     * 删除
     */
    @Suppress("UNUSED_PARAMETER")
    fun deleteNote(view: View) {
        mNoteDao.deleteAll()
        notifyChange()
    }

    /**
     * 查询
     */
    @Suppress("UNUSED_PARAMETER")
    fun searchNote(view: View) {
        val note = Note()
        val all = mNoteDao.loadAll()
        mActionResult.set(all.toString())
        notifyChange()
    }

    /**
     * 修改
     */
    @Suppress("UNUSED_PARAMETER")
    fun editNote(view: View) {
        mNoteDao.update(mNoteDao.loadByRowId(1).apply {
            comment = "RowId is 1"
        })
        searchNote(view)
    }

    /**
     * 修改
     */
    @Suppress("UNUSED_PARAMETER")
    fun getFirstNote(view: View) {
        val note = mNoteDao.queryBuilder()
            .where(NoteDao.Properties.Id.between(-1, 5))
            .orderAsc()
            .build()
            .listLazy()
        mActionResult.set(note.get(0).toString())
        note.close()
    }

    @Suppress("UNUSED_PARAMETER")
    fun deleteNotes(view: View) {
        mNoteDao.queryBuilder()
            .where(NoteDao.Properties.Id.between(-1, 5))
            .orderAsc()
            .buildDelete()
            .executeDeleteWithoutDetachingEntities()
        searchNote(view)
    }
}