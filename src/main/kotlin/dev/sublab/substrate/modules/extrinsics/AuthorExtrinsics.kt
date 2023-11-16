package dev.sublab.substrate.modules.extrinsics

import dev.sublab.substrate.rpcClient.Rpc

interface SubmitExtrinsicsModule {
    suspend fun submitCall(callData: String): String?
}

class AuthorExtrinsics(private val rpc: Rpc) : SubmitExtrinsicsModule {
    override suspend fun submitCall(callData: String) = rpc.sendRequest<String, String> {
        method = "author_submitExtrinsic"
        responseType = String::class
        params = listOf(callData)
        paramsType = String::class
    }
}