package tech.vauldex.consulting_station.models.service

import java.util.UUID
import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }
import play.api.mvc.RequestHeader
import play.api.i18n._
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import cats.data.{ EitherT, OptionT }
import cats.implicits._
import ejisan.kuro.otp.{ OTPKey, OTPAlgorithm }
import tech.vauldex.consulting_station._
import errors.CsError
import errors.CsError._
import models.domain._
import models.dao._
import models.repo._
import utils._

@Singleton
class MemberService @Inject()(
    mailer: MailSender,
    memberRepo: MemberRepo,
    memberProfileRepo: MemberProfileRepo,
    preferenceService: PreferenceService,
    subscriptionRepo: SubscriptionRepo,
    stripeService: StripeService,
    implicit val messagesApi: MessagesApi,
    protected val dbConfigProvider: DatabaseConfigProvider,
    implicit val ec: ExecutionContext)
  extends HasDatabaseConfigProvider[db.PostgresProfile]
  with I18nSupport  {

  private def ioActionResult(result: Future[Int])
    : EitherT[Future, CsError, Unit] = EitherT {
    result map { num =>
      if (num == 1) Right(())
      else if (num == 0) Left(MemberNotExists())
      else Left(InternalContradictionError())
    }
  }

  def addMember(
      member: Member,
      password: String)(implicit request: RequestHeader)
    : EitherT[Future, CsError, (Member, MemberSecurity)] = {
    for {
      _ <- checkActiveEmail(member.email)
      security <- EitherT.right[CsError](MemberSecurity(password))
      _ <- ioActionResult(memberRepo.add(member, security, None))

    } yield {
      mailer.emailVerification(member, security.getVerificationCode)
      (member, security)
    }
  }

  def addMyProfile(id: UUID, profile: MemberProfile)
    : EitherT[Future, CsError, Unit] = {
      for {
      _ <- memberRepo.findProfile(id).map[CsError](_ => MemberConflicts()).toLeft[Unit](())
      _ <- ioActionResult(memberProfileRepo.add(profile))
      result <- ioActionResult(memberRepo.update(id, Some(profile.id)))
    } yield result
  }

  def getMyProfile(id: UUID): EitherT[Future, CsError, MemberProfile] = {
    for {
      member <- memberRepo.find(id).toRight[CsError](MemberNotExists())
      profile <- member._3.map { id =>
        memberProfileRepo.find(id).toRight[CsError](MemberNotExists())
      } getOrElse {
        EitherT.left[MemberProfile](Future.successful[CsError](MemberNotExists()))
      }
    } yield profile
  }

  def updateMyProfile(id: UUID)(
      name: Option[Name],
      katakanaName: Option[Name],
      address: Option[Address],
      phoneNumber: Option[Option[String]],
      company: Option[Company]): EitherT[Future, CsError, Unit] = {
    for {
      member <- memberRepo.find(id).toRight[CsError](MemberNotExists())
      result <- member._3.map { profileId =>
        ioActionResult(
          memberProfileRepo
          .update(profileId)(name, katakanaName, address, phoneNumber, company))
      } getOrElse {
        EitherT.left[Unit](Future.successful[CsError](MemberNotExists()))
      }
    } yield result
  }

  def updateEmail(
      id: UUID,
      email: String,
      secret: OTPKey = OTPKey.random(OTPAlgorithm.SHA1))(implicit request: RequestHeader)
    : EitherT[Future, Throwable, Member] = {
    for {
      member <- memberRepo.find(id, true).toRight[CsError](MemberNotExists())
      _ <- EitherT(
        if(member._1.email === email)
          Future.successful(Right(()))
        else {
          memberRepo.isEmailActive(email)
          .map(if(_) Left(MemberConflicts()) else Right(()))
        })
      _ <- memberRepo.updateEmail(member._1.id, email, Some(secret))
      security <- memberRepo.findValidSecurity(id)
      checkSubscription <- EitherT.right(subscriptionRepo.exists(id))
      _ <- {
        if(checkSubscription)
          stripeService.updateCustomerEmail(id, email)
        else EitherT.right[Throwable](Future.successful(()))
      }
    } yield {
      val updatedMember =  member._1.copy(email = email)
      mailer.emailVerification(updatedMember, security.getVerificationCode)
      updatedMember
    }
  }

   def checkActiveEmail(email: String)(implicit ec: ExecutionContext)
    : EitherT[Future, CsError, Unit] = EitherT {
    memberRepo
      .isEmailActive(email)
      .map(if(_) Left(MemberConflicts(409, Some("error.member_conflict"))) else Right(()))
  }

  def sendInviteEmail(email: String)(implicit request: RequestHeader): Future[Unit] =
    preferenceService.predef.csFrontendUrl.get.map { url =>
      mailer.sendInviteEmail(email, url+"/signup")
    }
}
