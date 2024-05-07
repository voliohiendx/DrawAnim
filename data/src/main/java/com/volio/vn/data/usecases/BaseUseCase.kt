package com.volio.vn.data.usecases

open class BaseUseCase {

    suspend fun runWithExceptionCheck(
        exception: (ex: Exception) -> Unit = {},
        invoke: suspend () -> Unit
    ) {
        try {
            invoke.invoke()
        } catch (ex: Exception) {
            ex.printStackTrace()
            exception.invoke(ex)
        }
    }


    suspend fun <T> runWithCheckException(
        exception: (ex: Exception) -> Unit = {},
        invoke: suspend () -> T
    ): T? {
        return try {
            invoke.invoke()
        } catch (ex: Exception) {
            ex.printStackTrace()
            exception.invoke(ex)
            null
        }
    }

    suspend fun <T : Any> runWithCheckExceptionByResult(
        exception: (ex: Exception) -> Unit = {},
        invoke: suspend () -> T
    ): Result<T> {
        return try {
            val data = invoke.invoke()
            Result.success(data)
        } catch (ex: Exception) {
            ex.printStackTrace()
            exception.invoke(ex)
            Result.failure(ex)
        }
    }

}