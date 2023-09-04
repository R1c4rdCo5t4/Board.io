package pt.isel.ls.boardio.app.utils

import org.http4k.core.Method
import org.http4k.filter.AllowAll
import org.http4k.filter.CorsPolicy
import org.http4k.filter.OriginPolicy
import org.http4k.filter.ServerFilters

// cross-origin filter to allow requests from swagger
fun corsFilter() = ServerFilters.Cors(
    CorsPolicy(
        OriginPolicy.AllowAll(),
        listOf("*"),
        Method.values().toList(),
        true
    )
)
