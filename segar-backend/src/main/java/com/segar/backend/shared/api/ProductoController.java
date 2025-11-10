package com.segar.backend.shared.api;

import java.util.List;

import com.segar.backend.shared.domain.Producto;
import com.segar.backend.shared.service.ProductoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;




@RequestMapping("api/producto")
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

    @PostMapping("/create")
    public Producto createProducto(@RequestBody Producto producto) {
        productoService.saveProducto(producto);
        return producto;
    }
    
    @DeleteMapping("/{id}")
    public void deleteProducto(@PathVariable Long id) {
        productoService.deleteProducto(id);
    }
    
    @GetMapping("/{id}")
    public Producto getProductoById(@PathVariable Long id) {
        return productoService.getProductoById(id);
    }

    @PutMapping("/{id}")
    public Producto updateProducto(@PathVariable String id, @RequestBody Producto producto) {
        return productoService.updateProducto(Long.parseLong(id), producto);
    }

    @GetMapping("/empresa/{empresaId}/sin-tramites")
    public List<Producto> getProductosByEmpresaIdNotAssociatedWithTramites(@PathVariable Long empresaId) {
        return productoService.getProductosByEmpresaIdNotAssociatedWithTramites(empresaId);
    }

    @GetMapping("/con-registro-vigente")
    public List<Producto> getProductosConRegistrosSanitariosVigentes() {
        return productoService.getProductosConRegistrosSanitariosVigentes();
    }

    @GetMapping("/empresa/{empresaId}/con-registro-vigente")
    public List<Producto> getProductosConRegistrosSanitariosVigentesByEmpresaId(@PathVariable Long empresaId) {
        return productoService.getProductosConRegistrosSanitariosVigentesByEmpresaId(empresaId);
    }

}
