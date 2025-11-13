package ponto.certo;

import ponto.certo.dao.UsuarioDAO;
import java.util.List;

public class UsuarioController {
    private static UsuarioController instance;
    private final UsuarioDAO usuarioDAO;
    private Usuario usuarioLogado;
    
    private UsuarioController() {
        usuarioDAO = new UsuarioDAO();
        // Garante que o admin padrão exista na base
        usuarioDAO.createAdminIfMissing();
    }
    
    public static UsuarioController getInstance() {
        if (instance == null) {
            instance = new UsuarioController();
        }
        return instance;
    }
    
    public boolean autenticar(String login, String senha) {
        Usuario usuario = usuarioDAO.autenticar(login, senha);
        if (usuario != null) {
            usuarioLogado = usuario;
            return true;
        }
        return false;
    }
    
    public void cadastrarUsuario(String login, String senha, String nome, boolean isAdmin) {
        if (!isLoginExistente(login)) {
            usuarioDAO.cadastrarUsuario(new Usuario(login, senha, nome, isAdmin));
        }
    }
    
    public boolean isUsuarioLogadoAdmin() {
        return usuarioLogado != null && usuarioLogado.isAdmin();
    }
    
    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }
    
    public void logout() {
        usuarioLogado = null;
    }
    
    public boolean isLoginExistente(String login) {
        return usuarioDAO.isLoginExistente(login);
    }
    
    /**
     * Retorna lista com todos os funcionários cadastrados
     */
    public java.util.List<Usuario> getTodosFuncionarios() {
        List<Usuario> lista = usuarioDAO.getTodosFuncionarios();
        return lista;
    }
}