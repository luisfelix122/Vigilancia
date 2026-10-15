package escenario_vigilancia_mvc.modelo

enum class TipoMovimiento {
    ENTRADA,
    SALIDA
}

enum class TipoPersona(val etiqueta: String) {
    ALUMNO("Alumno"),
    PROFESOR("Profesor"),
    TRABAJADOR("Trabajador"),
    SERVICIO_MOVIL("Servicio Móvil / Delivery"),
    VISITANTE("Visitante")
}

enum class PuertaAcceso(val codigo: String, val etiqueta: String) {
    PUERTA_1("P1", "Puerta 1 (Principal)"),
    PUERTA_4("P4", "Puerta 4 (Secundaria)")
}

data class Vehiculo(
    val placa: String,
    val nombrePropietario: String,
    val documentoIdentidad: String,
    val tipoPersona: TipoPersona,
    val numeroCelular: String = "",
    val observaciones: String = "",
    val fechaPrimerRegistro: String = ""
)

data class RegistroAcceso(
    val id: Long = 0L,
    val placa: String,
    val tipoMovimiento: TipoMovimiento,
    val puerta: PuertaAcceso,
    val fechaHoraTexto: String,
    val nombreConductor: String,
    val documentoIdentidad: String = "",
    val numeroCelular: String = "",
    val tipoPersona: TipoPersona,
    val vigilanteResponsable: String = "",
    val esReingresoForzado: Boolean = false,
    val esSalidaForzada: Boolean = false,
    val esConductorOcasional: Boolean = false,
    val titularVehiculo: String = "",
    val observaciones: String = ""
)

data class EstadoVehicular(
    val registradoPreviamente: Boolean,
    val datosVehiculo: Vehiculo?,
    val ultimoAcceso: RegistroAcceso? = null,
    val ultimoMovimiento: TipoMovimiento?,
    val proximoMovimientoPermitido: TipoMovimiento,
    val permiteReingresoForzado: Boolean = false,
    val permiteSalidaForzada: Boolean = false,
    val titularHabitual: String? = null,
    val mensajeInformativo: String
)

data class SesionVigilante(
    val idVigilante: String = "VIG-001",
    val nombreVigilante: String = "Oficial de Guardia",
    val rol: String = "GUARDIA_GARITA"
)
