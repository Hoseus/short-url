package com.hoseus.short_url.controller

import com.hoseus.error.model.ErrorDto
import com.hoseus.logging.infoExtended
import com.hoseus.short_url.model.ShortUrlDto
import com.hoseus.short_url.model.ShortenUrlDto
import com.hoseus.short_url.service.ShortUrlService
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.RestResponse
import org.jboss.resteasy.reactive.server.ServerExceptionMapper
import javax.validation.constraints.NotNull
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path("/v1/short-url")
class ShortUrlController(
	private val service: ShortUrlService,
	private val errorMapper: CustomErrorMapper,
) {
    @POST
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun shortenUrl(
        @NotNull(message = "Body is mandatory")
        shortenUrlDto: ShortenUrlDto,
    ): RestResponse<ShortUrlDto> =
		run {
			logger.infoExtended { f, _ -> f.start("shortenUrl", "ShortenUrlDto: $shortenUrlDto") }
		}
		.let {
			this.service.shortenUrl(shortenUrlDto.url!!)
				.fold(
					{ RestResponse.ok(it) },
					{ throw it }
				)
		}
		.also { response ->
			logger.infoExtended { f, h ->
				f.end(
					"shortenUrl",
					"Status: ${response.status}. Body: ${h.toJson(response.entity)}"
				)
			}
		}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	fun findById(
		@PathParam("id") id: String
	): RestResponse<ShortUrlDto> =
		run {
			logger.infoExtended { f, _ -> f.start("findById", "Id: $id") }
		}
		.let {
			this.service.findById(id)
				.fold(
					{ RestResponse.ok(it) },
					{ throw it }
				)
		}
		.also { response ->
			logger.infoExtended { f, h ->
				f.end(
					"findById",
					"Status: ${response.status}. Body: ${h.toJson(response.entity)}"
				)
			}
		}

    @ServerExceptionMapper
    fun mapException(e: Throwable): RestResponse<ErrorDto> =
        this.errorMapper.mapException(e)
			.also { response -> logger.infoExtended { f, h -> f.end("mapException", "Status: ${response.status}. Body: ${h.toJson(response.entity)}") } }

	companion object {
		private val logger: Logger = Logger.getLogger(ShortUrlController::class.java)
	}
}
