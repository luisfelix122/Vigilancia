# Arquitectura de Diseño: Modelo-Vista-Controlador (MVC)
## Asignatura: Análisis y Diseño de Sistemas II
### Escenario: Control y Registro de Acceso Vehicular en Garita (Vigilancia UNP)

Este módulo implementa de forma pura y desacoplada el patrón arquitectónico **Modelo-Vista-Controlador (MVC)** para el escenario individual de vigilancia de garita vehicular.

---

## 1. ¿De qué trata la aplicación y qué hace?

**VigilanciaUNP** es una solución integral desarrollada para el control de accesos vehiculares en las garitas del campus de la **Universidad Nacional de Piura (UNP)** (Puerta 1 Principal y Puerta 4 Secundaria).

### Funcionalidades principales:
1. **Detección y Reconocimiento de Placas con IA/OCR:** Mediante la cámara móvil, el sistema identifica en tiempo real la placa del vehículo (o permite su ingreso manual), acelerando la atención en garita.
2. **Consulta al Padrón Institucional:** Identifica al instante si el vehículo está registrado y a qué estamento pertenece (Alumno, Docente, Personal Administrativo, Servicio Móvil o Visitante).
3. **Control de Coherencia de Permanencia:** Determina si el vehículo figura dentro o fuera del campus, impidiendo ingresos duplicados o salidas sin ingreso previo.
4. **Gestión de Excepciones y Auditoría:** Permite registrar autorizaciones forzadas (reingreso o salida forzada) auditando al vigilante responsable y registrando conductores ocasionales.
5. **Persistencia Local y Reportes:** Almacena todos los eventos en SQLite local y genera reportes de accesos auditados.

---

## 2. Estructura del Módulo MVC

```text
escenario_vigilancia_mvc/
├── modelo/
│   ├── Entidades.kt             # Entidades de dominio (Vehiculo, RegistroAcceso, EstadoVehicular, Enums)
│   └── AccesoVehicularModel.kt   # Lógica de negocio pura, validaciones de coherencia y persistencia
├── vista/
│   ├── IVigilanciaView.kt       # Contrato/Interfaz abstracta de la vista (desacoplamiento total)
│   └── ConsolaGaritaView.kt     # Implementación concreta pasiva (Passive View) para garita
├── controlador/
│   └── VigilanciaController.kt  # Mediador y orquestador entre IVigilanciaView y AccesoVehicularModel
├── MainEjemploMvc.kt            # Demostración ejecutable del flujo completo
├── README.md                    # Explicación del módulo
└── documentacion/
    └── INFORME_ARQUITECTURA_MVC_VIGILANCIA.pdf # Informe final para entrega académica
```

---

## 3. ¿Qué hay en cada componente y por qué?

### A. MODELO (`modelo/`)
- **¿Qué contiene?**
  - Entidades de datos (`Vehiculo`, `RegistroAcceso`, `EstadoVehicular`, `TipoMovimiento`, `PuertaAcceso`, `TipoPersona`).
  - Lógica de negocio (`AccesoVehicularModel.kt`): Validación de placas, control de estados (adentro/afuera), titularidad y persistencia.
- **¿Por qué va en el Modelo?**
  - Las reglas de seguridad y la estructura de los datos pertenecen al **negocio institucional**, no a la pantalla ni al controlador. El modelo no depende de frameworks visuales ni dispositivos de hardware (Principio de Responsabilidad Única - SRP).

### B. VISTA (`vista/`)
- **¿Qué contiene?**
  - Contrato abstracto `IVigilanciaView` (`mostrarEstadoVehicular()`, `mostrarConfirmacionExito()`, `mostrarMensajeError()`, `mostrarHistorial()`, `mostrarCargando()`).
  - Implementación concreta `ConsolaGaritaView`.
- **¿Por qué va en la Vista?**
  - La Vista es estrictamente una **Vista Pasiva (Passive View)**: solo renderiza información y captura inputs del vigilante. No toma decisiones de negocio ni consulta bases de datos.

### C. CONTROLADOR (`controlador/`)
- **¿Qué contiene?**
  - Clase mediadora `VigilanciaController.kt` con manejadores de eventos (`onPlacaDetectadaOIngresada()`, `onRegistrarAccesoSolicitado()`, `onConsultarHistorialSolicitado()`).
- **¿Por qué va en el Controlador?**
  - Es el **puente que desacopla la Vista del Modelo**. Recibe los eventos de usuario, invoca las reglas del Modelo y le ordena a la Vista qué renderizar según el resultado.

## 4. Diagramas UML Incluidos en el Informe

El informe en PDF incluye los siguientes diagramas de ingeniería de software estructurados de forma espaciosa e individual:
1. **Diagrama de Casos de Uso:** Actores (Vigilante, Cámara IA, SQLite) y casos de uso de captura, evaluación, registro y auditoría.
2. **Diagrama de Actividades con Swimlanes:** Flujo de trabajo y bifurcaciones de decisión entre Vigilante, Controlador y Modelo/BD.
3. **Diagrama de Clases (MVC):** Descomposición formal en paquetes `modelo`, `vista` y `controlador`, con interfaces y multiplicidades.
4. **Diagrama de Secuencia:** Línea de tiempo de interacción entre componentes durante una solicitud de acceso real.

---

## 5. Documento PDF de Entrega

El PDF formal para presentar en la asignatura **Análisis y Diseño de Sistemas II** se encuentra listo en:
👉 `escenario_vigilancia_mvc/documentacion/INFORME_ARQUITECTURA_MVC_VIGILANCIA.pdf`
