package com.starkk.sdk.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig

/**
 * Creates a platform-specific [HttpClient] with the given configuration block.
 *
 * - **Android**: Uses the OkHttp engine.
 * - **iOS**: Uses the Darwin engine.
 */
internal expect fun createPlatformHttpClient(
    config: HttpClientConfig<*>.() -> Unit = {},
): HttpClient

