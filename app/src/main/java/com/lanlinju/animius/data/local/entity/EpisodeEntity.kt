package com.lanlinju.animius.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.lanlinju.animius.domain.model.Episode
import com.lanlinju.animius.util.EPISODE_TABLE

/**
 * [onDelete = ForeignKey.CASCADE] When a record in the parent table is deleted, all rows in the child table that reference the record will also be automatically deleted.
 * [onUpdate = ForeignKey.CASCADE] When the primary key of a record in the parent table is updated, all foreign keys in the child table that reference the primary key are automatically updated to the new value.
 */
@Entity(
    tableName = EPISODE_TABLE,
    indices = [Index("history_id", unique = false), Index("episode_url", unique = true)],
    foreignKeys = [
        ForeignKey(
            entity = HistoryEntity::class,
            parentColumns = ["history_id"],
            childColumns = ["history_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class EpisodeEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "episode_id") val episodeId: Long = 0L,
    @ColumnInfo(name = "history_id") val historyId: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "episode_url") val episodeUrl: String,
    @ColumnInfo(name = "last_position") val lastPosition: Long = 0L, /* Record the position of the last video playback*/
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
) {
    fun toEpisode(): Episode {
        return Episode(
            name = name,
            url = episodeUrl,
            lastPlayPosition = lastPosition,
            isPlayed = false,
            historyId = historyId
        )
    }
}
