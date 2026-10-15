package escenario_vigilancia_mvc.vista

import escenario_vigilancia_mvc.modelo.EstadoVehicular
import escenario_vigilancia_mvc.modelo.RegistroAcceso

/**
 * COMPONENTE: VISTA (View - Implementación Concreta)
 * 
 * Responsabilidad:
 * - Renderizar en pantalla (en este caso consola interactiva/terminal de garita) los datos enviados por el Controlador.
 * - Capturar las interacciones y acciones del usuario vigilante.
 * - Mantenerse como "Passive View" sin tomar decisiones de lógica de negocio.
 */
class ConsolaGaritaView : IVigilanciaView {

    override fun mostrarEstadoVehicular(estado: EstadoVehicular) {
        println("\n================ [ PANTALLA GARITA: ESTADO VEHÍCULO ] ================")
        println("» Mensaje Informativo : ${estado.mensajeInformativo}")
        println("» Registrado en Padrón: ${if (estado.registradoPreviamente) "SÍ" else "NO"}")
        if (estado.titularHabitual != null) {
            println("» Titular Habitual    : ${estado.titularHabitual}")
        }
        println("» Último Movimiento   : ${estado.ultimoMovimiento?.name ?: "Sin registros previos"}")
        println("» Próximo Permitido   : ${estado.proximoMovimientoPermitido.name}")
        if (estado.permiteReingresoForzado) {
            println("» [ADVERTENCIA] Requiere forzar reingreso si el vehículo ya estaba adentro.")
        }
        if (estado.permiteSalidaForzada) {
            println("» [ADVERTENCIA] Requiere forzar salida si no figura entrada activa.")
        }
        println("======================================================================\n")
    }

    override fun mostrarConfirmacionExito(mensaje: String, registro: RegistroAcceso) {
        println("\n>>> [REGISTRO COMPLETADO EXITOSAMENTE] <<<")
        println("ID Acceso      : #${registro.id}")
        println("Placa          : ${registro.placa}")
        println("Movimiento     : ${registro.tipoMovimiento}")
        println("Puerta         : ${registro.puerta.etiqueta}")
        println("Fecha y Hora   : ${registro.fechaHoraTexto}")
        println("Conductor      : ${registro.nombreConductor} (DNI: ${registro.documentoIdentidad})")
        println("Tipo de Persona: ${registro.tipoPersona.etiqueta}")
        if (registro.esReingresoForzado) println("Alerta         : INGRESO FORZADO AUTORIZADO")
        if (registro.esSalidaForzada) println("Alerta         : SALIDA FORZADA AUTORIZADA")
        if (registro.observaciones.isNotBlank()) println("Observaciones  : ${registro.observaciones}")
        println("Detalle        : $mensaje\n")
    }

    override fun mostrarMensajeError(mensaje: String) {
        println("\n[ERROR DE REGLA EN GARITA] ---------------------------------------------")
        println("DETALLE: $mensaje")
        println("------------------------------------------------------------------------\n")
    }

    override fun mostrarHistorial(accesos: List<RegistroAcceso>) {
        println("\n=================== [ HISTORIAL DE ACCESOS EN GARITA ] ===================")
        if (accesos.isEmpty()) {
            println("No se registran movimientos en el turno actual.")
        } else {
            accesos.forEach { acc ->
                println("[#${acc.id}] ${acc.fechaHoraTexto} | ${acc.placa} | ${acc.tipoMovimiento} | ${acc.puerta.codigo} | ${acc.nombreConductor}")
            }
        }
        println("=========================================================================\n")
    }

    override fun mostrarCargando(estaCargando: Boolean) {
        if (estaCargando) {
            println("... [Procesando operación en base de datos de garita] ...")
        }
    }
}
