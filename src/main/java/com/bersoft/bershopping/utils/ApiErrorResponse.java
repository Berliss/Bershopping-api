package com.bersoft.bershopping.utils;

import java.util.List;

public record ApiErrorResponse(String message, List<String> details, Integer status) {
}