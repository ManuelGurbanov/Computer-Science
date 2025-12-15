;########### SECCION DE DATOS
section .data
; extern free
;########### SECCION DE TEXTO (PROGRAMA)
section .text

; Completar las definiciones (serán revisadas por ABI enforcer):
ITEM_KIND_OFFSET EQU 0
ITEM_WEIGHT_OFFSET EQU 4
ITEM_SIZE EQU 8

BACKPACK_ITEMS_OFFSET EQU 0
BACKPACK_MAX_WEIGHT_OFFSET EQU 8
BACKPACK_ITEM_COUNT_OFFSET EQU 12
BACKPACK_SIZE EQU 16

DESTINATION_NAME_OFFSET EQU 0
DESTINATION_REQUIREMENTS_OFFSET EQU 32
DESTINATION_REQUIREMENTS_SIZE_OFFSET EQU 40
DESTINATION_SIZE EQU 48

EVENT_NEXT_OFFSET EQU 0
EVENT_DESTINATION_OFFSET EQU 8
EVENT_SIZE EQU 16

ITINERARY_FIRST_OFFSET EQU 0
ITINERARY_SIZE EQU 8

NULL EQU 0

extern backpackContainsItem
extern free

; void filterPossibleDestinations(itinerary_t *itinerary, backpack_t *backpack)
global filterPossibleDestinations

filterPossibleDestinations:
    push rbp
    mov rbp, rsp
    push r12
    push r13
    push r14
    push r15
    push rbx
    sub rsp, 8

    ; me guardo los parametros
    mov r12, rsi ; backpack_t backpack
    mov r13, rdi ; rbx = itinerary_t itinerary
    mov r15, 0

    mov rbx, [r13] ; rbx = itinerary_first
    cmp rbx, NULL 
    je .fin ; rbx = itinerary_first
    
    jmp .ciclo

    .siguiente:
        mov r15, rbx
        mov rbx, [rbx]
    .ciclo:
        test rbx, rbx
        je .fin
        
        ; paso parametros a cumple requisitos
        mov rdi, r12
        mov rsi, [rbx + EVENT_DESTINATION_OFFSET] 
        
        call meetsRequirements
        ; veo lo que me devuelve
        cmp al, 0
        jne .siguiente
        cmp r15, NULL   

        je .next
        mov  rax, [rbx]
        mov  [r15], rax
        
        .next:
        cmp [r13], rbx
        jne .sacarEvento

        mov rax, [rbx]
        mov [r13], rax
    .sacarEvento:
        mov r14, [rbx]
        mov rdi, rbx
        call freeEvento
        
        mov rbx, r14
        jmp .ciclo
    .fin:
        add rsp, 8
        pop rbx
        pop r15
        pop r14
        pop r13
        pop r12
        pop rbp
        ret

global meetsRequirements
meetsRequirements:
    push rbp
    mov rbp, rsp
    push r12
    push r13
    push r14
    sub rsp, 8

    mov rdx, [rsi + DESTINATION_REQUIREMENTS_OFFSET]
    mov eax, [rsi + DESTINATION_REQUIREMENTS_SIZE_OFFSET]
    cmp eax, NULL
    je .finTrue

    mov r13, rdi
    mov r14, rdx
    lea r12, [rdx + rax * 4]
.ciclo:
    mov esi, dword [r14]
    mov rdi, r13
    call backpackContiene

    cmp al, NULL 
    je .fin
    
    add r14, 4
    cmp r12, r14
    jne .ciclo
.fin:
    add rsp, 8
    pop r14
    pop r13
    pop r12
    pop rbp
    ret
.finTrue:
    mov eax, 1
    add rsp, 8
    pop r14
    pop r13
    pop r12
    pop rbp
    ret


global freeEvento
freeEvento: ; rdi event_t *event
    push rbp
    mov rbp, rsp
    push r12
    push r13
    
    mov r12, rdi ; r12 puntero a Evento
    
    mov r8, [rdi + EVENT_DESTINATION_OFFSET]
    mov rdi, [r8 + DESTINATION_REQUIREMENTS_OFFSET]
    call free
    mov rdi, [r12 + EVENT_DESTINATION_OFFSET]
    call free

    mov rdi, r12
    call free

    pop r13
    pop r12
    pop rbp
    ret

global backpackContiene
backpackContiene:
    push rbp
    mov rbp, rsp
    push r12
    push r13
    mov edx, [rdi + BACKPACK_ITEM_COUNT_OFFSET]
    cmp edx, NULL
    je .finFalse
    mov rax, [rdi]
    mov edx, edx
    lea rdx, [rax + rdx * 8]
.ciclo:
    cmp [rax], esi
    je .finTrue
    add rax, 8
    cmp rax, rdx
    jne .ciclo
.finFalse:
    mov eax, 0
    pop r13
    pop r12
    pop rbp
    ret
.finTrue:
    mov eax, 1
    pop r13
    pop r12
    pop rbp
    ret