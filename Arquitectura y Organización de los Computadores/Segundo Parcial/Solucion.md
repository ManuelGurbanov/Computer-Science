# Resolución 

## Ejercicio 1
Dado un sistema como el kernel del TP, vamos a mostrar los cambios necesarios para abarcar el sistema de recursos que se nos pide.

Lo primero que voy a hacer es aprovechar la macro definida en idt.c para definir las nuevas entradas, correspondientes a las dos syscall que pide este inciso.

```C
//Macros que utilizamos en el TP
#define IDT_ENTRY3(numero)                                                     
  idt[numero] = (idt_entry_t) {                                                
    .offset_31_16 = HIGH_16_BITS(&_isr##numero),                               
    .offset_15_0 = LOW_16_BITS(&_isr##numero),                                 
    .segsel = GDT_CODE_0_SEL,                                                  
    .type = INTERRUPT_GATE_TYPE,                                                              
    .dpl = 0x03,                                                                  
    .present = 0x01                                                              
};
```


Elijo esta macro para definir las syscall ya que deben tener un DPL de 11, dado que son interrupciones llamadas por el usuario.

```C
// Agrego al snippet del TP dos entradas, por legibilidad no las copio.
void idt_init(){
    .
    .
  //otras entradas.
    .
  IDT_ENTRY3(90); //solicitar
  IDT_ENTRY3(91); //recurso_listo
    .
    .
    .
}
```

Elijo la posición 90 y 91 ya que bajo la convención de Intel, están disponibles para syscalls.

Ahora podemos ir al *isr.asm* y definir las rutinas que van a manejar estas nuevas interrupciones.

### Rutina de interrupción: Solicitar

Esta rutina va a pasar por 'eax' a nuestra auxiliar en C el tipo de recurso que busca.
Asumo como mi convención que la tarea hizo un mov a ese registro con el parámetro que le envía a la syscall.

```ASM
extern solicitar_c
global _isr90
_isr90:
  pushad
  call pic_finish1 ; le aviso al PIC que atiendo la interrupción

  xor eax, eax ; limpio eax por comodidad
  push al ; paso como parámetro a solicitar_c el tipo de solicitud, en los 8 bits mas bajos de EAX por la convención que me definí.
  call solicitar_c
  add esp, 4 ; limpio la pila, como solo hice un push puedo simplemente añadirle 4 al esp.

  call sched_next_task ; me traigo la siguiente tarea
  mov word [sched_task_selector], ax ; escribo el selector que me dio la función
  jmp far [sched_task_offset] ; cambiar de tarea

  popad
  iret
```

### Implementación en C: Solicitar

Antes de hacer la función, veamos qué modificación necesitaremos hacer en el scheduler para que las tareas puedan guardar la información necesaria.

Tenemos definidos en el TP el siguiente struct, que definen una entrada en el sched.

```C

typedef struct {
  int16_t selector;
  task_state_t state;
} sched_entry_t; // Entrada en el scheduler
```

Vamos a necesitar almacenar los _tipo de recurso_ que están involucrados en la tarea, para poder operar con ellos cuando necesitemos.
Agreguemos eso.

Además, doy una definición de *recurso_t*.

```C
typedef enum {
  TASK_SLOT_FREE,
  TASK_RUNNABLE,
  TASK_PAUSED,
  TASK_WAITING // Creo un nuevo estado para las tareas que solicitaron recursos y no pueden seguir su ejecución, para diferenciar de las pausadas que tenemos en el kernel
} task_state_t; // Posibles estados de la tarea

typedef struct {
    int8_t resourceId;
} recurso_t

typedef struct {
  int16_t selector;
  task_state_t state;
  recurso_t requests[]; // Agrego esta línea, representa los tipos de recurso que está pidiendo.
  recurso_t provides[]; // También agrego los recursos que provee.

  // No me preocupo por el tamaño de los arreglos, asumo que son del suficiente tamaño para guardar todo.

  int8_t producingFor; // Identificador de la tarea para la que está produciendo, asumo que con 8 bits puedo representarlo sin problemas.
} sched_entry_t;
```

