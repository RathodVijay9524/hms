package com.vijay.User_Master.config.security;

import org.springframework.security.access.prepost.PreAuthorize;
import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('INVENTORY_MANAGER') or hasRole('ADMIN')")
public @interface InventoryManagerAccess {
}
