package com.segar.backend.shared.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.segar.backend.models.Producto;
import com.segar.backend.repositories.ProductoRepository;
import com.segar.backend.services.interfaces.ProductoService;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public List<Producto> getAllProductos() {
        List<Producto> productos = productoRepository.findAll();
        return productos;
    }
}
