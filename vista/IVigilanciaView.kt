package escenario_vigilancia_mvc.vista

import escenario_vigilancia_mvc.modelo.EstadoVehicular
import escenario_vigilancia_mvc.modelo.RegistroAcceso

/**
 * COMPONENTE: VISTA (View - Interfaz Contractual)
 * 
 * Responsabilidad:
 * - Definir la interfaz abstracta que la Vista debe implementar para presentar información al usuario (vigilante).
 * - Desacopla la lógica de presentación del framework concreto (sea Jetpack Compose, XML en Android, JavaFX o Consola).
 * - No contiene ninguna regla de validación de negocio ni accede a base de datos.
 * - Recibe datos preparados desde el Controlador y los renderiza.
 */
interface IVigilanciaView {

    /**
     * Muestra en pantalla el resultado de la evaluación de la placa (si está adentro/afuera, titular, etc.).
     */
    fun mostrarEstadoVehicular(estado: EstadoVehicular)

    /**
     * Notifica al usuario el éxito de la operación con los detalles del acceso registrado.
     */
    fun mostrarConfirmacionExito(mensaje: String, registro: RegistroAcceso)

    /**
     * Despliega mensajes de error o advertencia de negocio (ej. intento de salida sin entrada).
     */
    fun mostrarMensajeError(mensaje: String)

    /**
     * Muestra el listado del historial de accesos de la garita.
     */
    fun mostrarHistorial(accesos: List<RegistroAcceso>)

    /**
     * Indica visualmente si el sistema está procesando una operación.
     */
    fun mostrarCargando(estaCargando: Boolean)
}
