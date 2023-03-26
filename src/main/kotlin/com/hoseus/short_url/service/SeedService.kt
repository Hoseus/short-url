package com.hoseus.short_url.service

import com.hoseus.logging.errorExtended
import com.hoseus.logging.infoExtended
import com.hoseus.short_url.repository.SeedRepository
import org.jboss.logging.Logger
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class SeedService(
	private val seedRepository: SeedRepository,
) {

	fun next(): Result<Long> =
		run {
			logger.infoExtended { f, _ -> f.start("next", "Generating next seed") }
		}.let {
			this.seedRepository.next()
		}.onSuccess {
			logger.infoExtended { f, _ -> f.end("next", "Seed: $it") }
		}.onFailure {
			logger.errorExtended(it) { f, _ -> f.error("next", "Message: ${it.message}") }
		}

	companion object {
		private val logger: Logger = Logger.getLogger(SeedService::class.java)
	}
}
