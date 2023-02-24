package com.hoseus.short_url.model

import io.quarkus.arc.Unremovable
import io.quarkus.runtime.annotations.ConfigGroup
import io.quarkus.runtime.annotations.ConfigPhase
import io.quarkus.runtime.annotations.ConfigRoot
import io.smallrye.config.ConfigMapping
import io.smallrye.config.WithDefault

/**
 * Short url configuration properties
 */
@Unremovable
@ConfigMapping(prefix = "hoseus.short-url")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
interface ShortUrlRuntimeConfiguration {
	/**
	 * The base path for building the resulting short url.
	 * This is the base uri to expose to clients using this REST API.
	 */
	fun basePath(): String

	/**
	 * Key generation properties.
	 * The key is unique and maps to a unique long url.
	 */
	fun key(): KeyProperties

	@ConfigGroup
	interface KeyProperties {
		/**
		 * Seed generation properties.
		 * The seed is used for unique key generation.
		 */
		fun seed(): SeedProperties

		@ConfigGroup
		interface SeedProperties {
			/**
			 * Seed generation redis properties.
			 */
			fun redis(): RedisProperties

			@ConfigGroup
			interface RedisProperties {
				/**
				 * Name of the redis key that will hold the counter.
				 */
				@WithDefault("short-url:seed-counter")
				fun key(): String
				/**
				 * Initial value of the counter if the key does not exist.
				 */
				@WithDefault("0")
				fun counterStartValue(): Long
			}
		}
	}
}
