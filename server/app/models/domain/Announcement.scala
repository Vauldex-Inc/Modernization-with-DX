package tech.vauldex.consulting_station.models.domain

import java.util.UUID
import java.time.Instant

case class Announcement(
    id: UUID,
    title: Option[String],
    details: String,
    isDeleted: Boolean,
    createdAt: Instant)

object Announcement {
  val tupled = (apply: (UUID, Option[String], String, Boolean, Instant) => Announcement).tupled

  def apply(
    title: Option[String],
    details: String,
    isDeleted: Boolean = false): Announcement =
    Announcement(
      UUID.randomUUID,
      title,
      details,
      isDeleted,
      Instant.now)
}
