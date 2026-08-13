# ---

# ---

# **Especificación de Requisitos de Software**

# **GastoSimple**

**Versión 1.1**

 

## 

## 

## 

## 

## 

## 

## 

## 

## 

## 

## 

## 

## 

## 

## 

## 

## **Histórico de Revisiones**

 

| Fecha | Versión | Descripción | Autor(es) |
| :---- | :---- | :---- | :---- |
| *7/ago/26* | *1.0* | *Creación del ERS/SRS de GastoSimple basada en el Brief v1.0, detallando Sprints con tablas Canvas BDD y Requisitos Suplementarios alineados a la norma ISO 25010\.* | *Leonel Rojas, Jesús Acosta, Rolando Rodrigo* |
| *13/ago/26* |                            *1.1* | *Incorporación de la Épica 5: Cuotas y Gastos Imprevistos (Historias de Usuario HU-12 a HU-15, entregables del negocio, desglose del Release 2 y atributos de calidad de amortización).* | *Leonel Rojas* |

 

## 

## 

## 

## **Tabla de Contenidos**

1\. Sprint  
1.1 Sprint 1 (MVP \- Iteración 1\)  
1.1.1 HU-01: Registro de Presupuesto Total  
1.1.2 HU-02: Registro de Usuarios Participantes  
1.1.3 HU-03: Asignación de Porcentajes de Aporte  
1.1.4 HU-04: Registro Ilimitado de Gastos  
1.1.5 HU-05: Visualización de Lista Ordenada de Gastos  
1.1.6 HU-09: Calendario Interactivo de Gastos Recurrentes  
1.1.7 HU-10: Notificaciones Locales de Vencimiento  
1.2 Sprint 2 (Release 2 \- Iteración 2\)  
1.2.1 HU-06: Panel Visual de Porcentaje Consumido  
1.2.2 HU-07: Identificación de Categorías de Mayor Consumo  
1.2.3 HU-08: Desglose Individual de Cuota de Pago  
1.2.4 HU-11: Selección de Temas Visuales Prediseñados  

2\. Requisitos Suplementarios  
2.1 Eficiencia de Desempeño  
2.2 Compatibilidad  
2.3 Capacidad de Interacción  
2.4 Fiabilidad  
2.5 Seguridad  
2.6 Mantenibilidad  
2.7 Flexibilidad  
2.8 Protección

 

# **Especificación de Requisitos de Software**

# **GastoSimple**

Este artefacto detalla los requisitos de software para el Sistema **GastoSimple**, según dos grandes aspectos claves para su desarrollo: Las Historias de Usuario en cada Sprint con su especificación según el modelo Canvas (incluyendo conversación con Product Owner y Criterios de Aceptación en formato BDD Dado/Cuando/Entonces), y las Especificaciones Suplementarias respaldadas por los atributos de calidad de la norma **ISO 25010**. Toda esta información establece los lineamientos y las restricciones que debe considerar el equipo de desarrollo para la construcción de la aplicación móvil Android.

 

## **1\. Sprint**

### **1.1 Sprint 1 (MVP \- Iteración 1\)**

*Historias enfocadas en la Gestión de Presupuesto, Registro Básico de Gastos y Control Temporal (HU-01, HU-02, HU-03, HU-04, HU-05, HU-09, HU-10).*

 

#### **1.1.1 HU-01: Registro de Presupuesto Total**

 

| 2\. Historia de Usuario | 1\. Persona | 4\. Contexto |
| :---- | :---- | :---- |
| **Como** usuario,**quiero** registrar un presupuesto total, **para** establecer un techo financiero dentro de un periodo determinado. | **Primario:** Usuario individual o representante financiero de un grupo (pareja, roommates, familia). | **Épica:** Épica 1 \- Gestión de Presupuesto y Registro de Gastos. **Escenario:** Configuración inicial o edición de ciclo financiero. **Regla de Negocio:** El monto presupuestado debe ser un número positivo finito (mayor a cero) con soporte de hasta 2 decimales. |

 

| 3\. Criterios de Aceptación | 5\. Definición de Preparado (DoR) | 6\. Definición de Terminado (DoD) |
| :---- | :---- | :---- |
| **Dado que** el usuario ingresa al módulo de configuración de presupuesto,**cuando** ingresa un monto numérico válido y guarda los cambios,**entonces** el sistema debe almacenar el valor en la base de datos local (Room) y fijarlo como el límite global del periodo.**Dado que** el usuario ingresa un valor negativo o no numérico,**cuando** intenta guardar,**entonces** la interfaz muestra un mensaje de validación indicando que el presupuesto debe ser un número mayor a cero. | • La historia cumple con el modelo INVEST.• Interfaz en Jetpack Compose definida con un campo numérico con formato de moneda.• Entidad BudgetEntity especificada en Room Database.• Reglas de validación monetaria documentadas. | • Código fuente integrado en arquitectura MVVM.• Persistencia local probada en Room mediante pruebas de integración.• 100% de cobertura de pruebas unitarias en el ViewModel de presupuesto.• Sin llamadas ni dependencias a servidores externos (Offline-First). |

 

| 7\. Resultado Esperado | 8\. Métricas | 9\. Retroalimentación |
| :---- | :---- | :---- |
| Definición clara del límite financiero global para el periodo de control de egresos. | **Puntos de Historia:** 3 pts.**Casos de Prueba:** 4 casos de prueba ejecutados exitosamente (ingreso válido, valor cero, valor negativo, edición de valor existente). | **Nota Técnica:** Se utiliza StateFlow para emitir el presupuesto actualizado en tiempo real a la interfaz gráfica. Koin provee el repositorio correspondiente. |

 

 