Ahora, vamos a la implementación de *solicitar_c*, auxiliar que llamé desde assembler.

El siguiente código se puede implementar directamente en *sched.c*

```C
void solicitar_c(recurso_t tipo_de_solicitud){
    sched_entry_t tarea_llamadora = sched_tasks[current_task];

    int indice_libre;
    int encontre_indice = 0;

    // Voy a buscar el primer índice libre en los requests de la tarea, para poder incluir el recurso que se quiere añadir a las solicitudes.

    for (int i = 0; i < tarea_llamadora->requests.length; i++){
        if (!encontre_indice) continue;

        if (tarea_llamadora->requests[i] == 0) {
            indice_libre == i;
            encontre_indice = 1;
        }
    }

    tarea_llamadora->requests[indice_libre] = tipo_de_solicitud; // Seteo el tipo de solicitud pasado desde la interrupción por parámetro como un elemento más del array de requests.
    tarea_t tarea_disponible = hay_tarea_disponible_para_recurso(tipo_de_solicitud);

    if (tarea_disponible != 0){
        tarea_disponible.producingFor = current_task;
    }

    tarea_llamadora->state = TASK_WAITING; // Como solicitó un recurso que no está listo en este momento, le pongo el nuevo estado que armé.
}
```

### Rutina de interrupción: Recurso Listo
_Permite a la tarea llamadora avisar que su recurso está listo._

```ASM
global _isr91
_isr91:
  pushad

  call pic_finish1 ; le aviso al PIC que atiendo la interrupción
  call recurso_listo

  add esp, 4 ; limpio la pila

  call sched_next_task
  mov word [sched_task_selector], ax
  jmp far [sched_task_offset] ; cambiar de tarea

  popad
  iret
```

### Implementación en C: Recurso Listo

```C
#define DIRECCION_VIRTUAL_ORIGEN 0x0AAAA000
#define DIRECCION_VIRTUAL_DESTINO 0x0BBBB000

// Agrego al snippet del TP dos entradas, por legibilidad no las copio.
void recurso_listo(){
    sched_entry_t tarea_actual = sched_tasks[current_task];

    task_id_t id_tarea_esperando = tarea_actual->producingFor;

    sched_entry_t tarea_esperando = sched_tasks[id_tarea_esperando]; // Recupero la tarea que estaba esperando que la llamadora termine de producir
    
    // Ahora vamos a copiar la página (se que es solo una por los 4KB del enunciado) de la función que da el recurso a la que lo pidió.
    // Como copy page pide direcciones físicas y no las tenemos, vamos a utilizar una auxiliar que pase direcciones
    // virtuales a físicas dado el CR3 de la tarea en cuestión.

    // El origen es la memoria física a la que apunta 0x0AAAA000 en esta tarea.
    paddr_t origen = obtenerDireccionFisica(rcr3(), DIRECCION_VIRTUAL_ORIGEN);

    
    // El destino es la memoria física a la que apunta 0x0BBBB000 en la tarea que pidió el recurso.
    // Voy a obtener el CR3 de la tarea que necesito el destino, con la auxiliar que definiré después.

    uint32_t cr3_tarea_destino = cr3_segun_selector(tarea_esperando->selector);
    paddr_t destino = obtenerDireccionFisica(cr3_tarea_destino, DIRECCION_VIRTUAL_ORIGEN);

    copy_page(destino, origen); // Hago efectivamente la copia de la página

    // la tarea llamadora debe ser desalojada y restaurada por completo a su estado inicial.

    restaurar_tarea(current_task); // llamo a la función que reinicia la task
}

// Función auxiliar
// Dado un selector de segmento de 16 bits, devuelve el CR3 de la tarea.
uint32_t cr3_segun_selector(uint16_t selector){
    uint16_t id_en_gdt =  selector >> 3; // Me limpio los atributos shifteando a derecha, porque en el selector de segmento los bit mas altos son el índice en la GDT
    uint32_t cr3 =  gdt[id_en_gdt].base; // Esto no está guardado todo junto, habría que juntar los 3 campos shifteando cada uno y con un OR, pero lo accedo directo para simplificar.
    return cr3;
}

// Función auxiliar
// Dado un CR3 y una dirección virtual, devuelve la física.
uint32_t obtenerDireccionFisica(uint32_t cr3_tarea,uint32_t* dir){
    pd_entry_t* pd = (pd_entry_t*)CR3_TO_PAGE_DIR(cr3_tarea);
    int pdi = VIRT_PAGE_DIR(dir);

    if (!(pd[pdi].attrs & MMU_P)) //Devuelvo NULL si no está mapeada
    return 0;

    pt_entry_t* pt = (pt_entry_t*)MMU_ENTRY_PADDR(pd[pdi].pt);

    int pti = VIRT_PAGE_TABLE(dir);
    if (!(pt[pti].attrs & MMU_P)) //Devuelvo NULL si no está mapeada
    return 0;

    paddr_t dir_ret = MMU_ENTRY_PADDR(pt[pti].page);
    
    return dir_ret;
}
```

