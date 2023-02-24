package com.hoseus.short_url.model

import com.fasterxml.jackson.annotation.JsonProperty
import io.quarkus.runtime.annotations.RegisterForReflection
import org.hibernate.validator.constraints.URL
import javax.persistence.*
import javax.validation.constraints.NotNull

@RegisterForReflection
data class ShortenUrlDto(
	@NotNull(message = "url is mandatory")
	@URL(message = "invalid url format")
	val url: String? = null,
)

@RegisterForReflection
data class ShortUrlDto(
	val id: String,
	@JsonProperty("short_url")
	val shortUrl: String,
	@JsonProperty("original_url")
	val originalUrl: String
)

@RegisterForReflection
@Entity
@Table(name = "short_url")
data class ShortUrlModel(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "private_id")
	val privateId: Long? = null,
	@Column(name = "public_id")
	val publicId: String,
	@Column(name = "short_url")
	val shortUrl: String,
	@Column(name = "original_url")
	val originalUrl: String,
)
