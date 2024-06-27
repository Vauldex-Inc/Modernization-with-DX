package tech.vauldex.consulting_station.controllers

import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import cats.implicits._
import tech.vauldex.consulting_station.utils.{ MailSender, ReCaptchaValidator }

@Singleton
class ContactController @Inject()(
    implicit val ec: ExecutionContext,
    mailer: MailSender,
    reCaptchaValidator: ReCaptchaValidator,
    val controllerComponents: ControllerComponents)
  extends BaseController with play.api.i18n.I18nSupport {

  private val contactUsForm = Form(tuple(
    "name" -> nonEmptyText,
    "company_name" -> text,
    "email" -> email,
    "phone_number" -> nonEmptyText,
    "business" -> nonEmptyText,
    "content" -> nonEmptyText,
    "response" -> nonEmptyText))

  def contactUs = Action.async { implicit request =>
    contactUsForm.bindFromRequest.fold(
      formWithErrors =>  Future.successful(BadRequest(formWithErrors.errorsAsJson)),
      { case (name, companyName, email, phoneNumber, business, content, response) =>
        reCaptchaValidator.validate(response).fold(
          error => BadRequest(Json.obj("error_codes" -> error)),
          _ => {
            mailer.sendContactUsEmail(
              name,
              companyName,
              email,
              phoneNumber,
              business,
              content)
            Ok
          })
      })
  }
}
