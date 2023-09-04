package pt.isel.ls.boardio.domain.lists.api

import org.http4k.core.Method.DELETE
import org.http4k.core.Method.GET
import org.http4k.core.Method.PATCH
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.bind
import org.http4k.routing.routes
import pt.isel.ls.boardio.app.api.AppApi.Companion.request
import pt.isel.ls.boardio.app.api.utils.decodeAs
import pt.isel.ls.boardio.app.api.utils.getIntParameter
import pt.isel.ls.boardio.app.api.utils.getIntQuery
import pt.isel.ls.boardio.app.api.utils.getOptionalIntQueries
import pt.isel.ls.boardio.app.api.utils.getToken
import pt.isel.ls.boardio.app.api.utils.json
import pt.isel.ls.boardio.app.api.utils.message
import pt.isel.ls.boardio.domain.lists.services.ListsServices

class ListsApi(private val services: ListsServices) {

    val routes = routes(
        "/" bind POST to request { createList(it) },
        "/{listId}" bind GET to request { getList(it) },
        "/{listId}" bind PATCH to request { updateList(it) },
        "/{listId}" bind DELETE to request { deleteList(it) },
        "/{listId}/cards" bind GET to request { getListCards(it) },
        "/{listId}/move" bind PATCH to request { moveList(it) }
    )

    private fun createList(request: Request): Response {
        val token = request.getToken()
        val list = request.decodeAs<CreateListRequest>()
        val listId = services.createList(token, list.name, list.boardId)
        val response = CreateListResponse(listId)
        return Response(CREATED).json(response)
    }

    private fun getList(request: Request): Response {
        val token = request.getToken()
        val listId = request.getIntParameter("listId")
        val list = services.getList(token, listId)
        return Response(OK).json(list)
    }

    private fun getListCards(request: Request): Response {
        val token = request.getToken()
        val listId = request.getIntParameter("listId")
        val (skip, limit) = request.getOptionalIntQueries("skip", "limit")
        val cards = services.getListCards(token, listId, skip, limit)
        return Response(OK).json(cards)
    }

    private fun deleteList(request: Request): Response {
        val token = request.getToken()
        val listId = request.getIntParameter("listId")
        services.deleteList(token, listId)
        return Response(OK).message("List deleted successfully")
    }

    private fun moveList(request: Request): Response {
        val token = request.getToken()
        val listId = request.getIntParameter("listId")
        val index = request.getIntQuery("index")
        services.moveList(token, listId, index)
        return Response(OK).message("List moved successfully")
    }

    private fun updateList(request: Request): Response {
        val token = request.getToken()
        val listId = request.getIntParameter("listId")
        val list = request.decodeAs<UpdateListRequest>()
        services.updateList(token, listId, list.name, list.index, list.archived)
        return Response(OK).message("List updated successfully")
    }
}