Esta es la implementación del *copy page* que armamos para el TP y estoy llamando desde la implementación de C.

```C
void copy_page(paddr_t dst_addr, paddr_t src_addr) {
  uint32_t cr3 = rcr3();

  mmu_map_page(cr3, DST_VIRT_PAGE, dst_addr,  MMU_P | MMU_W);
  mmu_map_page(cr3, SRC_VIRT_PAGE, src_addr,  MMU_P);

  for(int i = 0; i < 1024; i++){ 
    ((vaddr_t*) DST_VIRT_PAGE)[i] = ((vaddr_t*) SRC_VIRT_PAGE)[i];
  }

  mmu_unmap_page(cr3,  DST_VIRT_PAGE);
  mmu_unmap_page(cr3, SRC_VIRT_PAGE);

  return;
}
```

Pero hay que ver el caso en el que la página no está mapeada, que caería en un page fault.
En ese caso, el enunciado dice que hay que mapear la página y salir del page fault con la página limpia de basura.

Para que el kernel pueda gestionar esta nueva feature de paginación, necesitamos modificar el page_fault_handler que definimos en la MMU.

Veamos:
```C
bool page_fault_handler(vaddr_t virt) {
  print("Atendiendo page fault...", 0, 0, C_FG_WHITE | C_BG_BLACK);

  if (virt == DIRECCION_VIRTUAL_DESTINO){
    // Para obtener el CR3 que voy a usar en map page, puedo usar la auxiliar que definí hace un rato
    uint32_t cr3 = cr3_segun_selector();
    paddr_t phy = mmu_next_free_kernel_page(); // Me traigo la próxima página libre
    mmu_map_page(cr3, virt, phy, MMU_P | MMU_U | MMU_W); // Me mapeo la página a la próxima página libre

    zero_page(virt); 
    // Mi zero_page del TP toma dirección física, pero por aclaración del enunciado ASUMO que tengo que pasarle una dirección virtual y listo.
    // Sino, el llamado sería zero_page(phy), si tomamos la siguiente implementación de zero_page que es la que tenemos en el TP:
    // static inline void zero_page(paddr_t addr) {
    //     kmemset((void*)addr, 0x00, PAGE_SIZE);
    // }
  }

  //El siguiente código era el page fault de mi TP, pero le agregué un else para que no interfiera.
  else if(virt >= ON_DEMAND_MEM_START_VIRTUAL && virt <= ON_DEMAND_MEM_END_VIRTUAL){
    mmu_map_page(rcr3(), ON_DEMAND_MEM_START_VIRTUAL, ON_DEMAND_MEM_START_PHYSICAL, MMU_P | MMU_U | MMU_W);
    return 1;
  }
  return 0;
}
```

Esta es la implementación de mmu_map_page que utilizo, la misma que para el TP:

