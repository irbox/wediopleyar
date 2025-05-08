package com.lanlinju.animius.dandanplay

import com.lanlinju.animius.data.remote.dandanplay.DandanplayDanmakuProviderFactory
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Test

class DandanplayDanmakuProviderTest {
    private val provider = DandanplayDanmakuProviderFactory().create()

    @Test
    fun testFetchTVDanmakuSession() = runBlocking {

        val session = provider.fetch("One Piece", "Episode 11")

        assertNotNull(session)
    }

    @Test
    fun testFetchMovieDanmakuSession() = runBlocking {

        val session = provider.fetch("Violet Evergarden The Movie", "Complete Collection")

        assertNotNull(session)
    }

}