#### **1.1.2 HU-02: Registro de Usuarios Participantes**

 

| 2\. Historia de Usuario | 1\. Persona | 4\. Contexto |
| :---- | :---- | :---- |
| **Como** usuario,**quiero** agregar uno o más usuarios a la gestión financiera,**para** controlar los gastos compartidos (parejas, familias, roommates) o individuales. | **Primario:** Usuario administrador del grupo local.**Secundario:** Participantes registrados dentro de la app local. | **Épica:** Épica 1 \- Gestión de Presupuesto y Registro de Gastos.**Escenario:** Configuración de integrantes de la cuenta compartida o individual.**Regla de Negocio:** Se debe registrar al menos un usuario (por defecto "Usuario Principal"). Operación monodispositivo sin login ni sincronización cloud. |

 

| 3\. Criterios de Aceptación | 5\. Definición de Preparado (DoR) | 6\. Definición de Terminado (DoD) |
| :---- | :---- | :---- |
| **Dado que** el usuario navega a la sección de configuración de participantes,**cuando** escribe un nombre de alias y presiona "Agregar",**entonces** la persona se añade a la lista activa de usuarios guardada localmente.**Dado que** se intenta registrar un nombre en blanco o repetido,**cuando** se presiona "Guardar",**entonces** el sistema previene el registro y muestra la advertencia correspondiente en pantalla. | • Interfaz UI diseñada con un listado dinámico y modal de registro.• Entidad UserEntity definida en Room.• Flujo de datos desacoplado mediante Koin e inyección de dependencias. | • Funcionalidad probada sin conexión a Internet.• Pruebas de integración para inserción y borrado de usuarios exitosas.• Componente visual UserChip en Jetpack Compose verificado en modo claro y oscuro. |

 

| 7\. Resultado Esperado | 8\. Métricas | 9\. Retroalimentación |
| :---- | :---- | :---- |
| Registro transparente de participantes de la gestión de gastos sin requerir registros de cuentas ni datos personales sensibles. | **Puntos de Historia:** 3 pts.**Casos de Prueba:** 5 casos de prueba ejecutados (creación individual, agregación múltiple, nombres duplicados, eliminación de participantes). | **Conversación PO:** No requerimos campos de correo ni teléfono, solo nombres/alias para atribuir las compras dentro del dispositivo. |

 

 

#### **1.1.3 HU-03: Asignación de Porcentajes de Aporte**

 

| 2\. Historia de Usuario | 1\. Persona | 4\. Contexto |
| :---- | :---- | :---- |
| **Como** usuario,**quiero** asignar porcentajes de aporte/pago a cada usuario registrado (ej. 50%-50% o 60%-40%),**para que** el sistema calcule automáticamente cuánto debe aportar cada uno en los gastos grupales. | **Primario:** Co-administradores de finanzas compartidas. | **Épica:** Épica 1 \- Gestión de Presupuesto y Registro de Gastos.**Escenario:** Ajuste de reglas de equidad financiera.**Regla de Negocio:** La suma exacta de los porcentajes asignados a todos los usuarios registrados debe ser estrictamente igual al 100%. |

 

| 3\. Criterios de Aceptación | 5\. Definición de Preparado (DoR) | 6\. Definición de Terminado (DoD) |
| :---- | :---- | :---- |
| **Dado que** existen dos o más usuarios registrados,**cuando** el usuario ajusta los desgloses porcentuales,**si** la suma total equivale exactamente al 100%,**entonces** el sistema guarda la distribución de aportes.**Dado que** el usuario configura una distribución cuya suma sea diferente del 100% (ej. 90% o 105%),**cuando** intenta guardar,**entonces** el botón de guardado se deshabilita y se notifica el error de suma errónea. | • Lógica matemática de distribución de cuotas definida.• Mockup interactivo con sliders o campos numéricos porcentuales.• Métodos de validación unitaria creados en la capa de Dominio. | • Pruebas unitarias al 100% en los cálculos de porcentaje.• Verificación de precisión sin errores de redondeo de punto flotante usando BigDecimal.• Persistencia local de porcentajes en Room. |

 

| 7\. Resultado Esperado | 8\. Métricas | 9\. Retroalimentación |
| :---- | :---- | :---- |
| Automatización del cálculo de distribución de aportes financieros según porcentajes acordados. | **Puntos de Historia:** 5 pts.**Casos de Prueba:** 6 casos de prueba (50/50, 60/40, 33.33/33.33/33.34, suma invalida \>100%, suma invalida \<100%). | **Nota de Diseño:** Usar BigDecimal en Kotlin para evitar imprecisiones financieras inherentes al tipo Float o Double. |

 

 

#### **1.1.4 HU-04: Registro Ilimitado de Gastos**

 

| 2\. Historia de Usuario | 1\. Persona | 4\. Contexto |
| :---- | :---- | :---- |
| **Como** usuario,**quiero** registrar egresos/gastos ilimitados especificando el monto, concepto y quién lo realizó,**para** mantener un seguimiento continuo sin restricciones. | **Primario:** Usuarios del sistema. | **Épica:** Épica 1 \- Gestión de Presupuesto y Registro de Gastos.**Escenario:** Formulario de carga diaria de transacciones.**Regla de Negocio:** Cada gasto debe estar asociado obligatoriamente a un usuario pagador, un concepto/categoría, un monto positivo y una fecha. |

 