```C
void mmu_map_page(uint32_t cr3, vaddr_t virt, paddr_t phy, uint32_t attrs) {
  pd_entry_t* pd = (pd_entry_t*) CR3_TO_PAGE_DIR(cr3);
  uint32_t pd_index = (uint32_t) VIRT_PAGE_DIR(virt);
  
  if(!(pd[pd_index].attrs & MMU_P)){
    pd[pd_index].pt = (mmu_next_free_kernel_page() >> 12);
    zero_page(MMU_ENTRY_PADDR(pd[pd_index].pt));
  }
    
  pd[pd_index].attrs = pd[pd_index].attrs | attrs | MMU_P;
  pt_entry_t* pt = (pt_entry_t*) MMU_ENTRY_PADDR(pd[pd_index].pt);
  uint32_t pt_index = (uint32_t) VIRT_PAGE_TABLE(virt);
  pt[pt_index].page = phy >> 12;
  pt[pt_index].attrs = attrs | MMU_P;
  tlbflush();
}
```





## Ejercicio 2
Implementar restaurar tarea, asumiendo que no utilizó páginas fuera de 0x0AAAA000 y 0x0BBBB000.

Para que la tarea funcione como si fuera nueva, tenemos que desmapear todas las páginas que se encuentren mapeadas.

Además, tenemos que volver el atributo state a _slot free_, para desalojarla. 

Vamos a dejar esta función en sched.c por comodidad.

```C
void restaurar_tarea(task_id_t id_tarea){
    // Para desmapear las páginas, necesitamos el CR3 de la tarea en cuestión.
    // Recordemos que el CR3 podemos obtenerlo en la GDT, y para eso habíamos codeado una auxiliar antes.
    sched_entry_t tarea = sched_tasks[id_tarea];
    uint16_t selectorTarea = tarea->selector;

    uint32_t cr3 = cr3_segun_selector(selectorTarea);

    mmu_unmap_page(cr3, DIRECCION_VIRTUAL_ORIGEN);
    mmu_unmap_page(cr3, DIRECCION_VIRTUAL_DESTINO);

    tarea->state = TASK_SLOT_FREE; // Desalojo la tarea
}
```

Esta es la implementación que utilizo de mmu_unmap_page, que armamos para el TP:

```C
paddr_t mmu_unmap_page(uint32_t cr3, vaddr_t virt) {
  pd_entry_t* pd = (pd_entry_t *) CR3_TO_PAGE_DIR(cr3);
  uint32_t pd_index = (uint32_t) VIRT_PAGE_DIR(virt);

  if (!(pd[pd_index].attrs & MMU_P)){
    return 0;
  }

  pt_entry_t* pt = (pt_entry_t*) MMU_ENTRY_PADDR(pd[pd_index].pt);
  uint32_t pt_index = (uint32_t) VIRT_PAGE_TABLE(virt);
  if (!(pt[pt_index].attrs & MMU_P)){
    return 0;
  }

  paddr_t phy = (paddr_t) MMU_ENTRY_PADDR(pt[pt_index].page);
  pt[pt_index].page = 0;
  pt[pt_index].attrs = 0;
  tlbflush();
  return phy;
}
```

## Ejercicio 3

Para implementar el arranque manual, definimos en la idt la entrada para esta interrupción externa, usando la macro que tenemos en el TP para las interrupciones de DPL = 00.

```C
#define IDT_ENTRY0(numero)                                                     
  idt[numero] = (idt_entry_t) {                                                
    .offset_31_16 = HIGH_16_BITS(&_isr##numero),                               
    .offset_15_0 = LOW_16_BITS(&_isr##numero),                                 
    .segsel = GDT_CODE_0_SEL,                                                  
    .type = INTERRUPT_GATE_TYPE,                                                              
    .dpl = 0x00,                                                                  
    .present = 0x01               
};

void idt_init(){
    // otras entradas
    IDTENTRY0(41); // Interrupción Externa
}
```

Ahora, programemos la rutina de atención en *isr.asm*.

```ASM
extern auxiliar_arranque
global _isr41
_isr41:
  pushad

  call pic_finish1;

  xor eax, eax ; me limpio EAX antes de utilizarlo
  mov al, byte [0xFAFAFA] ; mando a la parte baja de EAX lo que está en esa posición de memoria

  push eax ; le paso a la auxiliar el param
  call auxiliar_arranque

  add esp, 4 ; limpio la pila

  call sched_next_task
  mov word [sched_task_selector], ax
  jmp far [sched_task_offset] ; cambiar de tarea

  popad
  iret
```

