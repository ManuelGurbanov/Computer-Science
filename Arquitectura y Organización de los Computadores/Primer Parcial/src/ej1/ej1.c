#include "../ejs.h"
#include "../ejs_aux.h"

bool canItemFitInBackpack(backpack_t *backpack, item_t *item) {
  int pesoTotal = pesoItems(backpack->items,
                            backpack->item_count);
  
  uint8_t pesoItem = item->weight;

  bool res = true;

  uint8_t pesoMax = backpack->max_weight;

  if (pesoTotal + pesoItem > pesoMax)
  {
    res = false;
  }
  
  return res;
}


int pesoItems(item_t *items,uint32_t item_count){
  int res = 0;
  for (uint32_t i = 0; i < item_count; i++)
  {
    res += items[i].weight;
  }

  return res;
}