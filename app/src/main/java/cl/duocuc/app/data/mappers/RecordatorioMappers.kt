package cl.duocuc.app.data.mappers

import cl.duocuc.app.data.local.recordatorio.RecordatorioEntity
import cl.duocuc.app.model.Recordatorio

fun RecordatorioEntity.toDto() = Recordatorio(
    id = id,
    uid = uid,
    createdAt = createdAt,
    message = message
)

fun Recordatorio.toEntity() = RecordatorioEntity(
    id = id,
    uid = uid,
    createdAt = createdAt,
    message = message
)
