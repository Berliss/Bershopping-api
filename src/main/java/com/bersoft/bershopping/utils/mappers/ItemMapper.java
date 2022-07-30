package com.bersoft.bershopping.utils.mappers;

import com.bersoft.bershopping.utils.dtos.ItemDto;
import com.bersoft.bershopping.persistence.entities.checkout.AbstractItem;

import java.util.ArrayList;
import java.util.List;

public class ItemMapper {

    public List<ItemDto> mapItemsToItemsDto(List<? extends AbstractItem> itemList) {
        List<ItemDto> itemDtoList = new ArrayList<>();
        itemList.forEach(item ->
                itemDtoList.add(new ItemDto(
                        item.getProduct().getId(),
                        item.getProduct().getDescription(),
                        item.getProduct().getStock(),
                        item.getPrice(),
                        item.getQuantity(),
                        item.calcImport()
                ))
        );
        return itemDtoList;
    }

}
