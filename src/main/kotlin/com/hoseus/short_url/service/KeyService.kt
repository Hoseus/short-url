package com.hoseus.short_url.service

import com.aventrix.jnanoid.jnanoid.NanoIdUtils
import com.hoseus.error.exception.UnexpectedException
import com.hoseus.logging.errorExtended
import com.hoseus.logging.infoExtended
import org.jboss.logging.Logger
import javax.enterprise.context.ApplicationScoped
import kotlin.random.Random
import kotlin.random.asJavaRandom

@ApplicationScoped
class KeyService(
	private val seedService: SeedService,
) {
	fun next(): Result<String> =
		run {
			logger.infoExtended { f, _ -> f.start("next", "Generating next key") }
		}.let {
			this.nextSeed()
		}.fold(
			{
				this.nextKey(it)
			},
			{
				Result.failure(it)
			}
		)
		.onSuccess {
			logger.infoExtended { f, _ -> f.end("next", "Key: $it") }
		}
		.onFailure {
			logger.errorExtended(it) { f, _ -> f.error("next", "Message: ${it.message}") }
		}

	private fun nextSeed(): Result<Long> = this.seedService.next()

	private fun nextKey(seed: Long): Result<String> =
		runCatching {
			NanoIdUtils.randomNanoId(Random(seed).asJavaRandom(), alphabet, 7)
		}.fold(
			{
				Result.success(it)
			},
			{
				Result.failure(UnexpectedException(cause = it))
			}
		)

	companion object {
		private val logger: Logger = Logger.getLogger(KeyService::class.java)
		private val alphabet = (
			('0'..'9').toList() +
			('a'..'z').toList() +
			('A'..'Z').toList()
		).toCharArray()
	}
}