```C
void auxiliar_arranque(recurso_t rec){
    // Dado el recurso que nos manda asm, vamos a crear una tarea que produzca dicho recurso.

    paddr_t pagina_libre = mmu_next_free_user_page(); // Buscamos página libre

    tss_t nueva_tss = tss_create_user_task(pagina_libre); // Armamos TSS con la función del TP

    // Ahora recorremos el array de tareas para encontrar una posición libre y la ponemos ahí.
    int encontreLugar = 0;
    int index;

    for (int i = 0; i < MAX_TASKS; i++){
        if (encontreLugar == 1) continue; // Salgo si ya encontré lugar para poner la tarea
        sched_entry_t tarea = sched_tasks[i];

        if (tarea->state == "TASK_SLOT_FREE"){
            index = i;
            encontreLugar = 1;
        }
    }

    if (encontreLugar == 1){
        tss_set(nueva_tss, index); // Uso la función que hicimos en el TP para sincronizar la TSS
    }

    gdt_entry_t entrada_gdt = tss_gdt_entry_for_task(nueva_tss);

    // Ahora tengo que incluir la TSS en la GDT
    int indice_gdt;

    for (int i = 0; i < GDT_COUNT; i++){
        if (indice_gdt > 0) continue;
        if (gdt[i] == 0){
            indice_gdt = i;
        }
    }

    if (indice_gdt > 0){
        // Se que tiene que ser mayor a cero porque la primer entrada de la GDT está ocupada por el desciptor nulo.
        gdt[indice_gdt] = entrada_gdt;
    }

    // Acá ya inicializamos correctamente la tarea que va a producir ese recurso.
    // Ahora, seteemos los atributos que nos interesan.
    tarea->state = "TASK_RUNNABLE";
    tarea->requests = [];
    tarea->provides[0] = rec; // Le seteo ese producto.
}
```

Por el sistema que nos plantea el enunciado, un recurso existe si y solo si hay una *tarea que lo produce*.
Por ende, el mecanimo que pensé crea una nueva tarea con todo lo que esto implica.

Armar la *TSS* -> Armar la entrada en la *GDT* -> Insertar en la estructura.




## Ejercicio 4

Si se que hay a lo sumo 10 tareas y cada una produce máximo un producto, puedo asumir que existen *a lo sumo 10 productos*.

Podemos usar esta cota para definir un arreglo de 10 posiciones, donde la posición iésima es una arreglo de a lo sumo 10 tareas que representa las que están *esperando el recurso* iésimo.

Véase la posición en el array como un id de recurso.

De este modo, si hay 3 tareas (supongamos ID's 4,5,6) esperando el recurso cero y hay 2 esperando el recurso 2 (supongamos ID's 8 y 9), podríamos tener este estado en el array:

```C
[ [4,5,6], [], [8,9], [], [], ... ]
```
Análogamente, podemos armar un array para las que producen el recurso que está en esa posición.
Sería un array de arrays llamado productores, donde _productores[i]_ son los que producen el recurso de ID = i. 

# Implementación de las funciones que utilicé:

Voy a implementarlas con la lógica que plantee para expandir el Kernel, no con la que expliqué en este inciso.

```C
task_id_t hay_tarea_disponible_para_recurso(recurso_t recurso){
    for (int i = 0; i < MAX_TASKS; i++){

        sched_entry_t tarea = sched_tasks[i];

        if(tarea.producingFor >= 0){
            continue; // Si ya produce para alguien, no nos sirve.
        }

        for (j = 0, j < tarea.provides.length, j++){
            if (tarea.provides[j] == recurso){
                return i; // Devuelvo el índice de la tarea que produce y está libre 
            }
        }
    }

    return 0; // Devuelve cero si no existe tarea con ese recurso y disponible para trabajar.
}
```