# ---

# ---

# 

# 

# **Brief**

# **GastoSimple**

# **Versión 1.0**

# **Histórico de Revisiones**

 

| Fecha | Versión | Descripción | Autor(es) |
| :---- | :---- | :---- | :---- |
| *10/ago/26* | *1.0* | *Creación del Brief inicial de GastoSimple: Necesidad, Backlog (USM y HUs), Restricciones y Rangos de Calidad.* | *Leonel Rojas, Jesús Acosta, Rolando Rodrigo* |
|  |  |  |  |

 

# 

# 

# 

# 

# 

# 

# 

# 

# 

# 

# 

# 

# 

# 

# 

# **Tabla de Contenidos**

[**Brief	1**](#heading=)

[**GastoSimple	1**](#heading=)

[**Versión 1.0	1**](#heading=)

[**Histórico de Revisiones	2**](#heading=)

[**Tabla de Contenidos	3**](#heading=)

[**Brief	3**](#heading=)

[Épica 1: Gestión de Presupuesto y Registro de Gastos	5](#heading=)

[Épica 2: Análisis Financiero (Dashboard de Métricas)	5](#heading=)

[Épica 3: Control Temporal y Calendario de Pagos	6](#heading=)

[Épica 4: Personalización Visual (Configuración de Temas)	6](#heading=)

 

# **Brief**

*El propósito de este documento es recolectar, analizar y definir las necesidades a un alto nivel utilizando un Mapa de Impacto del sistema \*\*GastoSimple\*\*. Con base en ella se prepara el Backlog (User Story Map). Se especifican las restricciones de la aplicación y los criterios de aceptación aplicables al caso. Finalmente se identifican los rangos de calidad deseados.*

 

**1\.** **Necesidad**

 

| Meta | Personas | Impacto | Entregable |
| :---- | :---- | :---- | :---- |
| *Control y distribución del presupuesto* | *Usuarios* | *Podrán establecer un presupuesto financiero claro e integrar a uno o más participantes (parejas, roommates, familias) definiendo divisiones equitativas o porcentuales de responsabilidad.* | *Módulo de Configuración de Presupuesto y Usuarios Definición del presupuesto total. Registro de uno o múltiples usuarios.  Asignación de división porcentual de aportes (ej. 50%/50%, 60%/40%).* |
| *Trazabilidad ilimitada de gastos* | *Usuarios* | *Podrán registrar cada uno de sus gastos sin límites, categorizándolos para entender el destino de sus flujos de dinero.* | *Registro Flexible de Gastos Formulario de carga rápida de gastos cotidianos.  Asociación del gasto a uno o varios usuarios.  Historial detallado de egresos.* |
| *Análisis visual de hábitos financieros* | *Usuarios* | *Comprenderán de forma gráfica y porcentual en qué rubros se consume su dinero en relación con el presupuesto definido.* | *Dashboard de Métricas Financieras. Porcentaje de consumo del presupuesto actual.  Identificación de categorías de mayor consumo. Cálculo automático del saldo disponible e individual.* |
| *Planificación temporal y vencimientos* | *Usuarios* | *Preverán compromisos financieros futuros recurrentes (alquiler, suscripciones, compras habituales) evitando pagos tardíos.* | *Calendario de Pagos y Renovaciones  Vista de calendario con eventos de gastos.  Identificación visual de fechas de renovación o pago recurrente. Sistema de notificaciones locales de vencimiento.* |
| *Adaptación y comodidad visual* | *Usuarios* | *Podrán personalizar el aspecto de la aplicación según sus preferencias estilísticas mediante paletas de colores.* | *Pantalla de Configuración de Temas Selección de temas visuales prediseñados.* |

 

**2\.** **Backlog**

 

| Actividad Principal (Meta) | Gestión de Presupuesto y Gastos | Análisis Financiero (Métricas) | Control Temporal (Calendario) | Personalización Visual |
| :---- | :---- | :---- | :---- | :---- |
| *Tarea de Usuario* | *Configurar presupuesto, usuarios y registrar egresos* | *Consultar consumo y desglose de gastos* | *Monitorear vencimientos e itinerarios de pago* | *Personalizar interfaz* |
| *MVP (Iteración 1\)* | *Registrar presupuesto inicial.  Registrar uno o más usuarios.  Definir porcentaje de responsabilidad por usuario.  Registrar gastos de forma ilimitada.  Consultar listado de gastos.* | *(No incluido en MVP para agilizar lanzamiento)* | *Visualizar calendario con gastos recurrentes. Ver frecuencia de repetición de gastos. Recibir notificaciones locales de renovación.* | *(No incluido en MVP para agilizar lanzamiento)* |
| *Release 2 (Iteración 2\)* |  | *Ver porcentaje consumido del presupuesto.  Ver categorías que más consumen. Ver monto exacto a pagar por usuario.* | *ya :p* | *Cambiar temas y paletas de colores de la app.* |

 

### **Épica 1: Gestión de Presupuesto y Registro de Gastos** 

* **HU-01:** Como usuario, quiero registrar un presupuesto total para establecer un techo financiero dentro de un periodo determinado.

* **HU-02:** Como usuario, quiero agregar uno o más usuarios a la gestión financiera para controlar los gastos compartidos (parejas, familias, roommates) o individuales.

* **HU-03:** Como usuario, quiero asignar porcentajes de aporte/pago a cada usuario registrado (ej. 50%-50% o 60%-40%), para que el sistema calcule automáticamente cuánto debe aportar cada uno en los gastos grupales.

* **HU-04:** Como usuario, quiero registrar egresos/gastos ilimitados especificando el monto, concepto y quién lo realizó, para mantener un seguimiento continuo sin restricciones.

* **HU-05:** Como usuario, quiero visualizar una lista ordenada de todos los gastos registrados para tener una vista general inmediata de las transacciones efectuadas.

 

### **Épica 2: Análisis Financiero (Dashboard de Métricas)** 

* **HU-06:** Como usuario, quiero ver un panel visual con el porcentaje consumido de mi presupuesto total, para saber cuánto dinero me queda disponible de un vistazo.

* **HU-07:** Como usuario, quiero visualizar qué conceptos o rubros consumen la mayor parte de mi dinero, para identificar oportunidades de ahorro o ajustes.

* **HU-08:** Como usuario, quiero consultar el desglose exacto de lo que debe pagar cada persona según su porcentaje asignado, para realizar cuentas claras de forma equitativa y transparente.

 

### **Épica 3: Control Temporal y Calendario de Pagos** 

* **HU-09:** Como usuario, quiero ver un calendario interactivo que indique las fechas en las que se deben repetir o renovar ciertos gastos (suscripciones, facturas, compras habituales), para planificar mi liquidez.

* **HU-10:** Como usuario, quiero recibir notificaciones locales en mi dispositivo cuando se aproxime la fecha de renovación de un gasto, para evitar retrasos o recargos en mis pagos.

 

### **Épica 4: Personalización Visual (Configuración de Temas)** 

* **HU-11:** Como usuario, quiero seleccionar entre distintos temas de colores prediseñados en la aplicación, para adaptar la interfaz a mis gustos personales y mejorar mi experiencia de uso.

 

**3\. Restricciones**

 

* ***Almacenamiento y Privacidad (Offline-First):** Toda la información registrada por el/los usuario(s) se almacenará de manera 100% local en el dispositivo móvil mediante Room Database (SQLite) y Jetpack DataStore para preferencias. No existirá conexión a servidores backend externos, APIs remotas ni bases de datos en la nube. La aplicación no recolectará analíticas ni telemetría.*

* ***Entorno Monodispositivo y Sin Autenticación:** La aplicación no requerirá registro de cuenta ni inicio de sesión (Login/Signup). Operará en un entorno local por dispositivo, gestionando la dinámica multi-usuario dentro de la misma instancia instalada.*

* ***Idiomas Soportados:** El sistema estará restringido a los idiomas español (ES) e inglés (EN). Todas las cadenas de texto se gestionarán mediante recursos \`strings.xml\`, adaptándose automáticamente al idioma nativo configurado en el sistema operativo del dispositivo.*

* ***Términos y Condiciones y Privacidad Local:** La aplicación incorporará una sección de Términos y Condiciones y Política de Privacidad de lectura local para el usuario, garantizando la transparencia sobre el uso exclusivo y privado de sus datos.*

* ***Plataforma y Arquitectura Técnica:** El producto se desarrollará exclusivamente para el sistema operativo Android utilizando Kotlin, Jetpack Compose para la UI, arquitectura MVVM, Inyección de Dependencias con Koin y Corrutinas/Flows para operaciones asíncronas.*

 

**4\. Rangos de Calidad**

 

* ***Desempeño y Comportamiento Temporal (Time Behaviour \- ISO 25010):** El tiempo de arranque en frío (Cold Start) de la aplicación no debe superar los 4 segundos. Las transiciones entre pantallas y las consultas a la base de datos local deben ejecutarse con un tiempo de respuesta menor a 1 segundo. El consumo de recursos en segundo plano debe ser mínimo para preservar la batería del dispositivo.*

* ***Usabilidad y Adaptabilidad (ISO 25010):** La interfaz gráfica implementada en Jetpack Compose debe adaptarse responsivamente a los diferentes tamaños, resoluciones y orientaciones de pantalla de dispositivos móviles Android. Debe ser intuitiva y permitir la configuración de múltiples temas visuales.*

* ***Seguridad y Privacidad por Diseño (ISO 25010):** Al manejar datos financieros sensibles, la seguridad se garantiza mediante la no transmisión de datos a redes externas. Todo dato generado persiste exclusivamente en el almacenamiento local protegido de la aplicación.*

* ***Confiabilidad y Tolerancia a Fallas (ISO 25010):** La aplicación debe ser 100% funcional sin conexión a Internet. Debe ser capaz de pasar a segundo plano sin perder el estado de la sesión, sin reinicios abruptos ni cierres inesperados (Crash/ANR).*

* ***Mantenibilidad y Modolaridad (ISO 25010):** El proyecto debe seguir una estructura modular por características (Feature-Based) bajo el patrón MVVM y el principio de responsabilidad única. La arquitectura debe estar desacoplada mediante Koin para facilitar la incorporación limpia de las épicas de la Iteración 2 (Métricas, Calendario y Temas).*

* ***Adecuación Funcional (ISO 25010):** El sistema debe garantizar la correcta ejecución matemática y lógica de la división de porcentajes de gastos y los cálculos de presupuesto restante sin margen de error en las operaciones financieras registradas.*