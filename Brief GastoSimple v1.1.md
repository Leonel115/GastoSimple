# ---

---

# 

# **Brief**

## **GastoSimple**

**Versión 1.1**

### **Histórico de Revisiones**

| Fecha | Versión | Descripción | Autor(es)   |
| :---- | :---- | :---- | :---- |
| 7/ago/26 | 1.0 | Creación del Brief inicial de GastoSimple: Necesidad, Backlog (USM y HUs para 4 épicas), Restricciones y Rangos de Calidad. | Leonel Rojas, Jesús Acosta, Rolando Rodrigo |
| 13/ago/26 | 1.1 | Creación de épica 5 “Cuotas y Gastos Imprevistos” con sus respectivas UH | leonel Rojas |

### 

### 

### 

### 

### 

### 

### 

### 

### 

### 

### 

### 

### 

### 

### 

### **Tabla de Contenidos**

[**Brief	1**](#heading=)

[GastoSimple	1](#heading=)

[Histórico de Revisiones	2](#heading=)

[Tabla de Contenidos	3](#heading=)

[Brief	4](#heading=)

[1\. Necesidad	4](#heading=)

[2\. Backlog	6](#heading=)

[Épica 1: Gestión de Presupuesto y Registro de Gastos	7](#heading=)

[Épica 2: Análisis Financiero (Dashboard de Métricas)	7](#heading=)

[Épica 3: Control Temporal y Calendario de Pagos	7](#heading=)

[Épica 4: Personalización Visual (Configuración de Temas)	7](#heading=)

[Épica 5: Cuotas y Gastos Imprevistos	8](#heading=)

[3\. Restricciones	8](#heading=)

[4\. Rangos de Calidad	8](#heading=)

## 

## 

## 

## 

## **Brief**

*El propósito de este documento es recolectar, analizar y definir las necesidades a un alto nivel utilizando un Mapa de Impacto del sistema **GastoSimple**. Con base en ella se prepara el Backlog (User Story Map). Se especifican las restricciones de la aplicación y los criterios de aceptación aplicables al caso. Finalmente se identifican los rangos de calidad deseados.*

## **1\. Necesidad**

| *Meta* | *Personas* | *Impacto* | *Entregable*   |
| :---- | :---- | :---- | :---- |
| ***Control y distribución del presupuesto*** | *Usuarios* | *Podrán establecer un presupuesto financiero claro e integrar a uno o más participantes (parejas, roommates, familias) definiendo divisiones equitativas o porcentuales de responsabilidad.* | ***Módulo de Configuración de Presupuesto y Usuarios** • Definición del presupuesto total. • Registro de uno o múltiples usuarios. • Asignación de división porcentual de aportes (ej. 50%/50%, 60%/40%).* |
| ***Trazabilidad ilimitada de gastos*** | *Usuarios* | *Podrán registrar cada uno de sus gastos sin límites, categorizándolos para entender el destino de sus flujos de dinero.* | ***Registro Flexible de Gastos** • Formulario de carga rápida de gastos cotidianos. • Asociación del gasto a uno o varios usuarios. • Historial detallado de egresos.* |
| ***Análisis visual de hábitos financieros*** | *Usuarios* | *Comprenderán de forma gráfica y porcentual en qué rubros se consume su dinero en relación con el presupuesto definido.* | ***Dashboard de Métricas Financieras** • Porcentaje de consumo del presupuesto actual. • Identificación de categorías de mayor consumo. • Cálculo automático del saldo disponible e individual.* |
| ***Planificación temporal y vencimientos*** | *Usuarios* | *Preverán compromisos financieros futuros recurrentes (alquiler, suscripciones, compras habituales) evitando pagos tardíos.* | ***Calendario de Pagos y Renovaciones** • Vista de calendario con eventos de gastos. • Identificación visual de fechas de renovación o pago recurrente. • Sistema de notificaciones locales de vencimiento.* |
| ***Adaptación y comodidad visual*** | *Usuarios* | *Podrán personalizar el aspecto de la aplicación según sus preferencias estilísticas mediante paletas de colores.* | ***Pantalla de Configuración de Temas** • Selección de temas visuales prediseñados.* |
| ***Control de cuotas y gastos imprevistos*** | *Usuarios* | *Podrán registrar y planificar amortizaciones o aportes periódicos para saldar deudas a plazos o emergencias financieras extraordinarias hasta su liquidación total.* | ***Módulo de Gestión de Deudas y Cuotas** • Registro de gastos divididos a cuotas con número de plazos definidos. • Creación de compromisos para imprevistos (médicos, reparaciones, etc.). • Control de aportes periódicos (quincenales/mensuales) y saldo pendiente.* |

## 

## 

## 

## 

## 

## 

## **2\. Backlog**

| *Actividad Principal (Meta)* | *Gestión de Presupuesto y Gastos* | *Análisis Financiero (Métricas)* | *Control Temporal (Calendario)* | *Personalización Visual* | *Control de Cuotas e Imprevistos*   |
| :---- | :---- | :---- | :---- | :---- | :---- |
| ***Tarea de Usuario*** | *Configurar presupuesto, usuarios y registrar egresos* | *Consultar consumo y desglose de gastos* | *Monitorear vencimientos e itinerarios de pago* | *Personalizar interfaz* | *Gestionar compras a plazos y aportes para emergencias* |
| ***MVP (Iteración 1\)*** | *• Registrar presupuesto inicial. • Registrar uno o más usuarios. • Definir porcentaje de responsabilidad por usuario. • Registrar gastos de forma ilimitada. • Consultar listado de gastos.* | *(No incluido en MVP para agilizar lanzamiento)* | *• Visualizar calendario con gastos recurrentes. • Ver frecuencia de repetición de gastos. • Recibir notificaciones locales de renovación.* | *(No incluido en MVP para agilizar lanzamiento)* | *(No incluido en MVP para agilizar lanzamiento)* |
| ***Release 2 (Iteración 2\)*** |  | *• Ver porcentaje consumido del presupuesto. • Ver categorías que más consumen. • Ver monto exacto a pagar por usuario.* |  | *• Cambiar temas y paletas de colores de la app.* | *• Registrar compras/deudas a cuotas fijas. • Definir aportes periódicos (mensuales/quincenales) para imprevistos. • Visualizar saldo pendiente hasta la liquidación total.* |

### **Épica 1: Gestión de Presupuesto y Registro de Gastos**

> * **HU-01:** Como usuario, quiero registrar un presupuesto total para establecer un techo financiero dentro de un periodo determinado.  
> * **HU-02:** Como usuario, quiero agregar uno o más usuarios a la gestión financiera para controlar los gastos compartidos (parejas, familias, roommates) o individuales.  
> * **HU-03:** Como usuario, quiero asignar porcentajes de aporte/pago a cada usuario registrado (ej. 50%-50% o 60%-40%), para que el sistema calcule automáticamente cuánto debe aportar cada uno en los gastos grupales.  
> * **HU-04:** Como usuario, quiero registrar egresos/gastos ilimitados especificando el monto, concepto y quién lo realizó, para mantener un seguimiento continuo sin restricciones.  
> * **HU-05:** Como usuario, quiero visualizar una lista ordenada de todos los gastos registrados para tener una vista general inmediata de las transacciones efectuadas.

### **Épica 2: Análisis Financiero (Dashboard de Métricas)**

> * **HU-06:** Como usuario, quiero ver un panel visual con el porcentaje consumido de mi presupuesto total, para saber cuánto dinero me queda disponible de un vistazo.  
> * **HU-07:** Como usuario, quiero visualizar qué conceptos o rubros consumen la mayor parte de mi dinero, para identificar oportunidades de ahorro o ajustes.  
> * **HU-08:** Como usuario, quiero consultar el desglose exacto de lo que debe pagar cada persona según su porcentaje asignado, para realizar cuentas claras de forma equitativa y transparente.

### **Épica 3: Control Temporal y Calendario de Pagos**

> * **HU-09:** Como usuario, quiero ver un calendario interactivo que indique las fechas en las que se deben repetir o renovar ciertos gastos (suscripciones, facturas, compras habituales), para planificar mi liquidez.  
> * **HU-10:** Como usuario, quiero recibir notificaciones locales en mi dispositivo cuando se aproxime la fecha de renovación de un gasto, para evitar retrasos o recargos en mis pagos.

### **Épica 4: Personalización Visual (Configuración de Temas)**

> * **HU-11:** Como usuario, quiero seleccionar entre distintos temas de colores prediseñados en la aplicación, para adaptar la interfaz a mis gustos personales y mejorar mi experiencia de uso.

### **Épica 5: Cuotas y Gastos Imprevistos**

> * **HU-12:** Como usuario, quiero registrar gastos diferidos a cuotas fijas (ej. compras a plazos o deudas), indicando el monto total y número de pagos, para llevar un control de las cuotas restantes hasta su liquidación total.  
> * **HU-13:** Como usuario, quiero registrar un gasto imprevisto (ej. emergencias médicas o reparaciones) y establecer un esquema de aportes periódicos (mensuales o quincenales), para amortizar gradualmente el evento sin desequilibrar mi presupuesto habitual.  
> * **HU-14:** Como usuario, quiero visualizar el saldo pendiente actualizado de cada cuota o imprevisto activo, para saber exactamente cuánto dinero me falta por aportar hasta saldar la obligación.  
> * **HU-15:** Como usuario, quiero que el sistema cambie automáticamente el estado de una cuota o imprevisto a "Saldado" al completar el 100% de los pagos, eliminándolo del desglose de obligaciones pendientes activas.

## **3\. Restricciones**

> * ***Almacenamiento y Privacidad (Offline-First):** Toda la información registrada por el/los usuario(s) se almacenará de manera 100% local en el dispositivo móvil mediante Room Database (SQLite) y Jetpack DataStore para preferencias. No existirá conexión a servidores backend externos, APIs remotas ni bases de datos en la nube. La aplicación no recolectará analíticas ni telemetría.*  
> * ***Entorno Monodispositivo y Sin Autenticación:** La aplicación no requerirá registro de cuenta ni inicio de sesión (Login/Signup). Operará en un entorno local por dispositivo, gestionando la dinámica multi-usuario dentro de la misma instancia instalada.*  
> * ***Idiomas Soportados:** El sistema estará restringido a los idiomas español (ES) e inglés (EN). Todas las cadenas de texto se gestionarán mediante recursos strings.xml, adaptándose automáticamente al idioma nativo configurado en el sistema operativo del dispositivo.*  
> * ***Términos y Condiciones y Privacidad Local:** La aplicación incorporará una sección de Términos y Condiciones y Política de Privacidad de lectura local para el usuario, garantizando la transparencia sobre el uso exclusivo y privado de sus datos.*  
> * ***Plataforma y Arquitectura Técnica:** El producto se desarrollará exclusivamente para el sistema operativo Android utilizando Kotlin, Jetpack Compose para la UI, arquitectura MVVM, Inyección de Dependencias con Koin y Corrutinas/Flows para operaciones asíncronas.*

## **4\. Rangos de Calidad**

> * ***Desempeño y Comportamiento Temporal (Time Behaviour \- ISO 25010):** El tiempo de arranque en frío (Cold Start) de la aplicación no debe superar los 4 segundos. Las transiciones entre pantallas y las consultas a la base de datos local deben ejecutarse con un tiempo de respuesta menor a 1 segundo. El consumo de recursos en segundo plano debe ser mínimo para preservar la batería del dispositivo.*  
> * ***Usabilidad y Adaptabilidad (ISO 25010):** La interfaz gráfica implementada en Jetpack Compose debe adaptarse responsivamente a los diferentes tamaños, resoluciones y orientaciones de pantalla de dispositivos móviles Android. Debe ser intuitiva y permitir la configuración de múltiples temas visuales.*  
> * ***Seguridad y Privacidad por Diseño (ISO 25010):** Al manejar datos financieros sensibles, la seguridad se garantiza mediante la no transmisión de datos a redes externas. Todo dato generado persiste exclusivamente en el almacenamiento local protegido de la aplicación.*  
> * ***Confiabilidad y Tolerancia a Fallas (ISO 25010):** La aplicación debe ser 100% funcional sin conexión a Internet. Debe ser capaz de pasar a segundo plano sin perder el estado de la sesión, sin reinicios abruptos ni cierres inesperados (Crash/ANR).*  
> * ***Mantenibilidad y Modularidad (ISO 25010):** El proyecto debe seguir una estructura modular por características (Feature-Based) bajo el patrón MVVM y el principio de responsabilidad única. La arquitectura debe estar desacoplada mediante Koin para facilitar la incorporación limpia de las épicas de la Iteración 2 (Métricas, Calendario, Temas y Cuotas/Imprevistos).*  
> * ***Adecuación Funcional (ISO 25010):** El sistema debe garantizar la correcta ejecución matemática y lógica de la división de porcentajes de gastos, amortización de cuotas y los cálculos de presupuesto restante sin margen de error en las operaciones financieras registradas.*