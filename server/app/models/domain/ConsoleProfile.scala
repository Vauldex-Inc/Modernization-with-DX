package tech.vauldex.consulting_station.models.domain

import java.util.UUID
import play.api.libs.json._
import tech.vauldex.consulting_station.utils._

case class ConsoleProfile(
    name: Name,
    katakanaName: Name,
    address: Address,
    phoneNumber: Option[String])

object ConsoleProfile {
  val tupled = (apply: (Name, Name, Address, Option[String]) => ConsoleProfile).tupled
}
