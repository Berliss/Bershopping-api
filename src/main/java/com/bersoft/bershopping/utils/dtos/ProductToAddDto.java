package com.bersoft.bershopping.utils.dtos;

import javax.validation.constraints.NotNull;

public record ProductToAddDto( @NotNull Long id, @NotNull Double quantity) {}
