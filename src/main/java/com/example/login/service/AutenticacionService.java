package com.example.login.service;

import com.example.login.dto.AutenticacionResponseDTO;
import com.example.login.dto.LoginRequestDTO;
import com.example.login.dto.RegistroRequestDTO;
import com.example.login.exception.CorreoDuplicadoException;
import com.example.login.exception.CredencialesInvalidasException;
import com.example.login.exception.RolNoCoincidenteException;
import com.example.login.model.Usuario;
import com.example.login.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 Servicio que concentra la lógica de negocio de Registro e Inicio de Sesión:
 Reemplaza el almacenamiento temporal en memoria por persistencia real en PostgreSQL (Supabase)
 */
@Service
public class AutenticacionService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;  //Encriptador BCrypt
    private final JwtService jwtService;  //Generador de tokens JWT

    //Inyección de dependencias por constructor (buena práctica recomendada por Spring)
    public AutenticacionService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     Registra un nuevo usuario en la base de datos:
     Valida que el correo no exista, encripta la contraseña con BCrypt y devuelve el token JWT
     */
    @Transactional
    public AutenticacionResponseDTO registrar(RegistroRequestDTO request) {

        //Normaliza los textos: quita espacios sobrantes y pasa el correo a minúsculas
        String nombreLimpio = request.getNombre().trim();
        String correoLimpio = request.getCorreo().trim().toLowerCase();

        //1. Verifica que no exista otra cuenta con el mismo correo (sin importar mayúsculas)
        if (usuarioRepository.existsByCorreoIgnoreCase(correoLimpio)) {
            throw new CorreoDuplicadoException(
                    "El correo " + correoLimpio + " ya se encuentra registrado en el sistema"
            );
        }

        //2. Encripta la contraseña con BCrypt antes de guardarla (nunca se guarda en texto plano)
        String passwordEncriptada = passwordEncoder.encode(request.getPassword());

        //3. Crea la entidad Usuario y la persiste en PostgreSQL
        Usuario nuevoUsuario = new Usuario(
                nombreLimpio,
                correoLimpio,
                passwordEncriptada,
                request.getRol()
        );
        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        //4. Genera el token JWT para el usuario recién creado
        String token = jwtService.generarToken(usuarioGuardado);

        //5. Retorna el DTO de respuesta con el token y el idUsuario generado por la BD
        return new AutenticacionResponseDTO(
                token,
                usuarioGuardado.getId(),
                usuarioGuardado.getNombre(),
                usuarioGuardado.getCorreo(),
                usuarioGuardado.getRol(),
                "Usuario registrado exitosamente"
        );
    }

    /**
     Valida las credenciales de inicio de sesión y aplica el blindaje de rol:
     Impide que un usuario registrado como RELATOR ingrese como COORDINADOR (y viceversa)
     */
    @Transactional(readOnly = true) // Optimiza el rendimiento al indicar que solo se realizan consultas de lectura
    public AutenticacionResponseDTO login(LoginRequestDTO request) {

        //Normaliza el correo ingresado en LoginScreen
        String correoLimpio = request.getCorreo().trim().toLowerCase();

        //1. Busca el usuario en BD; si no existe, lanza 401 Unauthorized
        Usuario usuario = usuarioRepository.findByCorreoIgnoreCase(correoLimpio)
                .orElseThrow(() -> new CredencialesInvalidasException(
                        "Correo o contraseña incorrectos"
                ));

        //2. Verifica que la cuenta no esté desactivada
        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new CredencialesInvalidasException(
                    "Esta cuenta se encuentra desactivada. Contacte a un coordinador"
            );
        }

        //3. Compara la contraseña ingresada contra el hash BCrypt guardado en BD
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new CredencialesInvalidasException(
                    "Correo o contraseña incorrectos"
            );
        }

        //4. Regla de negocio: verifica que el rol seleccionado coincida con el rol registrado en BD
        if (usuario.getRol() != request.getRol()) {
            throw new RolNoCoincidenteException(
                    "Este correo está registrado como " + usuario.getRol()
                            + ". No puedes ingresar como " + request.getRol()
            );
        }

        //5. Genera el token JWT de sesión
        String token = jwtService.generarToken(usuario);

        //6. Devuelve el DTO con token e idUsuario para que Android navegue a la pantalla correspondiente
        return new AutenticacionResponseDTO(
                token,
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getRol(),
                "Inicio de sesión exitoso"
        );
    }
}
