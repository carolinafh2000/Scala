package controllers

import models.MovieRepository
import play.api.mvc._
import play.api.libs.json.Json

import javax.inject._
import models.{Movie, MovieRepository}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class MovieController @Inject()(cc: ControllerComponents,
                                movieRepository: MovieRepository)
    extends AbstractController(cc) {

  implicit val serializador = Json.format[Movie]
  val logger = play.Logger.of("MovieController")

  def getMovies = Action.async {
    movieRepository.getAll
      .map(movies => {
        val j = Json.obj("data" -> movies, "message" -> "Movies Listed")
        Ok(j)
      })
      .recover {
        case ex =>
          logger.error("Fallo en getMovies", ex)
          InternalServerError(s"Hubo un error: ${ex.getLocalizedMessage}")
      }
  }
  def getMovie(id: String) = Action.async {
    movieRepository
      .getOne(id)
      .map(movie => {
        val j = Json.obj("data" -> movie, "message" -> "Movie Listed")
        Ok(j)
      })
      .recover {
        case ex =>
          logger.error("Fallo en getMovie", ex)
          InternalServerError(s"Hubo un error: ${ex.getLocalizedMessage}")
      }
  }
  def createMovie = Action.async(parse.json) { request =>
    val validator = request.body.validate[Movie]
    validator.asEither match {
      case Left(error) => Future.successful(BadRequest(error.toString()))
      case Right(movie) => {
        movieRepository
          .create(movie)
          .map(movies => {
            val j = Json.obj("data" -> movie, "message" -> "Movies created")
            Ok(j)
          })
          .recover {
            case ex =>
              logger.error("Fallo en createMovie", ex)
              InternalServerError(s"Hubo un error: ${ex.getLocalizedMessage}")
          }
      }
    }
  }
  def updateMovie(id: String) = Action.async(parse.json) { request =>
    val validator = request.body.validate[Movie]
    validator.asEither match {
      case Left(error) => Future.successful(BadRequest(error.toString()))
      case Right(movie) => {
        movieRepository
          .update(id, movie)
          .map(movies => {
            val j = Json.obj("data" -> movie, "message" -> "Movies updated")
            Ok(j)
          })
          .recover {
            case ex =>
              logger.error("Fallo en updateMovie", ex)
              InternalServerError(s"Hubo un error: ${ex.getLocalizedMessage}")
          }
      }
    }
  }
  def deleteMovie(id: String) = Action.async {
    movieRepository
      .delete(id)
      .map(movie => {
        val j = Json.obj("data" -> movie, "message" -> "Movies Listed")
        Ok(j)
      })
      .recover {
        case ex =>
          logger.error("Fallo en deleteMovie", ex)
          InternalServerError(s"Hubo un error: ${ex.getLocalizedMessage}")
      }
  }
}
