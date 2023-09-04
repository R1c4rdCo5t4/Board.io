package pt.isel.ls.boardio.domain.cards.api

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
import pt.isel.ls.boardio.app.api.utils.getToken
import pt.isel.ls.boardio.app.api.utils.json
import pt.isel.ls.boardio.app.api.utils.message
import pt.isel.ls.boardio.domain.cards.services.CardsServices

class CardsApi(private val services: CardsServices) {

    val routes = routes(
        "/" bind POST to request { createCard(it) },
        "/{cardId}" bind GET to request { getCard(it) },
        "/{cardId}" bind PATCH to request { updateCard(it) },
        "/{cardId}" bind DELETE to request { deleteCard(it) },
        "/{cardId}/move" bind PATCH to request { moveCard(it) },
        "/{cardId}/duedate" bind PATCH to request { updateCardDueDate(it) }
    )

    private fun createCard(request: Request): Response {
        val token = request.getToken()
        val card = request.decodeAs<CreateCardRequest>()
        val cardId = services.createCard(token, card.name, card.description, card.listId)
        val response = CreateCardResponse(cardId)
        return Response(CREATED).json(response)
    }

    private fun getCard(request: Request): Response {
        val token = request.getToken()
        val cardId = request.getIntParameter("cardId")
        val card = services.getCard(token, cardId)
        return Response(OK).json(card)
    }

    private fun moveCard(request: Request): Response {
        val token = request.getToken()
        val cardId = request.getIntParameter("cardId")
        val listId = request.getIntQuery("listId")
        val index = request.getIntQuery("index")
        services.moveCard(token, cardId, listId, index)
        return Response(OK).message("Card moved successfully")
    }

    private fun deleteCard(request: Request): Response {
        val token = request.getToken()
        val cardId = request.getIntParameter("cardId")
        services.deleteCard(token, cardId)
        return Response(OK).message("Card deleted successfully")
    }

    private fun updateCard(request: Request): Response {
        val token = request.getToken()
        val cardId = request.getIntParameter("cardId")
        val card = request.decodeAs<UpdateCardRequest>()
        services.updateCard(token, cardId, card.name, card.description, card.archived)
        return Response(OK).message("Card updated successfully")
    }

    private fun updateCardDueDate(request: Request): Response {
        val token = request.getToken()
        val cardId = request.getIntParameter("cardId")
        val dueDate = request.decodeAs<UpdateCardDueDateRequest>().dueDate
        services.updateCardDueDate(token, cardId, dueDate)
        return Response(OK).message("Card updated successfully")
    }
}
