package com.hoseus.short_url.repository

import com.hoseus.error.exception.NotFoundException
import com.hoseus.error.exception.TimeoutException
import com.hoseus.error.exception.UnexpectedException
import com.hoseus.short_url.model.ShortUrlDto
import com.hoseus.short_url.model.ShortUrlModel
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import javax.enterprise.context.ApplicationScoped
import javax.persistence.NoResultException
import javax.persistence.QueryTimeoutException
import javax.transaction.Transactional

@ApplicationScoped
class ShortUrlRepository: PanacheRepository<ShortUrlModel> {

	@Transactional
	fun save(shortUrlDto: ShortUrlDto): Result<ShortUrlDto> =
		runCatching {
			this.persist(
				ShortUrlModel(
					publicId = shortUrlDto.id,
					shortUrl = shortUrlDto.shortUrl,
					originalUrl = shortUrlDto.originalUrl
				)
			)
		}.fold(
			{
				Result.success(shortUrlDto)
			},
			{ e ->
				when(e) {
					is QueryTimeoutException -> TimeoutException(message = "Timeout persisting short url", cause = e)
					else -> UnexpectedException(cause = e)
				}.let { Result.failure(it) }
			}
		)

	fun findById(id: String): Result<ShortUrlDto> =
		runCatching {
			this.find("public_id", id).singleResult()
		}.fold(
			{
				Result.success(
					ShortUrlDto(
						id = it.publicId,
						shortUrl = it.shortUrl,
						originalUrl = it.originalUrl
					)
				)
			},
			{ e ->
				when(e) {
					is NoResultException -> NotFoundException(message = "short url not found with id $id", cause = e)
					is QueryTimeoutException -> TimeoutException(message = "Timeout finding short url in database", cause = e)
					else -> UnexpectedException(cause = e)
				}.let { Result.failure(it) }
			}
		)
}
