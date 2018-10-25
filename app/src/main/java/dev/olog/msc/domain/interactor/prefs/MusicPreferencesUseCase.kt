package dev.olog.msc.domain.interactor.prefs

import dev.olog.msc.domain.entity.LastMetadata
import dev.olog.msc.domain.gateway.prefs.MusicPreferencesGateway
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicPreferencesUseCase @Inject constructor(
        private val gateway: MusicPreferencesGateway
) {

    fun getLastIdInPlaylist(): Int = gateway.getLastIdInPlaylist()
    fun observeLastIdInPlaylist(): Observable<Int> = gateway.observeLastIdInPlaylist()

    fun setLastPositionInQueue(position: Int){
        gateway.setLastPositionInQueue(position)
    }
    fun observeLastPositionInQueue(): Observable<Int> = gateway.observeLastPositionInQueue()
    fun getLastPositionInQueue(): Int = gateway.getLastPositionInQueue()

    fun setLastIdInPlaylist(idInPlaylist: Int) {
        gateway.setLastIdInPlaylist(idInPlaylist)
    }

    fun getRepeatMode(): Int = gateway.getRepeatMode()
    fun setRepeatMode(mode: Int) {
        gateway.setRepeatMode(mode)
    }

    fun getShuffleMode(): Int = gateway.getShuffleMode()
    fun setShuffleMode(mode: Int) {
        gateway.setShuffleMode(mode)
    }

    fun getBookmark() : Long = gateway.getBookmark()
    fun setBookmark(bookmark: Long) {
        gateway.setBookmark(bookmark)
    }

    fun isMidnightMode() : Observable<Boolean> = gateway.isMidnightMode()

    fun setSkipToPreviousVisibility(visible: Boolean) {
        gateway.setSkipToPreviousVisibility(visible)
    }
    fun observeSkipToPreviousVisibility(): Observable<Boolean> = gateway.observeSkipToPreviousVisibility()

    fun setSkipToNextVisibility(visible: Boolean) {
        gateway.setSkipToNextVisibility(visible)
    }
    fun observeSkipToNextVisibility(): Observable<Boolean> = gateway.observeSkipToNextVisibility()

    fun getLastMetadata(): LastMetadata {
        return gateway.getLastMetadata()
    }

    fun setLastMetadata(metadata: LastMetadata){
        gateway.setLastMetadata(metadata)
    }

    fun observeLastMetadata(): Observable<LastMetadata> {
        return gateway.observeLastMetadata()
    }

    fun setDefault(): Completable {
        return gateway.setDefault()
    }

    fun observeCrossFade(asMillis: Boolean): Observable<Int> {
        var obs = gateway.observeCrossFade()
        if (asMillis){
           obs = obs.map { it * 1000 }
        }
        return obs
    }

    fun observeGapless(): Observable<Boolean> {
        return gateway.observeGapless()
    }

    fun observePlaybackSpeed(): Observable<Float> {
        return gateway.observePlaybackSpeed()
    }

    fun setPlaybackSpeed(speed: Float) {
        gateway.setPlaybackSpeed(speed)
    }

    fun getPlaybackSpeed(): Float {
        return gateway.getPlaybackSpeed()
    }

}