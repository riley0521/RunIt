package com.rfdotech.run.network

import androidx.annotation.Keep
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.rfdotech.core.domain.auth.UserId
import com.rfdotech.core.domain.printAndThrowCancellationException
import com.rfdotech.core.domain.run.RemoteRunDataSource
import com.rfdotech.core.domain.run.Run
import com.rfdotech.core.domain.run.RunId
import com.rfdotech.core.domain.util.DataError
import com.rfdotech.core.domain.util.EmptyResult
import com.rfdotech.core.domain.util.Result
import kotlinx.coroutines.tasks.await
import java.util.UUID

@Keep
class FirebaseRemoteRunDataSource : RemoteRunDataSource {

    private val runCollection = Firebase.firestore.collection("runs")
    private val storage = Firebase.storage

    override suspend fun getAll(userId: UserId): Result<List<Run>, DataError.Network> {
        val result = try {
            runCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()
                .toObjects<FirestoreRunDto>()
        } catch (e: Exception) {
            e.printAndThrowCancellationException()
            return Result.Error(DataError.Network.SERVER_ERROR)
        }

        val runs = result.map { run -> run.toRun() }

        return Result.Success(runs)
    }

    override suspend fun upsert(userId: UserId, run: Run, mapPicture: ByteArray): Result<Run, DataError.Network> {
        var existingRun = getRunDtoById(run.id.orEmpty()) ?: run.toRunDtoV2(userId)

        if (run.mapPictureUrl == null) {
            val storageRef = getStorageRefByUserId(userId)
            val isSuccessful = try {
                storageRef.putBytes(mapPicture).await()
                existingRun = existingRun.copy(mapPictureUrl = storageRef.downloadUrl.await().toString())
                true
            } catch (e: Exception) {
                e.printAndThrowCancellationException()
                false
            }

            if (!isSuccessful) {
                return Result.Error(DataError.Network.SERVER_ERROR)
            }
        }

        return try {
            runCollection.document(existingRun.id).set(existingRun).await()
            Result.Success(existingRun.toRun())
        } catch (e: Exception) {
            e.printAndThrowCancellationException()
            Result.Error(DataError.Network.SERVER_ERROR)
        }
    }

    private suspend fun getRunDtoById(id: String): FirestoreRunDto? {
        val result = try {
            runCollection.document(id).get().await().toObject<FirestoreRunDto>()
        } catch (e: Exception) {
            e.printAndThrowCancellationException()
            null
        }
        return result
    }

    private fun getStorageRefByUserId(userId: String): StorageReference {
        val randomName = "run_" + UUID.randomUUID().toString()
        return storage.reference.child("$RUN_IMAGES_BUCKET/$userId/${randomName}.jpg")
    }

    private suspend fun deleteRunImage(url: String) {
        try {
            storage.getReferenceFromUrl(url).delete().await()
        } catch (e: Exception) {
            e.printAndThrowCancellationException()
        }
    }

    override suspend fun deleteById(id: RunId): EmptyResult<DataError.Network> {
        val existingRun = getRunDtoById(id) ?: return Result.Error(DataError.Network.SERVER_ERROR)

        return try {
            // Remove the document
            runCollection.document(existingRun.id).delete().await()

            // Remove the image from the bucket.
            existingRun.mapPictureUrl?.let { deleteRunImage(it) }
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printAndThrowCancellationException()
            Result.Error(DataError.Network.SERVER_ERROR)
        }
    }

    companion object {
        const val RUN_IMAGES_BUCKET = "run_images"
    }
}