| 3\. Criterios de Aceptación | 5\. Definición de Preparado (DoR) | 6\. Definición de Terminado (DoD) |
| :---- | :---- | :---- |
| **Dado que** el usuario despliega el formulario de registro de gasto,**cuando** ingresa el monto, selecciona la categoría, el usuario pagador y confirma,**entonces** la transacción se persiste en la base de datos Room y se actualiza el histórico inmediatamente.**Dado que** la aplicación opera sin conexión a internet y tiene miles de registros previas,**cuando** el usuario añade un nuevo gasto,**entonces** la inserción debe realizarse localmente en un tiempo menor a 1 segundo sin degradación del rendimiento. | • Diseño de formulario rápido (Bottom Sheet o Pantalla dedicada) en Jetpack Compose.• Tabla ExpenseEntity construida en Room con llaves foráneas hacia usuarios.• Categorías predeterminadas integradas. | • Prueba de carga con más de 10,000 transacciones guardadas localmente sin caídas.• Transacción atómica en Room Database.• Pruebas UI automatizadas con Compose Test Rule. |

 

| 7\. Resultado Esperado | 8\. Métricas | 9\. Retroalimentación |
| :---- | :---- | :---- |
| Trazabilidad continua y sin restricciones del flujo de caja de los usuarios. | **Puntos de Historia:** 5 pts.**Casos de Prueba:** 8 casos de prueba (inserción rápida, selección de usuario, edición, montos inválidos, rendimiento masivo). | **Conversación PO:** No limitaremos la cantidad de compras registradas; la base de datos SQLite manejará todo en el almacenamiento del dispositivo. |

 

 

#### **1.1.5 HU-05: Visualización de Lista Ordenada de Gastos**

 

| 2\. Historia de Usuario | 1\. Persona | 4\. Contexto |
| :---- | :---- | :---- |
| **Como** usuario,**quiero** visualizar una lista ordenada de todos los gastos registrados,**para** tener una vista general inmediata de las transacciones efectuadas. | **Primario:** Usuarios de GastoSimple. | **Épica:** Épica 1 \- Gestión de Presupuesto y Registro de Gastos.**Escenario:** Pantalla de historial o feed de transacciones.**Regla de Negocio:** El listado debe mostrarse cronológicamente en orden descendente (los gastos más recientes primero). |

 

| 3\. Criterios de Aceptación | 5\. Definición de Preparado (DoR) | 6\. Definición de Terminado (DoD) |
| :---- | :---- | :---- |
| **Dado que** existen gastos guardados en la app,**cuando** el usuario ingresa a la vista de historial,**entonces** observa las transacciones ordenadas desde la más reciente a la más antigua, mostrando el concepto, la fecha, el monto y el usuario responsable.**Dado que** no existen gastos registrados en el sistema,**cuando** el usuario abre la lista,**entonces** se muestra un componente gráfico informativo ("Empty State") invitando a registrar el primer gasto. | • Componente LazyColumn en Jetpack Compose implementado con optimización de memoria.• Dao de Room con consulta @Query("SELECT \* FROM expenses ORDER BY date DESC"). | • Renderizado fluido de la lista a 60 FPS.• Verificación de visualización adaptativa según el idioma configurado (ES/EN).• Estado reactivo mediante Flow. |

 

| 7\. Resultado Esperado | 8\. Métricas | 9\. Resultado y Retroalimentación |
| :---- | :---- | :---- |
| Consulta inmediata y estructurada del histórico transaccional de egresos. | **Puntos de Historia:** 2 pts.**Casos de Prueba:** 3 casos de prueba (lista con datos, estado vacío, actualización automática tras nuevo registro). | **Nota:** Utilizar LazyColumn para garantizar la reutilización de vistas y evitar problemas de memoria con listas largas. |

 

 

#### **1.1.6 HU-09: Calendario Interactivo de Gastos Recurrentes**

 

| 2\. Historia de Usuario | 1\. Persona | 4\. Contexto |
| :---- | :---- | :---- |
| **Como** usuario,**quiero** ver un calendario interactivo que indique las fechas en las que se deben repetir o renovar ciertos gastos (suscripciones, facturas, compras habituales),**para** planificar mi liquidez. | **Primario:** Usuario con compromisos de pago fijos o periódicos. | **Épica:** Épica 3 \- Control Temporal y Calendario de Pagos.**Escenario:** Planificación mensual en vista de calendario.**Regla de Negocio:** Los gastos recurrentes deben proyectarse en sus respectivas fechas de vencimiento de cada mes. |

 

| 3\. Criterios de Aceptación | 5\. Definición de Preparado (DoR) | 6\. Definición de Terminado (DoD) |
| :---- | :---- | :---- |
| **Dado que** el usuario navega hacia la pantalla de calendario,**cuando** la vista se carga,**entonces** se renderiza la cuadrícula del mes actual destacando visualmente con un indicador los días que poseen compromisos o renovaciones previstos.**Dado que** el usuario selecciona un día específico marcado en el calendario,**cuando** interactúa con la celda,**entonces** se despliega un panel inferior (Bottom Sheet) listando las suscripciones y egresos agendados para esa fecha. | • Componente de cuadrícula de calendario responsivo diseñado para Jetpack Compose.• Atributos de recurrencia (semanal, mensual, anual) añadidos a la estructura del gasto. | • Navegación entre meses fluida (transición \< 1s).• Correcta alineación de días según la configuración regional (*Locale*) del teléfono.• Pruebas de renderizado UI ejecutadas. |

 

