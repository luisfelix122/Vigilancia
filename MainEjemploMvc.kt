package escenario_vigilancia_mvc

import escenario_vigilancia_mvc.controlador.VigilanciaController
import escenario_vigilancia_mvc.modelo.*
import escenario_vigilancia_mvc.vista.ConsolaGaritaView

/**
 * CLASE DEMOSTRATIVA DE EJECUCIÓN (Runner)
 * 
 * Simula el flujo completo del escenario "Control y Registro de Acceso Vehicular en Garita"
 * demostrando la separación estricta de responsabilidades del patrón Modelo-Vista-Controlador (MVC).
 */
fun main() {
    println("========================================================================")
    println(" DEMOSTRACIÓN ARQUITECTURA MVC - ESCENARIO DE VIGILANCIA EN GARITA")
    println("========================================================================")

    // 1. Instanciación independiente de Modelo y Vista
    val modelo = AccesoVehicularModel()
    val vista = ConsolaGaritaView()

    // 2. El Controlador enlaza y coordina ambos componentes
    val controlador = VigilanciaController(modelo = modelo, vista = vista)

    val placaEjemplo = "P3X-892"

    println("\n--- PASO 1: DETECCIÓN O INGRESO DE PLACA VEHICULAR ---")
    // El usuario ingresa la placa (o el sensor de cámara la detecta)
    controlador.onPlacaDetectadaOIngresada(placaEjemplo)

    println("\n--- PASO 2: REGISTRO DE ENTRADA REGULAR ---")
    controlador.onRegistrarAccesoSolicitado(
        placa = placaEjemplo,
        tipoMovimiento = TipoMovimiento.ENTRADA,
        puerta = PuertaAcceso.PUERTA_1,
        nombreConductor = "Ing. Juan Pérez Saldarriaga",
        documentoIdentidad = "40582910",
        numeroCelular = "969123456",
        tipoPersona = TipoPersona.PROFESOR,
        observaciones = "Docente de Ingeniería de Sistemas",
        vigilanteResponsable = "Oficial Gómez (Guardia Garita 1)"
    )

    println("\n--- PASO 3: INTENTO DE REGISTRAR OTRA ENTRADA (PRUEBA DE REGLA DE NEGOCIO) ---")
    // El vehículo ya está adentro. Intentamos registrar entrada sin forzar
    controlador.onRegistrarAccesoSolicitado(
        placa = placaEjemplo,
        tipoMovimiento = TipoMovimiento.ENTRADA,
        puerta = PuertaAcceso.PUERTA_1,
        nombreConductor = "Ing. Juan Pérez Saldarriaga",
        documentoIdentidad = "40582910",
        tipoPersona = TipoPersona.PROFESOR,
        vigilanteResponsable = "Oficial Gómez"
    )

    println("\n--- PASO 4: REGISTRO DE SALIDA REGULAR ---")
    controlador.onRegistrarAccesoSolicitado(
        placa = placaEjemplo,
        tipoMovimiento = TipoMovimiento.SALIDA,
        puerta = PuertaAcceso.PUERTA_1,
        nombreConductor = "Ing. Juan Pérez Saldarriaga",
        documentoIdentidad = "40582910",
        tipoPersona = TipoPersona.PROFESOR,
        vigilanteResponsable = "Oficial Gómez"
    )

    println("\n--- PASO 5: CONSULTA DEL HISTORIAL DE ACCESOS AUDITADO ---")
    controlador.onConsultarHistorialSolicitado()

    println("========================================================================")
    println(" FIN DE LA DEMOSTRACIÓN MVC")
    println("========================================================================")
}
