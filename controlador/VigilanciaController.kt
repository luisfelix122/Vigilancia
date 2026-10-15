package escenario_vigilancia_mvc.controlador

import escenario_vigilancia_mvc.modelo.*
import escenario_vigilancia_mvc.vista.IVigilanciaView

/**
 * COMPONENTE: CONTROLADOR (Controller)
 * 
 * Responsabilidad:
 * - Actuar como mediador y director de orquesta entre la Vista (IVigilanciaView) y el Modelo (AccesoVehicularModel).
 * - Manejar los eventos generados por el usuario o por sensores automáticos (ej. reconocimiento de placa por cámara IA).
 * - Invocar las operaciones pertinentes en el Modelo sin saber cómo están implementadas a bajo nivel.
 * - Interpretar los resultados producidos por el Modelo y actualizar la Vista correspondiente.
 * 
 * ¿Por qué existe y por qué va acá?
 * Porque desacopla completamente el Modelo de la Vista. Si mañana la Vista cambia de terminal a Android (Compose),
 * el Modelo permanece intacto y el Controlador solo necesita comunicarse a través de la interfaz de la Vista.
 */
class VigilanciaController(
    private val modelo: AccesoVehicularModel,
    private val vista: IVigilanciaView
) {

    /**
     * Evento disparado cuando se introduce o escanea una placa.
     * El controlador solicita al modelo la evaluación y envía los datos a la vista.
     */
    fun onPlacaDetectadaOIngresada(placa: String) {
        vista.mostrarCargando(true)
        val estado = modelo.evaluarPlaca(placa)
        vista.mostrarCargando(false)
        vista.mostrarEstadoVehicular(estado)
    }

    /**
     * Evento disparado cuando el vigilante presiona el botón para registrar un acceso.
     * El controlador delega la lógica de negocio al modelo y decide qué vista mostrar según el resultado.
     */
    fun onRegistrarAccesoSolicitado(
        placa: String,
        tipoMovimiento: TipoMovimiento,
        puerta: PuertaAcceso,
        nombreConductor: String,
        documentoIdentidad: String,
        numeroCelular: String = "",
        tipoPersona: TipoPersona,
        observaciones: String = "",
        vigilanteResponsable: String = "Oficial Gómez",
        esReingresoForzado: Boolean = false,
        esSalidaForzada: Boolean = false,
        esConductorOcasional: Boolean = false,
        titularVehiculo: String = ""
    ) {
        vista.mostrarCargando(true)

        val resultado = modelo.registrarAcceso(
            placaRaw = placa,
            tipoMovimiento = tipoMovimiento,
            puerta = puerta,
            nombreConductor = nombreConductor,
            documentoIdentidad = documentoIdentidad,
            numeroCelular = numeroCelular,
            tipoPersona = tipoPersona,
            observaciones = observaciones,
            vigilanteResponsable = vigilanteResponsable,
            esReingresoForzado = esReingresoForzado,
            esSalidaForzada = esSalidaForzada,
            esConductorOcasional = esConductorOcasional,
            titularVehiculo = titularVehiculo
        )

        vista.mostrarCargando(false)

        when (resultado) {
            is AccesoVehicularModel.ResultadoOperacion.Exito -> {
                vista.mostrarConfirmacionExito(resultado.mensaje, resultado.registro)
            }
            is AccesoVehicularModel.ResultadoOperacion.Error -> {
                vista.mostrarMensajeError(resultado.motivo)
            }
        }
    }

    /**
     * Evento para solicitar y visualizar el historial de accesos del turno.
     */
    fun onConsultarHistorialSolicitado() {
        val historial = modelo.obtenerHistorial()
        vista.mostrarHistorial(historial)
    }
}
