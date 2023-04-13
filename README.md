![video](visuales_github/video.gif) 

# RetoYape
Mini proyecto para evaluación técnica para la empresa Yape.

---
# Estructura del proyecto
El proyecto tiene tres módulos: App y base_ui, y buildSrc:

### Módulo app:
Módulo principal donde se encuentras las actividades y archivos pertenecientes a la arquitectura de componentes MVVM.    

### Módulo base_ui:
En este módulo se encuentran las vistas personalizadas(Custom View).              

### Módulo buildSrc:
Este es un módulo de configuración; donde tenemos todas las dependencias, 
y configuraciones gradle del proyecto, con el uso de kotlin dsl. 

---
## Capas de aplicación
 Para el proyecto utilizamos la arquitectura de componentes, recomendada por Google. Adicionalmente usamos objetos LiveData,
 que son obejtos reactivos, tienen el beneficio de restepar el ciclo de vida de nuestros componentes, 
 solo invoca a su devolución de llamada solo cuando la vista este activa.
- **Model**
  --> En esta capa se encuentran las conexiones a los datos remotos, 
  a esta capa pertenecen los paquetes `source/remote`.
- **View**
  --> En esta capa se encuentran la interfaz de usurario, esta capa se encuentra observando al View Model para actualizarse cuando los datos cambien.
- **ViewModel**
  --> Esta capa se encarga de preparar y administrar los datos a la interfaz de usuario, y es observado por esta. Contiene objetos LiveData para captar cambios en la base de datos local, y transmitirlos a la capa View. También sobrevive a los cambios de configuracion de la activity y/o fragment que la observa porque tiene un ciclo de vida independiente.
  
  
  <a href="http://fvcproductions.com"><img src="https://miro.medium.com/max/3840/1*6YYuni9J8nDNjMAYh1TIAQ.jpeg" title="FVCproductions" alt="FVCproductions"></a>