| 7\. Resultado Esperado | 8\. Métricas | 9\. Retroalimentación |
| :---- | :---- | :---- |
| Visibilidad gráfica y anticipada de compromisos financieros futuros recurrentes. | **Puntos de Historia:** 5 pts.**Casos de Prueba:** 5 casos de prueba (navegación mensual, cambio de mes, selección de día con eventos, selección de día sin eventos, cambio de año). | **Conversación PO:** Desplazamiento horizontal por meses para recargar de forma más eficiente la vista de Compose. |

 

 

#### **1.1.7 HU-10: Notificaciones Locales de Vencimiento**

 

| 2\. Historia de Usuario | 1\. Persona | 4\. Contexto |
| :---- | :---- | :---- |
| **Como** usuario,**quiero** recibir notificaciones locales en mi dispositivo cuando se aproxime la fecha de renovación de un gasto,**para** evitar retrasos o recargos en mis pagos. | **Primario:** Usuario de la aplicación. | **Épica:** Épica 3 \- Control Temporal y Calendario de Pagos.**Escenario:** Alerta programada en el sistema operativo del teléfono.**Regla de Negocio:** La notificación se programa de forma 100% local mediante WorkManager o AlarmManager de Android, sin servidores de envío (Push Remotos). |

 

| 3\. Criterios de Aceptación | 5\. Definición de Preparado (DoR) | 6\. Definición de Terminado (DoD) |
| :---- | :---- | :---- |
| **Dado que** un gasto recurrente tiene una fecha de vencimiento programada,**cuando** se alcance el plazo de aviso previo (ej. 24 horas antes),**entonces** el dispositivo emite una notificación local del sistema con el detalle del concepto y monto a pagar.**Dado que** el dispositivo está en modo "Sin Conexión" o "Modo Avión",**cuando** llega la hora de la alerta,**entonces** la notificación se dispara correctamente de forma autónoma. | • Permisos de notificaciones (POST\_NOTIFICATIONS en Android 13+) contemplados.• Integración de WorkManager especificada para tareas en segundo plano. | • Prueba de alerta local exitosa con dispositivo desconectado de la red.• Gestión de permisos solicitados adecuadamente al usuario.• Sin consumo excesivo de batería en segundo plano. |

 

| 7\. Resultado Esperado | 8\. Métricas | 9\. Retroalimentación |
| :---- | :---- | :---- |
| Alertas oportunas de vencimiento sin comprometer la privacidad ni requerir conexión a la red. | **Puntos de Historia:** 5 pts.**Casos de Prueba:** 4 casos de prueba (notificación a tiempo, reprogramación tras pago, denegación de permisos, funcionamiento offline). | **Nota:** Cumple estrictamente con la restricción de arquitectura Offline-First. |

 

 

### **1.2 Sprint 2 (Release 2 \- Iteración 2\)**

*Historias del Dashboard de Análisis Financiero, gestion de cuotas junto a gastos imprevistos y Personalización Visual (HU-06, HU-07, HU-08, HU-11, HU-12, HU-13, HU-14, HU-15).*

 

#### **1.2.1 HU-06: Panel Visual de Porcentaje Consumido**

 

| 2\. Historia de Usuario | 1\. Persona | 4\. Contexto |
| :---- | :---- | :---- |
| **Como** usuario,**quiero** ver un panel visual con el porcentaje consumido de mi presupuesto total,**para** saber cuánto dinero me queda disponible de un vistazo. | **Primario:** Usuario que consulta el estado general de sus finanzas. | **Épica:** Épica 2 \- Análisis Financiero (Dashboard de Métricas). **Escenario:** Pantalla Principal / Dashboard.**Regla de Negocio:** Porcentaje Consumido \= (Gastos Acumulados / Presupuesto Total) \* 100\. Saldo Disponible \= Presupuesto Total \- Gastos Acumulados. |

 

| 3\. Criterios de Aceptación | 5\. Definición de Preparado (DoR) | 6\. Definición de Terminado (DoD) |
| :---- | :---- | :---- |
| **Dado que** existe un presupuesto definido y egresos registrados,**cuando** el usuario accede al Dashboard,**entonces** el sistema muestra un indicador gráfico (barra o anillo de progreso) con el porcentaje consumido y el saldo restante en moneda local.**Dado que** el total de gastos supera el 100% del presupuesto definido,**cuando** se actualiza el panel,**entonces** el gráfico cambia a un tono de advertencia visual (ej. rojo) indicando sobregiro presupuestario. | • Diseño de componentes de gráficos circulares/barras en Jetpack Compose Canvas. • Casos de prueba matemática definidos para presupuestos normales y sobregirados. | • Pruebas de interfaz exitosas. • Actualización reactiva inmediata en la UI al agregar o eliminar un gasto. • Cálculos numéricos precisos sin redondeos erróneos. |

 

| 7\. Resultado Esperado | 8\. Métricas | 9\. Retroalimentación |
| :---- | :---- | :---- |
| Visión sintética y gráfica del estado del presupuesto y la liquidez disponible. | **Puntos de Historia:** 3 pts. **Casos de Prueba:** 4 casos de prueba (consumo \< 100%, consumo \= 100%, sobregiro \> 100%, sin presupuesto asignado). | **Nota:** Las barras de progreso deben ser completamente vectoriales en Compose para responder a distintas resoluciones. |

 

 

#### **1.2.2 HU-07: Identificación de Categorías de Mayor Consumo**

 

