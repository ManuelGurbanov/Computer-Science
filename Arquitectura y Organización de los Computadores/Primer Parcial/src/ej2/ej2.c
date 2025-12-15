#include "../ejs.h"
#include "../ejs_aux.h"

bool meetsRequirements(backpack_t *backpack, destination_t *dest){
    item_t *itemsBackpack = backpack->items;

    bool res = true;

    item_kind_t *destReqs = dest->requirements;

    for (int i = 0; i < dest->requirements_size; i++)
    {
        item_kind_t tipo = destReqs[i];
        if (!backpackContainsItem(backpack, tipo))
        {
            res = false; 
            return res;
        }        
    }

    return res;
}

void free_evento(event_t *event){
    destination_t *destination = event->destination;
    free(destination->requirements);
    free(destination);

    free(event);
}

void filterPossibleDestinations(itinerary_t *itinerary, backpack_t *backpack) {
    event_t *evento = itinerary->first;
    event_t *anterior = NULL;

    while (evento != NULL)
    {
        bool sacarEvento = false; 
        if (!meetsRequirements(backpack, evento->destination))
        {
            sacarEvento = true;
        }

        if (sacarEvento)
        {
          if (anterior)
		  	anterior->next = evento->next;
		  if (evento == itinerary->first)
		  	itinerary->first = evento->next;
        }

        if (!sacarEvento)
        {
            anterior = evento;
        }
        
        event_t *eventoViejo = evento; 
        evento = evento->next;
        
        if (sacarEvento)
        {
            free_evento(eventoViejo);
        }
    }
}

//Cuentan con la siguiente función auxiliar ya implementada que pueden utilizar:

// bool backpackContainsItem(backpack_t *backpack, item_kind_t kind) {
//   for (uint32_t i = 0; i < backpack->item_count; ++i) {
//     if (backpack->items[i].kind == kind) {
//       return true;
//     }
//   }

//   return false;
// }