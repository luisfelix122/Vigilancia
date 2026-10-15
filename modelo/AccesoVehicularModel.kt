package escenario_vigilancia_mvc.modelo

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * COMPONENTE: MODELO (Model)
 * 
 * Responsabilidad:
 * - Gestionar el estado de los datos del dominio (vehículos y registros de accesos).
 * - Aplicar las reglas de negocio y restricciones del proceso de vigilancia en garita:
 *   1. Validación del formato de placa.
 *   2. Validación de coherencia de transiciones (no permitir doble ENTRADA regular sin forzar, ni SALIDA sin entrada previa).
 *   3. Reglas de conductores ocasionales y titularidad.
 *   4. Persistencia en memoria o base de datos.
 * 
 * El Modelo no tiene conocimiento alguno de la interfaz gráfica ni de cómo se presentan los datos.
 */
class AccesoVehicularModel {

    private val vehiculosRegistrados = mutableMapOf<String, Vehiculo>()
    private val historialAccesos = mutableListOf<RegistroAcceso>()
    private var secuencialIdAcceso = 1L

    sealed class ResultadoOperacion {
        data class Exito(val registro: RegistroAcceso, val mensaje: String) : ResultadoOperacion()
        data class Error(val motivo: String) : ResultadoOperacion()
    }

    /**
     * Evalúa el estado actual de una placa vehicular según las reglas de negocio.
     */
    fun evaluarPlaca(placaRaw: String): EstadoVehicular {
        val placa = normalizarPlaca(placaRaw)
        if (placa.isEmpty()) {
            return EstadoVehicular(
                registradoPreviamente = false,
                datosVehiculo = null,
                ultimoAcceso = null,
                ultimoMovimiento = null,
                proximoMovimientoPermitido = TipoMovimiento.ENTRADA,
                mensajeInformativo = "Ingrese o capture una placa válida."
            )
        }

        val vehiculo = vehiculosRegistrados[placa]
        val ultimoAcceso = historialAccesos.lastOrNull { it.placa == placa }
        val ultimoMovimiento = ultimoAcceso?.tipoMovimiento

        val proximoPermitido = if (ultimoMovimiento == TipoMovimiento.ENTRADA) {
            TipoMovimiento.SALIDA
        } else {
            TipoMovimiento.ENTRADA
        }

        val permiteReingresoForzado = (ultimoMovimiento == TipoMovimiento.ENTRADA)
        val permiteSalidaForzada = (ultimoMovimiento != TipoMovimiento.ENTRADA)
        val titularHabitual = vehiculo?.nombrePropietario

        val mensaje = when {
            vehiculo == null -> "Vehículo nuevo (no registrado en padrón)."
            ultimoMovimiento == TipoMovimiento.ENTRADA ->
                "El vehículo figura DENTRO del campus (Ingreso: ${ultimoAcceso?.fechaHoraTexto}). Corresponde SALIDA o Reingreso Forzado."
            else ->
                "Vehículo registrado a nombre de: ${titularHabitual ?: "Desconocido"}. Procede registrar ENTRADA."
        }

        return EstadoVehicular(
            registradoPreviamente = (vehiculo != null),
            datosVehiculo = vehiculo,
            ultimoAcceso = ultimoAcceso,
            ultimoMovimiento = ultimoMovimiento,
            proximoMovimientoPermitido = proximoPermitido,
            permiteReingresoForzado = permiteReingresoForzado,
            permiteSalidaForzada = permiteSalidaForzada,
            titularHabitual = titularHabitual,
            mensajeInformativo = mensaje
        )
    }

