;########### SECCION DE DATOS
section .data

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

; bool canItemFitInBackpack(backpack_t *backpack, item_t *item)
global canItemFitInBackpack
canItemFitInBackpack:
    push rbp
    mov rbp, rsp
    push r12
    push r13
    push r14
    push r15
    push rbx
    sub rsp, 8

    mov r12, rsi ; r12 item_t* item
    mov r13, rdi ; r13 backpack_t* backpack

    xor r14, r14

    mov esi, Dword [rdi + BACKPACK_ITEM_COUNT_OFFSET]
    mov rdi, [rdi]
    
    call pesoItems
    mov edx, eax ; r14d = pesoTotal
    movzx r14, byte [r12 + ITEM_WEIGHT_OFFSET]
    add eax, r14d ; r14d = pesoTotal + pesoItem
    movzx r14d, byte [r13 + BACKPACK_MAX_WEIGHT_OFFSET]
    cmp eax, r14d ; pesoTotal + pesoItem > pesoMax
    jg .finFalse
    mov al, 1 ; true
    jmp .fin
.finFalse:
    mov al, 0 
.fin:
    add rsp, 8
    pop rbx
    pop r15
    pop r14
    pop r13
    pop r12
    pop rbp
    ret

global pesoItems
pesoItems:
 ;   rdi *items
 ;   esi item_count
    push rbp
    mov rbp, rsp
    push r12
    push r13

    test esi, esi
    je .finNull
    
    mov rax, rdi
    mov esi, esi
    lea rsi, [rdi + rsi * ITEM_SIZE]
    mov edx, 0
.ciclo:
    movzx ecx, byte [rax + ITEM_WEIGHT_OFFSET]
    add edx, ecx
    add rax, 8
    cmp rax, rsi
    jne .ciclo
.fin:
    mov eax, edx
    pop r13
    pop r12
    pop rbp
    ret
.finNull:
    mov     edx, 0
    jmp     .fin