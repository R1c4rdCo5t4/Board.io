package pt.isel.ls.boardio.app

import org.http4k.routing.ResourceLoader
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.routing.singlePageApp
import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pt.isel.ls.boardio.app.api.AppApi
import pt.isel.ls.boardio.app.database.database.AppDatabase
import pt.isel.ls.boardio.app.database.datamem.AppDataMem
import pt.isel.ls.boardio.app.services.AppServices
import pt.isel.ls.boardio.app.utils.corsFilter

class AppServer(private val port: Int, url: String? = null) {
    private val server: Http4kServer

    init {
        val database = if (url != null) AppDatabase(url) else AppDataMem()
        val tasksServices = AppServices(database)
        val tasksApi = AppApi(tasksServices)
        val app = routes(
            "/api" bind tasksApi.routes,
            singlePageApp(ResourceLoader.Directory("static-content"))
        ).withFilter(corsFilter())
        server = app.asServer(Jetty(port))
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger("pt.isel.ls.boardio")
    }

    fun start() {
        server.start()
        logger.info("Server started listening in http://localhost:$port")
    }

    fun stop() {
        server.stop()
        logger.info("Server stopped")
    }
}
