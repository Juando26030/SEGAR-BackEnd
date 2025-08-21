package com.segar.backend.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;

import com.segar.backend.models.Producto;
import com.segar.backend.repositories.ProductoRepository;

import jakarta.transaction.Transactional;

@Controller
@Transactional
@Profile("default")
public class DatabaseInit implements ApplicationRunner{

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        productoRepository.save(new Producto("Producto 1", "Descripción del producto 1", "Especificaciones del producto 1", "Referencia del producto 1", "Fabricante del producto 1"));
    }
    
}
