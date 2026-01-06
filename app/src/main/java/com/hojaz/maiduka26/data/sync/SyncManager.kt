package com.hojaz.maiduka26.data.sync

import android.content.Context
import androidx.work.*
import com.hojaz.maiduka26.util.Constants
import com.hojaz.maiduka26.util.NetworkMonitor
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages data synchronization between local database and remote server.
 * Handles offline-first data management and conflict resolution.
 */
@Singleton
class SyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val networkMonitor: NetworkMonitor
) {
    private val workManager = WorkManager.getInstance(context)

    companion object {
        private const val SYNC_WORK_NAME = "maiduka_sync_work"
        private const val SYNC_TAG = "sync"
    }

    /**
     * Schedules periodic background sync.
     */
    fun schedulePeriodicSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            Constants.SYNC_INTERVAL_MINUTES, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                Constants.SYNC_BACKOFF_DELAY_SECONDS,
                TimeUnit.SECONDS
            )
            .addTag(SYNC_TAG)
            .build()

        workManager.enqueueUniquePeriodicWork(
            SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )

        Timber.d("Periodic sync scheduled every ${Constants.SYNC_INTERVAL_MINUTES} minutes")
    }

    /**
     * Triggers an immediate sync if online.
     */
    fun triggerImmediateSync() {
        if (!networkMonitor.isOnline) {
            Timber.d("Skipping immediate sync - device is offline")
            return
        }

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                Constants.SYNC_BACKOFF_DELAY_SECONDS,
                TimeUnit.SECONDS
            )
            .addTag(SYNC_TAG)
            .build()

        workManager.enqueue(syncRequest)
        Timber.d("Immediate sync triggered")
    }

    /**
     * Cancels all pending sync work.
     */
    fun cancelSync() {
        workManager.cancelAllWorkByTag(SYNC_TAG)
        Timber.d("Sync work cancelled")
    }

    /**
     * Gets the current sync status.
     */
    fun getSyncStatus() = workManager.getWorkInfosByTagLiveData(SYNC_TAG)
}

