package TPI.TPI.service.impl;

import TPI.TPI.Commons.GenericServiceImpl;
import TPI.TPI.DTO.UpdatePasswordDTO;
import TPI.TPI.DTO.UserDTO;
import TPI.TPI.Entity.Administradores;
import TPI.TPI.Entity.Personas;
import TPI.TPI.Entity.Usuarios;
import TPI.TPI.dao.api.AdministradorDaoAPI;
import TPI.TPI.dao.api.UsuarioDaoAPI;
import TPI.TPI.service.api.UsuarioServiceAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl extends GenericServiceImpl<Usuarios,Long>  implements UsuarioServiceAPI {
   @Autowired
    private UsuarioDaoAPI usuarioDaoAPI;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    AdministradorDaoAPI administradorDaoAPI;
    @Override
    public CrudRepository<Usuarios, Long> getDao() {
        return usuarioDaoAPI;
    }

    @Override
    public Usuarios save(UserDTO userDTO) {

        Optional<Administradores> obj = Optional.ofNullable(administradorDaoAPI.findByRol(userDTO.getRol()));
        Administradores administradores = obj.isPresent()?obj.get():new Administradores(userDTO.getRol());


        Personas personas = new Personas();
        personas.setNombre(userDTO.getNombre());
        personas.setApellido(userDTO.getApellido());
        personas.setEstado(true);

        Usuarios usuarios = new Usuarios();
        usuarios.setPersona(personas);
        usuarios.setAdministrador(administradores);
        usuarios.setUsuario(userDTO.getUsuario());
        usuarios.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        return usuarioDaoAPI.save(usuarios);
    }
    @Transactional
    @Override
    public void setPassword(UpdatePasswordDTO updatePasswordDTO) {
        usuarioDaoAPI.updatePassword(passwordEncoder.encode(updatePasswordDTO.getPassword()),updatePasswordDTO.getId());
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional <Usuarios> obj = Optional.ofNullable(usuarioDaoAPI.findByUsuario(username));
        if (!obj.isPresent() || !obj.get().getPersona().getEstado()) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        Usuarios user = obj.get();

        return new org.springframework.security.core.userdetails.User(user.getUsuario(), user.getPassword(), mapRolesToAuthorities(user.getAdministrador()));

    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Administradores roles) {
        Collection<Administradores> cRoles = new ArrayList<>();
        cRoles.add(roles);

        return cRoles.stream().map(role -> new SimpleGrantedAuthority(role.getRol().toString())).collect(Collectors.toList());
    }

    public Boolean existeUsuario(){
        if (usuarioDaoAPI.cantidadUsuarios() > 0) return true;
        return false;

    }
}
