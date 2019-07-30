# Instrucciones para el setup del ambiente de desarrollo

### Pre-requisitos: 
Se debe haber clonado y configurado el proyecto **com.consultec.esigns**

# Compilar 
Para obtener una distribución del proyecto se debe ejecutar el comando `gradle` desde la raiz del proyecto. Una vez finalizado, ubicar el directorio *build/distributions* donde se encuentra un archivo llamado *icepdf-os-6.3.1-SNAPSHOT.zip*. Descomprimir ese archivo. De los directorios resultantes, copiar el contenido del directorio *libs* al directorio indicado en la propiedad *'user.default.icepdfjarpath'* del archivo application.properties del proyecto *com.consultec.esigns.listener*. Esto con el objetivo de que el listener pueda conseguir las librerias .jar necesarias para ejecutar el comando que inicia el visualizador de PDF.

# Modo Debug
Para *debuggear* este proyecto se debe utilizar los siguientes parámetros: </br>

- clase principal: org.icepdf.ri.viewer.Main
- proyecto: icepdf-viewer
- argumentos de programa: -sessionid "SESSION_ID" > Donde "SESSION_ID" debe ser el nombre de la carpeta ubicada dentro del dir configurado en la propiedad *user.default.basehome* del archivo application.properties en el proyecto **com.consultec.esigns.listener**. Este dir debe contener un archivo llamado data.pdf.

- argumentos VM: -Djava.library.path="C:\Program Files (x86)\Common Files\WacomGSS";
