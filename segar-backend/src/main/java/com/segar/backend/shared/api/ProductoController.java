package com.segar.backend.shared.api;

import java.util.List;

import com.segar.backend.security.service.AuthenticatedUserService;
import com.segar.backend.shared.domain.Producto;
import com.segar.backend.shared.service.ProductoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.server.ResponseStatusException;




@RequestMapping("api/producto")
@RestController
public class ProductoController {

    @Autowired
    private ProductoServiceImpl productoService;

    @Autowired
    private AuthenticatedUserService authenticatedUserService;

    /**
     * Valida que el empresaId del request coincida con el del usuario autenticado
     */
    private void validateTenantAccess(Long empresaIdRequest) {
        Long empresaIdUsuario = authenticatedUserService.getCurrentUserEmpresaId();
        if (!empresaIdRequest.equals(empresaIdUsuario)) {
            throw new AccessDeniedException("No tienes permiso para acceder a recursos de otra empresa");
        }
    }

    @GetMapping("/all")
    public List<Producto> getAllProductos() {
        // Filtrar automáticamente por empresa del usuario autenticado
        Long empresaId = authenticatedUserService.getCurrentUserEmpresaId();
        System.out.println("Fetching products for empresaId: " + empresaId);
        List<Producto> productos = productoService.getProductosByEmpresaId(empresaId);
        System.out.println("Number of products fetched: " + productos.size());
        for (Producto producto : productos) {
            System.out.println("Product: " + producto.getNombre());
        }
        return productos;
    }

    @PostMapping("/create")
    public Producto createProducto(@RequestBody Producto producto) {
        // Asignar automáticamente la empresa del usuario autenticado
        Long empresaId = authenticatedUserService.getCurrentUserEmpresaId();
        producto.setEmpresaId(empresaId);
        productoService.saveProducto(producto);
        return producto;
    }
    
    @DeleteMapping("/{id}")
    public void deleteProducto(@PathVariable Long id) {
        // Validar que el producto pertenece a la empresa del usuario
        Producto producto = productoService.getProductoById(id);
        if (producto != null) {
            validateTenantAccess(producto.getEmpresaId());
        }
        productoService.deleteProducto(id);
    }
    
    @GetMapping("/{id}")
    public Producto getProductoById(@PathVariable Long id) {
        Producto producto = productoService.getProductoById(id);
        if (producto != null) {
            validateTenantAccess(producto.getEmpresaId());
        }
        return producto;
    }

    @PutMapping("/{id}")
    public Producto updateProducto(@PathVariable String id, @RequestBody Producto producto) {
        // Validar que el producto pertenece a la empresa del usuario
        Producto existente = productoService.getProductoById(Long.parseLong(id));
        if (existente != null) {
            validateTenantAccess(existente.getEmpresaId());
        }
        // Mantener el empresaId original
        producto.setEmpresaId(existente.getEmpresaId());
        return productoService.updateProducto(Long.parseLong(id), producto);
    }

    @GetMapping("/empresa/{empresaId}/sin-tramites")
    public List<Producto> getProductosByEmpresaIdNotAssociatedWithTramites(@PathVariable Long empresaId) {
        // Validar acceso cross-tenant
        validateTenantAccess(empresaId);
        return productoService.getProductosByEmpresaIdNotAssociatedWithTramites(empresaId);
    }
}
