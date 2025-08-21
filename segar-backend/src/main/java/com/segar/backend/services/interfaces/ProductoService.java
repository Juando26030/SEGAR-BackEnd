package com.segar.backend.services.interfaces;

import java.util.List;

import com.segar.backend.models.Producto;

public interface ProductoService {
    public List<Producto> getAllProductos();
}