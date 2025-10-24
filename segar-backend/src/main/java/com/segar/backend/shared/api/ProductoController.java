package com.segar.backend.shared.api;

import java.util.List;

import com.segar.backend.shared.domain.Producto;
import com.segar.backend.shared.service.ProductoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;




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

    /**
     * Busca productos por texto
     * @param query Texto de búsqueda (nombre, descripción, referencia o fabricante)
     * @return Lista de productos que coinciden con el texto
     */
    @GetMapping("/buscar")
    public List<Producto> buscarProductos(@RequestParam(required = false) String query) {
        System.out.println("Buscando productos con query: " + query);
        List<Producto> productos = productoService.buscarProductos(query);
        System.out.println("Número de productos encontrados: " + productos.size());
        return productos;
    }

    /**
     * Busca productos por empresa
     * @param empresaId ID de la empresa
     * @return Lista de productos de la empresa
     */
    @GetMapping("/empresa/{empresaId}")
    public List<Producto> getProductosByEmpresa(@PathVariable Long empresaId) {
        System.out.println("Buscando productos de la empresa: " + empresaId);
        return productoService.getProductosByEmpresaId(empresaId);
    }

    /**
     * Busca productos por texto y empresa
     * @param query Texto de búsqueda
     * @param empresaId ID de la empresa
     * @return Lista de productos que coinciden
     */
    @GetMapping("/buscar/empresa/{empresaId}")
    public List<Producto> buscarProductosPorEmpresa(
            @RequestParam(required = false) String query,
            @PathVariable Long empresaId) {
        System.out.println("Buscando productos con query: " + query + " para empresa: " + empresaId);
        return productoService.buscarProductosPorEmpresa(query, empresaId);
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
}
