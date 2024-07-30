package com.hoangvo.core.worker.upload

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.hoangkotlin.chatappsocial.core.chat_client.ChatClient
import com.hoangkotlin.chatappsocial.core.network.utils.ProgressCallback
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.map
import java.io.File
import java.util.UUID

@HiltWorker
class UploadProfileImageWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val chatClient: ChatClient
) : CoroutineWorker(appContext, workerParams) {


    override suspend fun doWork(): Result {
        val filePath = inputData.getString(DATA_IMAGE_FILE_PATH) ?: return Result.failure()
        val file = File(filePath)
        var result: Result = Result.success()
        chatClient.uploadProfileImage(file, object : ProgressCallback {
            override fun onSuccess(url: String) {
                setProgressAsync(workDataOf(SUCCESS to url)).get()
                val data: Data = Data.Builder().putAll(mapOf("url" to url)).build()
                result = Result.success(data)
            }

            override fun onError(error: Throwable) {
                setProgressAsync(workDataOf(ERROR to ERROR)).get()
                result = Result.failure()
            }

            override fun onProgress(bytesUploaded: Long, totalBytes: Long) {
                val progress = bytesUploaded / totalBytes.toFloat()

                setProgressAsync(workDataOf(PROGRESS to progress)).get()
            }
        })

        return result

    }

    companion object {
        private const val TAG = "UploadProfileImageWorker"
        private const val PROGRESS = "progress"
        private const val DATA_IMAGE_FILE_PATH = "image_uri"
        private const val SUCCESS = "success"
        private const val ERROR = "error"

        fun start(
            context: Context,
            filePath: String,
            userId: String
        ): UUID {
            val uploadProfileImageWorkRequest =
                OneTimeWorkRequestBuilder<UploadProfileImageWorker>()
                    .setConstraints(
                        Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                    )
                    .setInputData(
                        workDataOf(
                            DATA_IMAGE_FILE_PATH to filePath
                        )
                    )
                    .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "$userId:profile",
                ExistingWorkPolicy.REPLACE,
                uploadProfileImageWorkRequest
            )
            return uploadProfileImageWorkRequest.id
        }

        fun uploadWorkProgress(context: Context, requestId: UUID) =
            WorkManager.getInstance(context).getWorkInfoByIdFlow(requestId)
                .map { workInfo ->
                    when (workInfo.state) {
                        WorkInfo.State.ENQUEUED,
                        WorkInfo.State.RUNNING -> WorkerUploadState.InProgress(
                            workInfo.progress.getFloat(
                                PROGRESS,
                                0f
                            )
                        )

                        WorkInfo.State.SUCCEEDED -> WorkerUploadState.Success
                        WorkInfo.State.FAILED,
                        WorkInfo.State.BLOCKED,
                        WorkInfo.State.CANCELLED -> WorkerUploadState.Failure
                    }

                }
    }


}

sealed class WorkerUploadState {
    data object Success : WorkerUploadState()
    data object Failure : WorkerUploadState()
    data class InProgress(val progress: Float) : WorkerUploadState()
}