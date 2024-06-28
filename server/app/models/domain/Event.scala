package tech.vauldex.consulting_station.models.domain

import java.util.UUID
import java.time.Instant

case class Event(
    id: UUID,
    name: String,
    link: String,
    description: String,
    isPublic: Boolean,
    isDeleted: Boolean,
    startDateTime: Instant,
    endDateTime: Instant,
    createdAt: Instant)

object Event {
  val tupled = (apply: (
    UUID,
    String,
    String,
    String,
    Boolean,
    Boolean,
    Instant,
    Instant,
    Instant) => Event).tupled

  def apply(
    name: String,
    link: String,
    description: String,
    isPublic: Boolean,
    startDateTime: Instant,
    endDateTime: Instant,
    isDeleted: Boolean = false): Event =
    Event(
      UUID.randomUUID,
      name,
      link,
      description,
      isPublic,
      isDeleted,
      startDateTime,
      endDateTime,
      Instant.now)
}
