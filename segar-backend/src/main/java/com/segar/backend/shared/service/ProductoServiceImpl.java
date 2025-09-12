package com.segar.backend.shared.service;

import java.util.List;

import com.segar.backend.shared.domain.Producto;
import com.segar.backend.shared.infrastructure.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class ProductoServiceImpl{

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> getAllProductos() {
        List<Producto> productos = productoRepository.findAll();
        return productos;
    }
}
