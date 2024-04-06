package curso.spring.exception;

public class UsuarioOuSenhaInvalidaException extends RuntimeException {

    public UsuarioOuSenhaInvalidaException() {
        super("Usuario ou senha invalidos");
    }
}
