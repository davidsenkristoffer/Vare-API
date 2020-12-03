package h577870.routes

import h577870.dao.VareService
import h577870.entity.VareClass
import h577870.entity.VareEgenskaperClass
import h577870.utils.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*

//Objekt for databaseoperasjoner.
@KtorExperimentalAPI

private fun Route.vareRoutesGet() {
    authenticate {
        route("/vare") {
            //Alle varer
            get {
                val vareliste = vareservice.hentAlleVarer()
                if (vareliste.isNotEmpty()) call.respond(vareliste)
                else call.respondText("Liste med varer er tom / får ikke kontakt med database.",
                        status = HttpStatusCode.NotFound)
            }
            route("/{ean}") {
                /*
            Henter vare med ean-kode.
            Errorhandling ser litt rotete ut, wrapper det i en funksjon senere.
             */
                get {
                    val ean = call.parameters["ean"] ?: call.respondText("Bad request",
                            status = HttpStatusCode.BadRequest)
                    val escaped = ean.toString()
                    //Validering av ean
                    runCatching { require(validator.validateEan(escaped)) }
                            .onFailure { ErrorMessages.returnMessage(it, call) }

                    val vare = vareservice.hentVareMedEan(escaped)
                            ?: call.respondText("Varen finnes ikke",
                                    status = HttpStatusCode.NotFound
                            )
                    call.respond(vare)
                }
            } // END ean
        } //END vare
    }
}

/*
TODO:Authentication
 */

@KtorExperimentalAPI
private fun Route.vareRoutesPost() {
    authenticate {
        route("/vare") {
            /*
            TODO: Escape JSON-obj.
             */
            put("updatePris") {
                runCatching {
                    val body = call.receive<VareClass>()
                    when (vareservice.oppdaterVare(body) ?: 0) {
                        0 -> call.respondText("Error updating...", status = HttpStatusCode.NotFound)
                        else -> call.respondText("Updated price on vare with ean ${body.ean} to ${body.pris}")
                    }
                }.onFailure { ErrorMessages.returnMessage(it, call) }
            }//end PUT

            post("nyVare") {
                runCatching {
                    val body = call.receive<VareClass>()
                    vareservice.leggTilVare(body)
                    vareeservice.leggTilEgenskap(
                        VareEgenskaperClass(
                            ean = body.ean,
                            beholdning = 10.0,
                            prestasjonslager = 5,
                            dekningsperiode = 7,
                            snittsalg = 0.0
                        )
                    )
                    call.respondText(
                        "Successfully added vare with ean ${body.ean}",
                        status = HttpStatusCode.OK
                    )
                }.onFailure { ErrorMessages.returnMessage(it, call) }
            }
        }
    }
}

//Utvidelsesfunksjon for Application som henter endepunkter for vare.
@KtorExperimentalAPI
fun Application.registerVareRoutes() {
    routing {
        vareRoutesGet()
        vareRoutesPost()
    }
}

