package com.lanlinju.animius.data.remote.dandanplay

import com.anime.danmaku.api.DanmakuSession
import com.anime.danmaku.api.TimeBasedDanmakuSession
import com.lanlinju.animius.data.remote.dandanplay.DandanplayDanmakuProvider.Companion.ID
import com.lanlinju.animius.data.remote.dandanplay.dto.toDanmakuOrNull
import com.lanlinju.animius.util.createHttpClient
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A [DanmakuProvider] provides a stream of danmaku for a specific episode.
 *
 * @see DanmakuProviderFactory
 */
interface DanmakuProvider : AutoCloseable {
    // Unique identifier of the bullet comment provider
    val id: String

    // Suspend function, used to obtain the bullet chat session
    suspend fun fetch(subjectName: String, episodeName: String?): DanmakuSession?
}

interface DanmakuProviderFactory { // SPI interface
    /**
     * @see DanmakuProvider.id
     * Get the unique identifier of the bullet comment provider
     */
    val id: String

    // Create a new bullet message provider instance
    fun create(): DanmakuProvider
}

@Singleton
class DandanplayDanmakuProvider @Inject constructor(
    private val client: HttpClient
) : DanmakuProvider {

    companion object {
        const val ID = "DanmakuPlay"
    }

    override val id: String get() = ID

    private val dandanplayClient = DandanplayClient(client)
    private val moviePattern = Regex("Complete Collection|HD|Featured Film")
    private val nonDigitRegex = Regex("\\D")

    override suspend fun fetch(
        subjectName: String, episodeName: String?
    ): DanmakuSession? {
        if (episodeName.isNullOrBlank()) return null
        val formattedEpisodeName = episodeName.let { name ->
            when {
                moviePattern.containsMatchIn(name) -> "movie" // Movie version
                name.contains("No.") -> name.replace(nonDigitRegex, "") // tv No.01set -> 01
                name.matches(Regex("\\d+")) -> name // girigiri tv Episodes only have numbers
                else -> return null // Only get TV version and movie version bullet screen
            }
        }

        val searchEpisodeResponse =
            dandanplayClient.searchEpisode(subjectName, formattedEpisodeName)

        if (!searchEpisodeResponse.success || searchEpisodeResponse.animes.isEmpty()) {
            return null
        }
        val firstAnime = searchEpisodeResponse.animes[0]
        val episodes = firstAnime.episodes
        if (episodes.isEmpty()) {
            return null
        }
        val firstEpisode = episodes[0]
        val episodeId = firstEpisode.episodeId.toLong()

        return createSession(episodeId)
    }

    private suspend fun createSession(
        episodeId: Long,
    ): DanmakuSession {
        val list = dandanplayClient.getDanmakuList(episodeId = episodeId)
        return TimeBasedDanmakuSession.create(
            list.asSequence().mapNotNull { it.toDanmakuOrNull() },
            coroutineContext = Dispatchers.Default,
        )
    }

    override fun close() {
        //client.close()
    }
}

class DandanplayDanmakuProviderFactory : DanmakuProviderFactory {
    override val id: String get() = ID

    override fun create(): DandanplayDanmakuProvider {
        return DandanplayDanmakuProvider(createHttpClient())
    }
}


