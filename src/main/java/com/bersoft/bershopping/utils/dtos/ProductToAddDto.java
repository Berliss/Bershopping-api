package com.bersoft.bershopping.utils.dtos;

import javax.validation.constraints.NotNull;

public record ProductToAddDto( @NotNull(message = "id must be a positive numeric value") Long id, @NotNull(message = "quantity must be a positive numeric value") Double quantity) {}