    /**
     * Aplica la regla de negocio para registrar un nuevo movimiento de acceso.
     */
    fun registrarAcceso(
        placaRaw: String,
        tipoMovimiento: TipoMovimiento,
        puerta: PuertaAcceso,
        nombreConductor: String,
        documentoIdentidad: String,
        numeroCelular: String = "",
        tipoPersona: TipoPersona,
        observaciones: String = "",
        vigilanteResponsable: String = "Guardia de Turno",
        esReingresoForzado: Boolean = false,
        esSalidaForzada: Boolean = false,
        esConductorOcasional: Boolean = false,
        titularVehiculo: String = ""
    ): ResultadoOperacion {
        val placa = normalizarPlaca(placaRaw)
        if (placa.length < 5) {
            return ResultadoOperacion.Error("La placa debe contener al menos 5 caracteres alfanuméricos válidos.")
        }
        if (nombreConductor.isBlank()) {
            return ResultadoOperacion.Error("Debe ingresar el nombre del conductor.")
        }

        val ultimoAcceso = historialAccesos.lastOrNull { it.placa == placa }

        // Regla de coherencia de negocio: no duplicar entrada activa
        if (tipoMovimiento == TipoMovimiento.ENTRADA && ultimoAcceso?.tipoMovimiento == TipoMovimiento.ENTRADA) {
            if (!esReingresoForzado) {
                return ResultadoOperacion.Error(
                    "Conflicto de negocio: El vehículo ya se encuentra dentro del campus desde ${ultimoAcceso.fechaHoraTexto}. Active 'Reingreso Forzado' para justificar la excepción."
                )
            }
        }

        // Regla de coherencia de negocio: no permitir salida sin entrada previa
        if (tipoMovimiento == TipoMovimiento.SALIDA && ultimoAcceso?.tipoMovimiento != TipoMovimiento.ENTRADA) {
            if (!esSalidaForzada) {
                return ResultadoOperacion.Error(
                    "Conflicto de negocio: No existe registro de entrada activa previa. Active 'Salida Forzada' para autorizar el egreso."
                )
            }
        }

        val fechaHoraActual = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())

        // Auditoría interna de excepciones
        val marcasAuditoria = mutableListOf<String>()
        if (esReingresoForzado) marcasAuditoria.add("[AUDITORIA: REINGRESO FORZADO]")
        if (esSalidaForzada) marcasAuditoria.add("[AUDITORIA: SALIDA FORZADA]")
        if (esConductorOcasional) marcasAuditoria.add("[CONDUCTOR OCASIONAL: $nombreConductor | Titular: $titularVehiculo]")

        val obsFinal = (marcasAuditoria + listOfNotNull(observaciones.takeIf { it.isNotBlank() })).joinToString(" - ")

        // Actualizar o crear ficha del vehículo
        val vehiculoExistente = vehiculosRegistrados[placa]
        val titularFinal = if (esConductorOcasional && vehiculoExistente != null && vehiculoExistente.nombrePropietario.isNotBlank()) {
            vehiculoExistente.nombrePropietario
        } else {
            nombreConductor.trim()
        }

        val vehiculoActualizado = Vehiculo(
            placa = placa,
            nombrePropietario = titularFinal,
            documentoIdentidad = if (esConductorOcasional && vehiculoExistente != null) vehiculoExistente.documentoIdentidad else documentoIdentidad.trim(),
            tipoPersona = if (esConductorOcasional && vehiculoExistente != null) vehiculoExistente.tipoPersona else tipoPersona,
            numeroCelular = if (esConductorOcasional && vehiculoExistente != null) vehiculoExistente.numeroCelular else numeroCelular.trim(),
            observaciones = obsFinal,
            fechaPrimerRegistro = vehiculoExistente?.fechaPrimerRegistro ?: fechaHoraActual
        )
        vehiculosRegistrados[placa] = vehiculoActualizado

        // Crear y persistir el registro de acceso
        val nuevoAcceso = RegistroAcceso(
            id = secuencialIdAcceso++,
            placa = placa,
            tipoMovimiento = tipoMovimiento,
            puerta = puerta,
            fechaHoraTexto = fechaHoraActual,
            nombreConductor = nombreConductor.trim(),
            documentoIdentidad = documentoIdentidad.trim(),
            numeroCelular = numeroCelular.trim(),
            tipoPersona = tipoPersona,
            vigilanteResponsable = vigilanteResponsable,
            esReingresoForzado = esReingresoForzado,
            esSalidaForzada = esSalidaForzada,
            esConductorOcasional = esConductorOcasional,
            titularVehiculo = titularFinal,
            observaciones = obsFinal
        )

        historialAccesos.add(nuevoAcceso)

        val mensajeExito = "Movimiento de ${tipoMovimiento.name} registrado con éxito en ${puerta.etiqueta} ($fechaHoraActual)."
        return ResultadoOperacion.Exito(nuevoAcceso, mensajeExito)
    }

    fun obtenerHistorial(): List<RegistroAcceso> = historialAccesos.toList()

    fun obtenerVehiculo(placa: String): Vehiculo? = vehiculosRegistrados[normalizarPlaca(placa)]

    private fun normalizarPlaca(placa: String): String =
        placa.trim().uppercase().replace(" ", "").replace("-", "")
}
