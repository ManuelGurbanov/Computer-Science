Buenas tardes @ManuelGurbanov . Tu parcial se encuentra aprobado. Abajo podés encontrar el puntaje obtenido y las correcciones correspondientes.

Ante cualquier consulta respecto a la corrección, podés contactar a tu corrector (quien envía el mensaje) por este medio arrobándolo o escribirnos a orga-doc@dc.uba.ar. Pedidos de revisión también se deben dirigir a ese medio, y deben ser enviados el sábado 29 a última hora a más tardar.

Saludos!

Corrección segundo parcial - nota: 60
Ejercicio 1 - 30/50 pts
Una tarea no puede pedir más de un recurso a la vez, por lo que no tiene sentido tener un array de requests
Cada tarea provee un único recurso, por lo que no tiene sentido tener un array de provides
Llama a pic_finish en los handlers de las syscalls (A30 - medio)
Solicitar

if (!encontre_indice) continue; debería ser if (encontre_indice) para saltar las iteraciones luego de que se encuentra un índice
No maneja caso de que una tarea solicite sin que haya tarea productora libre (proponés una lógica para encolar tarea pero en ningún lugar de tu solución encarás como producir recursos para las tareas que quedan encoladas) (A14 - grave pero bajo a leve)
Deshabilita y desaloja la tarea actual
Falta enable de la tarea productora en el scheduler (en recurso_listo hace el disable así que no es que tuvo una interpretación alternativa del enunciado) (A39 - leve)
Recurso listo

Falta actualizar las estructuras propias (A35 - no baja)
Page fault handler

Está perfecto para 0xBBBB000, habría que hacer lo mismo para 0xAAAA000 pero no bajo puntos.
Ejercicio 2 - 10/25 pts
No se restauran los registros EBP, ESP, EIP (C5 - grave)
Sí hace unmap y bloquea la tarea
Ejercicio 3 - 10/15 pts
Crea nueva tarea para producir el recurso pedido, no es una interpretación que tiene sentido dado el enunciado - de dónde sacás el código para la nueva tarea? Implica que hay nueva maquinaria en la fábrica al apretar el botón?
Falta modificar la syscall de recurso listo para soportar el caso donde una tarea no tiene solicitante (E11 - no baja)
En el caso de crear una nueva tarea para atender el pedido del botón, no se debería hacer el jmp far a otra tarea. La tarea actual no tiene por qué ver su ejecución afectada.
Ejercicio 4 - 10/10 pts
Falta actualizar las estructuras propias (A35 - no baja)