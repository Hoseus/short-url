package com.hoseus.short_url.service

import com.hoseus.logging.errorExtended
import com.hoseus.logging.infoExtended
import com.hoseus.short_url.model.ShortUrlDto
import com.hoseus.short_url.repository.ShortUrlRepository
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jboss.logging.Logger
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.core.UriBuilder

@ApplicationScoped
class ShortUrlService(
	private val keyService: KeyService,
	private val repository: ShortUrlRepository,
	@ConfigProperty(name = "hoseus.short-url.base-path") private val shortUrlBasePath: String,
) {
	fun shortenUrl(url: String): Result<ShortUrlDto> =
		run {
			logger.infoExtended { f, _ -> f.start("shortenUrl", "Url: $url") }
		}.let {
			this.createFromUrl(url)
		}.fold(
			{
				this.persist(it)
			},
			{
				Result.failure(it)
			}
		)
		.onSuccess {
			logger.infoExtended { f, h -> f.end("shortenUrl", "ShortUrlDto: ${h.toJson(it)}") }
		}
		.onFailure {
			logger.errorExtended(it) { f, _ -> f.error("shortenUrl", "Url: $url. Message: ${it.message}") }
		}

	fun findById(id: String) =
		run {
			logger.infoExtended { f, _ -> f.start("findById", "Id: $id") }
		}.let {
			this.repository.findById(id)
		}.onSuccess {
			logger.infoExtended { f, h -> f.end("findById", "ShortUrlDto: ${h.toJson(it)}") }
		}
		.onFailure {
			logger.errorExtended(it) { f, _ -> f.error("findById", "Id: $id. Message: ${it.message}") }
		}

	private fun createFromUrl(url: String): Result<ShortUrlDto> =
		run {
			logger.infoExtended { f, _ -> f.start("createFromUrl", "Url: $url") }
		}.let {
			this.keyService.next()
		}.map {
			ShortUrlDto(
				id = it,
				shortUrl = UriBuilder.fromPath(this.shortUrlBasePath).path(it).build().toString(),
				originalUrl = url
			)
		}.onSuccess {
			logger.infoExtended { f, h -> f.end("createFromUrl", "ShortUrlDto: ${h.toJson(it)}") }
		}

	private fun persist(shortUrlDto: ShortUrlDto): Result<ShortUrlDto> =
		run {
			logger.infoExtended { f, h -> f.start("persist", "ShortUrlDto: ${h.toJson(shortUrlDto)}") }
		}.let {
			this.repository.save(shortUrlDto)
		}.onSuccess {
			logger.infoExtended { f, h -> f.end("persist", "ShortUrlDto: ${h.toJson(it)}") }
		}

	companion object {
		private val logger: Logger = Logger.getLogger(ShortUrlService::class.java)
	}
}
