package com.hoseus.short_url.repository

import com.hoseus.error.exception.CommunicationException
import com.hoseus.error.exception.UnexpectedException
import io.quarkus.redis.datasource.RedisDataSource
import io.quarkus.redis.datasource.value.SetArgs
import io.quarkus.redis.datasource.value.ValueCommands
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.util.concurrent.CompletionException
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class SeedRepository(
	private val redisDataSource: RedisDataSource,
	@ConfigProperty(name = "hoseus.short-url.key.seed.redis.key") private val seedCounterRedisKey: String,
	@ConfigProperty(name = "hoseus.short-url.key.seed.redis.counter-start-value") private val seedCounterStartValue: Long,
) {
	private val redisValueCommands: ValueCommands<String, Long> = this.redisDataSource.value(Long::class.java)

	init {
		if (this.seedCounterStartValue > 0) {
			this.redisValueCommands.set(this.seedCounterRedisKey, this.seedCounterStartValue, SetArgs().nx())
		}
	}

	fun next(): Result<Long> =
		runCatching {
			this.redisValueCommands.incr(this.seedCounterRedisKey)
		}.fold(
			{
				Result.success(it)
			},
			{ e ->
				when(e) {
					is CompletionException -> CommunicationException(cause = e, message = "Error establishing connection with redis")
					else -> UnexpectedException(cause = e)
				}.let { Result.failure(it) }
			}
		)
}