| 2\. Historia de Usuario | 1\. Persona | 4\. Contexto |
| :---- | :---- | :---- |
| **Como** usuario,**quiero** visualizar qué conceptos o rubros consumen la mayor parte de mi dinero,**para** identificar oportunidades de ahorro o ajustes. | **Primario:** Usuario de la aplicación. | **Épica:** Épica 2 \- Análisis Financiero (Dashboard de Métricas). **Escenario:** Sección de desglose por categoría en el Dashboard. **Regla de Negocio:** La acumulación por categoría agrupa la suma de montos de todos los egresos del periodo filtrados por su tipo (ej. Alimentación, Servicios, Entretenimiento). |

 

| 3\. Criterios de Aceptación | 5\. Definición de Preparado (DoR) | 6\. Definición de Terminado (DoD) |
| :---- | :---- | :---- |
| **Dado que** se han registrado transacciones en distintas categorías,**cuando** el usuario consulta las métricas de consumo,**entonces** se despliega una lista ordenada de mayor a menor con el total gastado por categoría y su porcentaje correspondiente del gasto total.**Dado que** no existen transacciones registradas en el periodo actual,**cuando** se visualiza el componente,**entonces** se despliega una vista con el mensaje de "Sin datos suficientes para este periodo". | • Consulta SQL de agrupación @Query("SELECT category, SUM(amount) FROM expenses GROUP BY category ORDER BY SUM(amount) DESC") probada. • Diagrama o lista de desglose maquetada. | • Ejecución eficiente de la consulta sin bloquear el hilo principal (Uso de Corrutinas). • Verificación en pantalla del listado de mayor a menor consumo. • Pruebas unitarias de la lógica de agrupación aprobadas. |

 

| 7\. Resultado Esperado | 8\. Métricas | 9\. Retroalimentación |
| :---- | :---- | :---- |
| Detección clara de fuga de capital o gastos predominantes por rubros. | **Puntos de Historia:** 3 pts.**Casos de Prueba:** 4 casos de prueba (múltiples categorías, una sola categoría, sin datos, actualización dinámica). | **Nota Técnica:** Las operaciones de agregación SQL se ejecutan en Room sobre hilos de fondo mediante Dispatchers.IO. |

 

 

#### **1.2.3 HU-08: Desglose Individual de Cuota de Pago**

 

| 2\. Historia de Usuario | 1\. Persona | 4\. Contexto |
| :---- | :---- | :---- |
| **Como** usuario,**quiero** consultar el desglose exacto de lo que debe pagar cada persona según su porcentaje asignado,**para** realizar cuentas claras de forma equitativa y transparente. | **Primario:** Integrantes de presupuestos compartidos (parejas, roommates, familias). | **Épica:** Épica 2 \- Análisis Financiero (Dashboard de Métricas). **Escenario:** Panel de conciliación financiera de aportes. **Regla de Negocio:** La responsabilidad individual \= Gastos Totales del Grupo \* % Asignado al Usuario. El balance individual compara lo efectivamente pagado por el usuario frente a su responsabilidad calculada. |

 

| 3\. Criterios de Aceptación | 5\. Definición de Preparado (DoR) | 6\. Definición de Terminado (DoD) |
| :---- | :---- | :---- |
| **Dado que** existen porcentajes de aportación definidos (HU-03) y gastos globales registrados,**cuando** el usuario consulta la pestaña de desglose individual,**entonces** el sistema presenta una tarjeta por persona indicando: Cuota requerida según porcentaje, Total pagado en transacciones y Saldo a favor o en contra.**Dado que** el saldo individual es a favor (pagó más de su cuota),**cuando** se visualiza la tarjeta del participante,**entonces** se indica claramente la cantidad que debe ser reembolsada por los otros integrantes. | • Fórmula de balances cruzados documentada y aprobada por el Product Owner. • Componente visual de tarjetas de resumen de participantes maquetado en Jetpack Compose. | • Cero margen de error numérico en la suma y resta de conciliación. • Pruebas unitarias exhaustivas con escenarios de 2, 3 y 4 usuarios. • Interfaz responsiva probada en pantallas de diversos tamaños. |

 

| 7\. Resultado Esperado | 8\. Métricas | 9\. Retroalimentación |
| :---- | :---- | :---- |
| Conciliación financiera clara y transparente para cuentas compartidas. | **Puntos de Historia:** 5 pts. **Casos de Prueba:** 6 casos de prueba (cuotas iguales 50/50, cuotas desiguales 60/40, un solo pagador, saldos neutros). | **Conversación PO:** Esto responde a la necesidad clave de evitar conflictos de dinero en grupos mediante cuentas matemáticamente exactas. |

 

 

#### **1.2.4 HU-11: Selección de Temas Visuales Prediseñados**

 

| 2\. Historia de Usuario | 1\. Persona | 4\. Contexto |
| :---- | :---- | :---- |
| **Como** usuario,**quiero** seleccionar entre distintos temas de colores prediseñados en la aplicación,**para** adaptar la interfaz a mis gustos personales y mejorar mi experiencia de uso. | **Primario:** Usuarios de GastoSimple. | **Épica:** Épica 4 \- Personalización Visual (Configuración de Temas). **Escenario:** Pantalla de Configuración de la App. **Regla de Negocio:** La preferencia del tema seleccionado debe almacenarse en Jetpack DataStore y aplicarse de forma reactiva sin reiniciar la app. |

 

