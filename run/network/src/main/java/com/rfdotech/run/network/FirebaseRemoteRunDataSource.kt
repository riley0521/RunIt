package com.rfdotech.run.network

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.rfdotech.core.domain.DispatcherProvider
import com.rfdotech.core.domain.auth.UserId
import com.rfdotech.core.domain.printAndThrowCancellationException
import com.rfdotech.core.domain.run.RemoteRunDataSource
import com.rfdotech.core.domain.run.Run
import com.rfdotech.core.domain.run.RunId
import com.rfdotech.core.domain.util.DataError
import com.rfdotech.core.domain.util.EmptyResult
import com.rfdotech.core.domain.util.Result
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class FirebaseRemoteRunDataSource(
    private val dispatcherProvider: DispatcherProvider
) : RemoteRunDataSource {

    private val runCollection = Firebase.firestore.collection("runs")
    private val storage = Firebase.storage

    override suspend fun getAll(userId: UserId): Result<List<Run>, DataError.Network> = withContext(dispatcherProvider.io) {
        val result = runCollection
            .whereEqualTo("userId", userId)
            .get()
            .await()
            .toObjects<FirestoreRunDto>()

        val runs = result.map { run -> run.toRun() }

        Result.Success(runs)
    }

    override suspend fun upsert(userId: UserId, run: Run, mapPicture: ByteArray): Result<Run, DataError.Network> = withContext(dispatcherProvider.io) {
        var existingRun = getRunDtoById(run.id.orEmpty()) ?: run.toRunDtoV2(userId)

        if (run.mapPictureUrl == null) {
            val storageRef = getStorageRefByUserId(userId)
            val isSuccessful = try {
                storageRef.putBytes(mapPicture).await()
                existingRun = existingRun.copy(mapPictureUrl = storageRef.downloadUrl.toString())
                true
            } catch (e: Exception) {
                e.printAndThrowCancellationException()
                false
            }

            if (!isSuccessful) {
                return@withContext Result.Error(DataError.Network.SERVER_ERROR)
            }
        }

        runCollection.document(existingRun.id).set(existingRun).await()

        return@withContext Result.Success(existingRun.toRun())
    }

    private suspend fun getRunDtoById(id: String): FirestoreRunDto? = withContext(dispatcherProvider.io) {
        val result = runCollection.document(id).get().await().toObject<FirestoreRunDto>()
        return@withContext result
    }

    private fun getStorageRefByUserId(userId: String): StorageReference {
        val randomName = "run_" + UUID.randomUUID().toString()
        return storage.reference.child("$RUN_IMAGES_BUCKET/$userId/${randomName}.jpg")
    }

    private suspend fun deleteRunImage(url: String) = withContext(dispatcherProvider.io) {
        try {
            storage.getReferenceFromUrl(url).delete().await()
        } catch (e: Exception) {
            e.printAndThrowCancellationException()
        }
    }

    override suspend fun deleteById(id: RunId): EmptyResult<DataError.Network> = withContext(dispatcherProvider.io) {
        val existingRun = getRunDtoById(id) ?: return@withContext Result.Error(DataError.Network.SERVER_ERROR)

        runCollection.document(existingRun.id).delete().await()
        existingRun.mapPictureUrl?.let { deleteRunImage(it) }

        return@withContext Result.Success(Unit)
    }

    companion object {
        const val RUN_IMAGES_BUCKET = "run_images"
    }
}