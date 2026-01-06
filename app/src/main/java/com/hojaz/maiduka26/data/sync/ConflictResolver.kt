package com.hojaz.maiduka26.data.sync

import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles conflict resolution for data synchronization.
 * Implements strategies for resolving conflicts between local and remote data.
 */
@Singleton
class ConflictResolver @Inject constructor() {

    /**
     * Conflict resolution strategy.
     */
    enum class Strategy {
        /** Remote data wins, overwrites local changes */
        REMOTE_WINS,
        /** Local data wins, keeps local changes */
        LOCAL_WINS,
        /** Most recent update wins based on timestamp */
        LAST_WRITE_WINS,
        /** Merge both changes (requires custom logic) */
        MERGE,
        /** Manual resolution required */
        MANUAL
    }

    /**
     * Represents a data conflict between local and remote versions.
     */
    data class Conflict<T>(
        val entityId: String,
        val entityType: String,
        val localData: T,
        val remoteData: T,
        val localUpdatedAt: Long,
        val remoteUpdatedAt: Long
    )

    /**
     * Resolves a conflict using the specified strategy.
     */
    fun <T> resolve(
        conflict: Conflict<T>,
        strategy: Strategy = Strategy.LAST_WRITE_WINS
    ): T {
        Timber.d("Resolving conflict for ${conflict.entityType}:${conflict.entityId} using $strategy")

        return when (strategy) {
            Strategy.REMOTE_WINS -> conflict.remoteData
            Strategy.LOCAL_WINS -> conflict.localData
            Strategy.LAST_WRITE_WINS -> {
                if (conflict.localUpdatedAt > conflict.remoteUpdatedAt) {
                    conflict.localData
                } else {
                    conflict.remoteData
                }
            }
            Strategy.MERGE -> {
                // For merge, we'd need custom logic per entity type
                // Default to last write wins
                if (conflict.localUpdatedAt > conflict.remoteUpdatedAt) {
                    conflict.localData
                } else {
                    conflict.remoteData
                }
            }
            Strategy.MANUAL -> {
                // Store for manual resolution
                // Default to remote for now
                conflict.remoteData
            }
        }
    }

    /**
     * Determines the default strategy for an entity type.
     */
    fun getDefaultStrategy(entityType: String): Strategy {
        return when (entityType) {
            "sale", "sale_item", "sale_payment" -> Strategy.LOCAL_WINS // Sales created offline should be preserved
            "product" -> Strategy.LAST_WRITE_WINS
            "customer" -> Strategy.LAST_WRITE_WINS
            "shop_settings" -> Strategy.REMOTE_WINS // Settings typically managed centrally
            else -> Strategy.LAST_WRITE_WINS
        }
    }

    /**
     * Marks a conflict for manual resolution.
     */
    fun markForManualResolution(
        entityId: String,
        entityType: String
    ) {
        Timber.d("Marking $entityType:$entityId for manual resolution")
        // TODO: Store in a conflicts table for user review
    }
}

