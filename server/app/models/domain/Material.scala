package tech.vauldex.consulting_station.models.domain

import java.time.Instant
import java.util.UUID
import tech.vauldex.consulting_station.utils.MaterialSections

case class Material(
  id: UUID,
  idMember: UUID,
  createdAt: Instant,
  materialSections: List[MaterialSections.Value])

object Material {
  val tupled =
    (apply: (UUID, UUID, Instant, List[MaterialSections.Value]) => Material).tupled

  def apply(id: UUID = UUID.randomUUID, idMember: UUID, materialSections: List[MaterialSections.Value]): Material =
    Material(id, idMember, Instant.now, materialSections)
}
