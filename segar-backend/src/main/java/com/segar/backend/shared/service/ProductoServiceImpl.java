package com.segar.backend.shared.service;

import java.util.List;

import com.segar.backend.shared.domain.Producto;
import com.segar.backend.shared.infrastructure.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class ProductoServiceImpl {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> getAllProductos() {
        List<Producto> productos = productoRepository.findAll();
        return productos;
    }

    public Producto saveProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    public void deleteProducto(Long id) {
        productoRepository.deleteById(id);
    }

    public Producto getProductoById(Long id) {
        return productoRepository.findById(id).orElse(null);
    }

    public Producto updateProducto(Long id, Producto updatedProducto) {
        return productoRepository.findById(id).map(producto -> {
            producto.setNombre(updatedProducto.getNombre());
            producto.setDescripcion(updatedProducto.getDescripcion());
            producto.setEspecificaciones(updatedProducto.getEspecificaciones());
            producto.setReferencia(updatedProducto.getReferencia());
            producto.setFabricante(updatedProducto.getFabricante());
            producto.setEmpresaId(updatedProducto.getEmpresaId());
            return productoRepository.save(producto);
        }).orElse(null);
    }

    /**
     * Busca productos por texto
     * @param query Texto de búsqueda
     * @return Lista de productos que coinciden con el texto
     */
    public List<Producto> buscarProductos(String query) {
        // Si query es null, vacío o solo espacios, retornar todos los productos
        if (query == null || query.trim().isEmpty() || query.trim().length() < 1) {
            System.out.println("Query vacío, retornando todos los productos");
            return getAllProductos();
        }
        System.out.println("Buscando productos con query: '" + query.trim() + "'");
        return productoRepository.buscarProductos(query.trim());
    }

    /**
     * Busca productos por empresa
     * @param empresaId ID de la empresa
     * @return Lista de productos de la empresa
     */
    public List<Producto> getProductosByEmpresaId(Long empresaId) {
        return productoRepository.findByEmpresaId(empresaId);
    }

    /**
     * Busca productos por texto y empresa
     * @param query Texto de búsqueda
     * @param empresaId ID de la empresa
     * @return Lista de productos que coinciden con el texto y pertenecen a la empresa
     */
    public List<Producto> buscarProductosPorEmpresa(String query, Long empresaId) {
        if (query == null || query.trim().isEmpty()) {
            return getProductosByEmpresaId(empresaId);
        }
        return productoRepository.buscarProductosPorEmpresa(query.trim(), empresaId);
    }

    public List<Producto> getProductosByEmpresaIdNotAssociatedWithTramites(Long empresaId) {
        return productoRepository.findByEmpresaIdAndNotAssociatedWithTramites(empresaId);

    }
}
