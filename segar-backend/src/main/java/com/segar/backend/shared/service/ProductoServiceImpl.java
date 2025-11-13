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

    public List<Producto> getProductosByEmpresaIdNotAssociatedWithTramites(Long empresaId) {
        return productoRepository.findByEmpresaIdAndNotAssociatedWithTramites(empresaId);

    }

    public List<Producto> getProductosConRegistrosSanitariosVigentes() {
        return productoRepository.findProductosConRegistrosSanitariosVigentes();
    }

    public List<Producto> getProductosConRegistrosSanitariosVigentesByEmpresaId(Long empresaId) {
        return productoRepository.findProductosConRegistrosSanitariosVigentesByEmpresaId(empresaId);
    }

    public List<Producto> getProductosByEmpresaId(Long empresaId) {
        return productoRepository.findByEmpresaId(empresaId);
    }

}
