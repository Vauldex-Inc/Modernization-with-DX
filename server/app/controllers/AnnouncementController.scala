package tech.vauldex.consulting_station.controllers

import java.util.UUID
import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import cats.implicits._
import tech.vauldex.consulting_station._
import utils.security._
import utils.CustomForms._
import models.domain.Announcement
import models.repo.AnnouncementRepo
import models.util.JsonSerializers._

@Singleton
class AnnouncementController @Inject()(
    secureConsoleAction: SecureConsoleAction,
    announcementRepo: AnnouncementRepo,
    implicit val ec: ExecutionContext,
    val controllerComponents: ControllerComponents)
  extends BaseController with play.api.i18n.I18nSupport {

  private val announcementForm = Form(tuple(
    "title" -> optional(text),
    "details" -> nonEmptyText))

  def addAnnouncement = secureConsoleAction.async { implicit request =>
    announcementForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(formWithErrors.errorsAsJson)),
      { case (title, details) =>
        announcementRepo.add(Announcement(title, details)).map { count =>
          if(count == 1) Created
          else InternalServerError
        }
      })
  }

  def allAnnouncement = Action.async { implicit request =>
    searchForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(formWithErrors.errorsAsJson)),
      { case (searchType, keyword, marker) =>
        announcementRepo.all(marker).map (page  => Ok(page.copy(page.results).toJson))
      })
  }

  def getLatestAnnouncement = Action.async { implicit request =>
    announcementRepo.getLatest
      .map(announcement => Ok(Json.toJson(announcement))).getOrElse(NotFound)
  }

  def getAnnouncement(id: UUID) = secureConsoleAction.async { implicit request =>
    announcementRepo
      .find(id).map(announcement => Ok(Json.toJson(announcement))).getOrElse(NotFound)
  }

  def updateAnnouncement(id: UUID) = secureConsoleAction.async { implicit request =>
    announcementForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(formWithErrors.errorsAsJson)),
      { case (title, details) =>
        announcementRepo.update(id, title, details).map { count =>
          if(count == 1) NoContent
          else InternalServerError
        }
      })
  }

  def deleteAnnouncement(id: UUID) = secureConsoleAction.async { implicit request =>
    announcementRepo.setState(id, true).map(_ => Ok)
  }
}
