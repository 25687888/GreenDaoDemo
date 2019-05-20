package com.imeepwni.android.greendaodemo.db

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.text.TextUtils
import org.greenrobot.greendao.AbstractDao
import org.greenrobot.greendao.database.Database
import org.greenrobot.greendao.database.StandardDatabase
import org.greenrobot.greendao.internal.DaoConfig
import java.lang.reflect.InvocationTargetException
import java.util.*

/**
 * MigrationHelper Kotlin Version
 * <a href="https://stackoverflow.com/questions/13373170/greendao-schema-update-and-data-migration/30334668#30334668"/>
 */
object MigrationHelper {

    fun migrate(sqliteDatabase: SQLiteDatabase, vararg daoClasses: Class<out AbstractDao<*, *>>) {
        val db = StandardDatabase(sqliteDatabase)
        generateNewTablesIfNotExists(db, *daoClasses)
        generateTempTables(db, *daoClasses)
        dropAllTables(db, true, *daoClasses)
        createAllTables(db, false, *daoClasses)
        restoreData(db, *daoClasses)
    }

    fun migrate(db: StandardDatabase, vararg daoClasses: Class<out AbstractDao<*, *>>) {
        generateNewTablesIfNotExists(db, *daoClasses)
        generateTempTables(db, *daoClasses)
        dropAllTables(db, true, *daoClasses)
        createAllTables(db, false, *daoClasses)
        restoreData(db, *daoClasses)
    }

    private fun generateNewTablesIfNotExists(db: StandardDatabase, vararg daoClasses: Class<out AbstractDao<*, *>>) {
        reflectMethod(db, "createTable", true, *daoClasses)
    }

    private fun generateTempTables(db: StandardDatabase, vararg daoClasses: Class<out AbstractDao<*, *>>) {
        for (i in daoClasses.indices) {
            val daoConfig = DaoConfig(db, daoClasses[i])
            val tableName = daoConfig.tablename
            val tempTableName = daoConfig.tablename + "_TEMP"
            val insertTableStringBuilder = StringBuilder()
            insertTableStringBuilder.append("CREATE TEMP TABLE ").append(tempTableName)
            insertTableStringBuilder.append(" AS SELECT * FROM ").append(tableName).append(";")
            db.execSQL(insertTableStringBuilder.toString())
        }
    }

    private fun dropAllTables(
        db: StandardDatabase,
        ifExists: Boolean,
        vararg daoClasses: Class<out AbstractDao<*, *>>
    ) {
        reflectMethod(db, "dropTable", ifExists, *daoClasses)
    }

    private fun createAllTables(
        db: StandardDatabase,
        ifNotExists: Boolean,
        vararg daoClasses: Class<out AbstractDao<*, *>>
    ) {
        reflectMethod(db, "createTable", ifNotExists, *daoClasses)
    }

    /**
     * dao class already define the sql exec method, so just invoke it
     */
    private fun reflectMethod(
        db: StandardDatabase,
        methodName: String,
        isExists: Boolean,
        vararg daoClasses: Class<out AbstractDao<*, *>>
    ) {
        if (daoClasses.isEmpty()) {
            return
        }
        try {
            for (cls in daoClasses) {
                val method = cls.getDeclaredMethod(methodName, Database::class.java, Boolean::class.javaPrimitiveType)
                method.invoke(null, db, isExists)
            }
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

    }

    private fun restoreData(db: StandardDatabase, vararg daoClasses: Class<out AbstractDao<*, *>>) {
        for (i in daoClasses.indices) {
            val daoConfig = DaoConfig(db, daoClasses[i])
            val tableName = daoConfig.tablename
            val tempTableName = daoConfig.tablename + "_TEMP"
            // get all columns from tempTable, take careful to use the columns list
            val columns = getColumns(db, tempTableName)
            val properties = ArrayList<String>(columns.size)
            for (j in daoConfig.properties.indices) {
                val columnName = daoConfig.properties[j].columnName
                if (columns.contains(columnName)) {
                    properties.add(columnName)
                }
            }
            if (properties.size > 0) {
                val columnSQL = TextUtils.join(",", properties)

                val insertTableStringBuilder = StringBuilder()
                insertTableStringBuilder.append("INSERT INTO ").append(tableName).append(" (")
                insertTableStringBuilder.append(columnSQL)
                insertTableStringBuilder.append(") SELECT ")
                insertTableStringBuilder.append(columnSQL)
                insertTableStringBuilder.append(" FROM ").append(tempTableName).append(";")
                db.execSQL(insertTableStringBuilder.toString())
            }
            val dropTableStringBuilder = StringBuilder()
            dropTableStringBuilder.append("DROP TABLE ").append(tempTableName)
            db.execSQL(dropTableStringBuilder.toString())
        }
    }

    private fun getColumns(db: StandardDatabase, tableName: String): List<String> {
        var columns: List<String>? = null
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery("SELECT * FROM $tableName limit 0", null)
            if (null != cursor && cursor.columnCount > 0) {
                columns = Arrays.asList(*cursor.columnNames)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
            if (null == columns)
                columns = ArrayList()
        }
        return columns
    }

}
