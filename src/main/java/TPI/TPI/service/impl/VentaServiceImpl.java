package TPI.TPI.service.impl;

import TPI.TPI.Commons.GenericServiceImpl;
import TPI.TPI.Entity.Ventas;
import TPI.TPI.dao.api.VentaDaoAPI;
import TPI.TPI.service.api.VentaServiceAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class VentaServiceImpl extends GenericServiceImpl<Ventas, Integer> implements VentaServiceAPI {
    @Autowired
    VentaDaoAPI ventaDaoAPI;


    @Override
    public CrudRepository<Ventas, Integer> getDao() {
        return ventaDaoAPI;
    }

    @Override
    public Integer obtenerUltimoID() {
        Optional<Integer> id = Optional.ofNullable(ventaDaoAPI.ultimoID());
        return id.isPresent() ? id.get(): 0;
    }

    @Override
    @Transactional
    public void modificarEstadoPorId(Boolean estado, Integer id) {
        ventaDaoAPI.modificarEstadoPorId(estado,id);
    }


}
