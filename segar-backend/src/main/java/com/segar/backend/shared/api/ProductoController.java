package com.segar.backend.shared.api;

import java.util.List;

import com.segar.backend.shared.domain.Producto;
import com.segar.backend.shared.service.ProductoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/producto")
@RestController
public class ProductoController {

    @Autowired
    private ProductoServiceImpl productoService;

    @GetMapping("/all")
    public List<Producto> getAllProductos() {
        System.out.println("Fetching all products");
        List<Producto> productos = productoService.getAllProductos();
        System.out.println("Number of products fetched: " + productos.size());
        for (Producto producto : productos) {
            System.out.println("Product: " + producto.getNombre());
        }
        return productos;
    }
}