| 3\. Criterios de Aceptación | 5\. Definición de Preparado (DoR) | 6\. Definición de Terminado (DoD) |
| :---- | :---- | :---- |
| **Dado que** el usuario está en la pantalla de personalización,**cuando** selecciona una nueva paleta de colores prediseñada,**entonces** la interfaz de toda la aplicación actualiza sus colores de forma instantánea mediante un estado reactivo (StateFlow).**Dado que** el usuario cierra forzosamente la aplicación y la vuelve a abrir,**cuando** se inicia el sistema,**entonces** la aplicación lee la preferencia de DataStore y carga el tema seleccionado previamente. | • Definición de paletas cromáticas accesibles (cumplimiento de contraste) en [Theme.kt](http://Theme.kt). • Estructura de almacenamiento Key-Value configurada en Jetpack DataStore preferences. | • Cambio de tema en tiempo real sin destellos ni cierres inesperados. • Persistencia de selección en DataStore verificada. • Pruebas de contraste visual en modo claro y oscuro superadas. |

 

| 7\. Resultado Esperado | 8\. Métricas | 9\. Retroalimentación |
| :---- | :---- | :---- |
| Personalización y apropiación visual de la interfaz adaptada a las preferencias del usuario. | **Puntos de Historia:** 3 pts.**Casos de Prueba:** 4 casos de prueba (cambio en tiempo real, persistencia tras reinicio, alternancia entre temas, lectura inicial). | **Nota Técnica:** Se utiliza DataStore en lugar de Room por tratarse de una configuración liviana de tipo clave-valor. |

 **1.2.5 HU-12: Compras/Deudas a Cuotas Fijas** 

| 2\. Historia de Usuario | 1\. Persona | 4\. Contexto |
| :---- | :---- | :---- |
| **Como** usuario, **quiero** registrar compras o deudas a cuotas fijas indicando el monto total, número de plazos y frecuencia, **para** llevar un control estructurado de mis compromisos de pago diferidos. | **Primario**: Usuario individual o representante financiero de un grupo. | **Épica**: Épica 5 \- Cuotas y Gastos Imprevistos. Escenario: Registro de compras financiadas, préstamos o pagos diferidos. **Regla de Negocio**: El monto total debe ser mayor a cero, el número de plazos debe ser un entero positivo (≥ 1). El sistema calcula el valor base de la cuota como Monto Total / Número de Plazos usando BigDecimal. |

| 3\. Criterios de Aceptación | 5\. Definición de Preparado (DoR) | 6\. Definición de Terminado (DoD) |
| :---- | :---- | :---- |
| Dado que el usuario ingresa una compra a cuotas con un monto total de $1,200 a 12 plazos, cuando presiona "Guardar Compromiso", entonces el sistema genera el plan de amortización con 12 cuotas fijas de $100 cada una, almacena el registro en Room y fija el saldo pendiente inicial en $1,200. Dado que el usuario ingresa un número de cuotas igual a cero o un monto negativo, cuando intenta guardar, entonces la interfaz despliega una alerta de validación impidiendo la creación del registro. | • Formulario de registro de deuda/cuota maquetado en Jetpack Compose con selectores de plazo. • Entidad InstallmentExpenseEntity definida en Room con relaciones a la tabla de egresos. • Módulo de cálculo numérico verificado con BigDecimal. | • Lógica de generación de cuotas probada mediante pruebas unitarias en ViewModel y UseCase. • Persistencia local verificada en Room Database sin dependencias de red. • Interfaz responsiva probada en modo claro y oscuro. |

| 7\. Resultado Esperado | 8\. Métricas | 9\. Retroalimentación |
| :---- | :---- | :---- |
| Proyección y desglose automático de compromisos diferidos a plazo fijo sin errores de redondeo. | **Puntos de Historia:** 5 pts. **Casos de Prueba:** 5 casos ejecutados (registro a cuotas válido, plazos negativos/cero, división con decimales periódicos, edición de plazos, almacenamiento en Room). | Nota Técnica: Se utiliza BigDecimal.divide(plazos, 2, RoundingMode.HALF\_UP) para ajustar los decimales exactos en cada plazo sin perder centavos. |

 **1.2.6 HU-13: Gastos Imprevistos y Emergencias** 

| 2\. Historia de Usuario | 1\. Persona | 4\. Contexto |
| :---- | :---- | :---- |
| **Como** usuario, **quiero** registrar gastos imprevistos o contingencias asignándoles un esquema de aportes/amortizaciones periódicas (mensuales o quincenales), **para** amortizar emergencias sin desestabilizar mi flujo de caja habitual. | **Primario:** Usuario de la aplicación. | **Épica:** Épica 5 \- Cuotas y Gastos Imprevistos. **Escenario:** Registro y plan de recuperación ante egresos no planificados. **Regla de Negocio:** Los gastos imprevistos se marcan con una categoría especial de contingencia y permiten asociar aportes periódicos programados (quincenales o mensuales) para restituir el capital. |

| 3\. Criterios de Aceptación | 5\. Definición de Preparado (DoR) | 6\. Definición de Terminado (DoD) |
| :---- | :---- | :---- |
| Dado que surge una emergencia financiera (ej. reparación de vehículo por $500), cuando el usuario la registra como "Gasto Imprevisto" y selecciona un esquema de amortización quincenal o mensual, entonces el sistema registra la contingencia y programa las cuotas de recuperación en el calendario financiero local. Dado que el usuario efectúa un aporte parcial a la emergencia registrada, cuando confirma la transacción, entonces el sistema descuenta el aporte del saldo pendiente de la contingencia y recalcula el importe restante. | • Formulario de contingencia integrado con selector de periodicidad de amortización. • Atributo isEmergency y relación de abonos incorporados en Room. • Lógica de amortización parcial documentada. | • Pruebas unitarias al 100% en la reducción del saldo de imprevistos tras cada abono. • Integración fluida con el calendario de pagos locales (HU-09). • Funcionamiento offline comprobado. |

| 7\. Resultado Esperado | 8\. Métricas | 9\. Retroalimentación |
| :---- | :---- | :---- |
| Registro responsivo de contingencias con planes de amortización progresiva. | **Puntos de Historia:** 5 pts. **Casos de Prueba:** 4 casos ejecutados (creación de imprevisto, amortización quincenal, amortización mensual, saldo restante parcial). | **Conversación PO:** Los imprevistos deben distinguirse visualmente en la interfaz con un indicador de contingencia de color destacado. |

 

**1.2.7 HU-14: Monitoreo de Saldos Pendientes** 

| 2\. Historia de Usuario | 1\. Persona | 4\. Contexto |
| :---- | :---- | :---- |
| **Como** usuario, **quiero** consultar un panel de monitoreo de saldos pendientes, **para** visualizar el capital original, el avance de pago y el importe restado de mis deudas a cuotas e imprevistos en tiempo real. | **Primario:** Usuario de GastoSimple. | **Épica:** Épica 5 \- Cuotas y Gastos Imprevistos. **Escenario:** Vista de seguimiento del estado de deudas y contingencias activas. **Regla de Negocio:** Saldo Pendiente \= Capital Original \- Suma(Aportes/Cuotas Pagadas). Porcentaje Avance \= (Pagado / Capital Original) \* 100\. |

| 3\. Criterios de Aceptación | 5\. Definición de Preparado (DoR) | 6\. Definición de Terminado (DoD) |
| :---- | :---- | :---- |
| Dado que existen deudas a cuotas o imprevistos activos, cuando el usuario accede a la pantalla de "Monitoreo de Saldos Pendientes", entonces el sistema presenta una lista de tarjetas activas con el capital original, total abonado, saldo pendiente y barra de progreso de liquidación. Dado que no existen compromisos vigentes, cuando se abre la vista, entonces se despliega una pantalla de estado vacío ("Empty State") informando que no hay deudas ni imprevistos pendientes. | • Componente PendingBalanceCard diseñado en Jetpack Compose con barras de progreso vectoriales. • Consulta SQL reactiva @Query optimizada en Room para sumar aportes vinculados. • Estado reactivo mediante StateFlow. | • Renderizado a 60 FPS en LazyColumn. • Actualización reactiva inmediata en pantalla tras registrar un nuevo abono o cuota. • Precisión matemática validada con BigDecimal. |

| 7\. Resultado Esperado | 8\. Métricas | 9\. Retroalimentación |
| :---- | :---- | :---- |
| Visión centralizada e intuitiva del estado de amortización de todas las obligaciones activas. | Puntos de Historia: 3 pts. Casos de Prueba: 4 casos ejecutados (vista con múltiples deudas, barra de progreso parcial, actualización en tiempo real, estado vacío). | Nota Técnica: Las consultas de consolidación de saldo se ejecutan en un hilo secundario mediante Dispatchers.IO. |

 **1.2.8 HU-15: Cierre Automático y Cambio de Estado** 

| 2\. Historia de Usuario | 1\. Persona | 4\. Contexto |
| :---- | :---- | :---- |
| Como **usuario**, **quiero** que el sistema liquide y salde automáticamente una compra a cuotas o gasto imprevisto cuando el saldo pendiente llegue a cero, **para** mantener depurado mi panel de obligaciones activas. | **Primario:** Usuario de la aplicación. | **Épica:** Épica 5 \- Cuotas y Gastos Imprevistos. **Escenario:** Finalización y liquidación del 100% de una obligación financiera. **Regla de Negocio:** Cuando Saldo Pendiente \== 0.00, el sistema cambia automáticamente el estado del compromiso de ACTIVO a SALDADO y lo remueve del resumen de obligaciones vigentes. |

| 3\. Criterios de Aceptación | 5\. Definición de Preparado (DoR) | 6\. Definición de Terminado (DoD) |
| :---- | :---- | :---- |
| Dado que una deuda o imprevisto tiene un saldo pendiente igual al valor de su última cuota, cuando el usuario registra el pago final completando el 100% del monto, entonces el sistema cambia automáticamente el estado a SALDADO, lo remueve de la lista activa y emite un mensaje de confirmación de liquidación. Dado que un compromiso ha sido marcado como SALDADO, cuando el usuario consulta el desglose de saldos pendientes, entonces comprueba que dicho registro ya no suma al total de deudas vigentes, quedando disponible en el historial de liquidados. | • Enumeración de estados ObligationStatus { ACTIVE, SETTLED } definida en el modelo de dominio. • Disparador de cambio de estado implementado en la capa de UseCase al procesar un pago. | • Pruebas unitarias automatizadas del cambio de estado al llegar al 100%. • Filtrado correcto en la base de datos Room (WHERE status \= 'ACTIVE'). • Cero inconsistencias en los totales globales de deuda activa. |

| 7\. Resultado Esperado | 8\. Métricas | 9\. Retroalimentación |
| :---- | :---- | :---- |
| Cierre automático y transparente de compromisos financieros saldados sin intervención manual. | Puntos de Historia: 3 pts. Casos de Prueba: 4 casos ejecutados (liquidación exacta con última cuota, cambio de estado en Room, exclusión del panel activo, consulta en histórico). | Conversación PO: Al saldar un compromiso, se muestra un micro-mensaje afirmativo (Snackbar) felicitando al usuario por completar el pago. |

 

## **2\. Requisitos suplementarios**

### **2.1 Eficiencia de Desempeño**

#### **2.1.1 Comportamiento Temporal (ISO 25010\)**

El tiempo de arranque en frío (*Cold Start*) de la aplicación no debe superar los 4 segundos. Las transiciones entre pantallas, la navegación por el calendario y la ejecución de consultas a la base de datos local (Room) deben ejecutarse con un tiempo de respuesta menor a 1 segundo.

 

#### **2.1.2 Utilización de Recursos (ISO 25010\)**

La aplicación debe minimizar el consumo de batería y memoria RAM del dispositivo móvil. Se delegan todas las operaciones de lectura/escritura en base de datos y cálculos matemáticos pesados a hilos de ejecución secundarios utilizando Corrutinas Kotlin (Dispatchers.IO / Dispatchers.Default) y flujos reactivos (Flow), manteniendo el hilo principal UI completamente libre.

 

 

### **2.2 Compatibilidad**

#### **2.2.1 Interoperabilidad y Autonomía (ISO 25010\)**

Al ser un producto concebido bajo el paradigma **Offline-First**, la aplicación es 100% autónoma. No depende de ninguna Interfaz de Programación de Aplicaciones (API REST), microservicios externos ni bases de datos en la nube. Todas las funcionalidades principales, análisis de datos y notificaciones deben operar al 100% sin requiring conexión activa a Internet o datos móviles.

 

 

### **2.3 Capacidad de Interacción**

#### **2.3.1 Operabilidad y Soporte Bilingüe (ISO 25010\)**

El sistema debe ser completamente bilingüe, dando soporte nativo a los idiomas **Español (ES)** e **Inglés (EN)**. Todas las cadenas de texto visibles en la interfaz se gestionan mediante archivos de recursos centralizados (strings.xml), adaptándose automáticamente al idioma (*Locale*) configurado en el sistema operativo Android del dispositivo.

 

#### **2.3.2 Adaptabilidad Visual y Accesibilidad (ISO 25010\)**

La interfaz desarrollada en Jetpack Compose debe ser responsiva y adaptarse fluidamente a diversos tamaños, resoluciones y orientaciones (retrato/paisaje) de pantallas en smartphones Android. Asimismo, debe mantener un contraste cromático adecuado según los lineamientos de Material Design para garantizar la lecturabilidad en modos Claro y Oscuro.

 

 

### **2.4 Fiabilidad**

#### **2.4.1 Tolerancia a Fallos (ISO 25010\)**

El sistema debe gestionar adecuadamente los cambios de configuración del sistema operativo (rotación de pantalla, minimización o cambio de app en segundo plano), garantizando que no se produzca pérdida de datos ingresados en formularios ni reinicios bruscos o cierres inesperados (*Crashes* o *ANR \- Application Not Responding*).

 

#### **2.4.2 Madurez y Atomicidad (ISO 25010\)**

Todas las transacciones de inserción, actualización o eliminación en la base de datos SQLite administrada por Room deben ser atómicas. En caso de apagar de forma imprevista el teléfono o cerrar forzadamente la aplicación durante un guardado, la base de datos debe revertir la operación incompleta para evitar estados corruptos.

 

 

### **2.5 Seguridad**

#### **2.5.1 Confidencialidad y Privacidad por Diseño (ISO 25010\)**

Al manejar datos de transacciones financieras y presupuestos personales o grupales, la seguridad del sistema se garantiza mediante el aislamiento de datos. Toda la información persiste de forma exclusiva dentro del espacio de almacenamiento privado de la aplicación (*App Sandbox*). Queda estrictamente prohibida la recolección, rastreo, telemetría o envío de información sensible del usuario hacia servidores de terceros.

 

 

### **2.6 Mantenibilidad**

#### **2.6.1 Modularidad y Arquitectura (ISO 25010\)**

El código fuente de GastoSimple debe estructurarse estrictamente bajo el patrón arquitectónico **Model-View-ViewModel (MVVM)**, organizado por características (*Feature-Based*), asegurando la separación de responsabilidades entre la capa de presentación (Jetpack Compose), la capa de dominio/negocio y la capa de datos (Room/DataStore).

 

#### **2.6.2 Modificabilidad e Inyección de Dependencias (ISO 25010\)**

El desacoplamiento de componentes debe gestionarse utilizando **Koin** como contenedor de Inyección de Dependencias. La base de datos de Room debe incorporar un esquema previsor de migraciones (Migrations) para permitir que futuras versiones o funcionalidades agreguen campos o tablas sin riesgo de sobrescribir ni borrar la información histórica guardada por los usuarios.

 

 

### **2.7 Flexibilidad**

#### **2.7.1 Adaptabilidad y Configuración (ISO 25010\)**

La aplicación debe permitir la alteración flexible de parámetros operativos como temas visuales y asignaciones de aportes sin requerir la reinstalación del APK ni la pérdida de preferencias del usuario, almacenando estos estados livianos en Jetpack DataStore.

 

 

### **2.8 Protección**

#### **2.8.1 Precisión Matemática Financiera (ISO 25010\)**

El motor lógico del sistema debe garantizar la exactitud matemática absoluta en el cálculo del porcentaje consumido, saldo restante y conciliación de porcentajes de cuotas por usuario. Todas las operaciones financieras de coma flotante deben procesarse utilizando tipos de datos de alta precisión (BigDecimal) para evitar errores por redondeo en montos monetarios.