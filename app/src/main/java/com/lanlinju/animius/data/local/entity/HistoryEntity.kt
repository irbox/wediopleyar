package com.lanlinju.animius.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.lanlinju.animius.util.HISTORY_TABLE

@Entity(
    tableName = HISTORY_TABLE,
    indices = [Index("detail_url", unique = true)]
)
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "history_id") val historyId: Long = 0L,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "img_url") val imgUrl: String,
    @ColumnInfo(name = "detail_url") val detailUrl: String,
    @ColumnInfo(name = "source") val source: String, /* Used to determine the domain name */
    @ColumnInfo(name = "updated_at") val updatedAt: Long = System.currentTimeMillis()
)
