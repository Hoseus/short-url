package com.hoseus.short_url.controller

import com.hoseus.error.mapper.DefaultErrorMapper
import com.hoseus.error.model.BadRequestErrorDto
import com.hoseus.error.model.ErrorDto
import com.hoseus.error.model.RequestError
import io.quarkus.hibernate.validator.runtime.jaxrs.ResteasyReactiveViolationException
import org.jboss.resteasy.reactive.RestResponse
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class CustomErrorMapper: DefaultErrorMapper<RestResponse<ErrorDto>>(
	responseBuilder = { status, body ->
		RestResponse.status(
			RestResponse.Status.fromStatusCode(status),
			body
		)
	}
) {
	override fun mapException(e: Throwable): RestResponse<ErrorDto> =
		when(e) {
			is ResteasyReactiveViolationException -> {
				this.responseBuilder.build(
					status = 400,
					body = BadRequestErrorDto(code = "bad_request", errors = e.constraintViolations.map { RequestError(it.message) }.distinct())
				)
			}
			else -> super.mapException(e)
		}
}
