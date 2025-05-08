package com.lanlinju.animius.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.lanlinju.animius.domain.model.DownloadDetail
import com.lanlinju.animius.util.DOWNLOAD_DETAIL_TABLE

@Entity(
    tableName = DOWNLOAD_DETAIL_TABLE,
    indices = [Index("download_id", unique = false), Index("download_url", unique = true)],
    foreignKeys = [
        ForeignKey(
            entity = DownloadEntity::class,
            parentColumns = ["download_id"],
            childColumns = ["download_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DownloadDetailEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "download_detail_id") val downloadDetailId: Long = 0,
    @ColumnInfo(name = "download_id") val downloadId: Long,
    @ColumnInfo(name = "title") val title: String, /* Episode name eg: Episode 01*/
    @ColumnInfo(name = "img_url") val imgUrl: String,
    @ColumnInfo(name = "drama_number") val dramaNumber: Int = 0, /* Used for episode number sorting*/
    @ColumnInfo(name = "download_url") val downloadUrl: String,
    @ColumnInfo(name = "path") val path: String, /* Saved file path*/
    @ColumnInfo(name = "download_size") val downloadSize: Long = 0, /* 同下 */
    @ColumnInfo(name = "total_size") val totalSize: Long = 0, /* If it is m3u8 type, it is the number of fragments, other files indicate the number of bytes*/
    @ColumnInfo(name = "file_size") val fileSize: Long = 0, /* Write the size of the file after it is downloaded successfully*/
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
) {
    fun toDownloadDetail(): DownloadDetail {
        return DownloadDetail(
            title = title,
            imgUrl = imgUrl,
            dramaNumber = dramaNumber,
            downloadUrl = downloadUrl,
            path = path,
            downloadSize = downloadSize,
            totalSize = totalSize,
            fileSize = fileSize,
        )
    }
}
