package ponto.certo.dao;

import ponto.certo.Usuario;
import ponto.certo.db.DbConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de usuários com backend MySQL via JDBC.
 * Define URL/usuário/senha via propriedades do sistema:
 *   -Ddb.url=jdbc:mysql://<host>:<port>/<db>?serverTimezone=UTC
 *   -Ddb.user=<usuario>
 *   -Ddb.pass=<senha>
 * Caso não informadas, usa defaults: jdbc:mysql://localhost:3306/pontocerto, user=root, senha vazia.
 */
public class UsuarioDAO {
    private final String url;
    private final String user;
    private final String pass;

    public UsuarioDAO() {
        DbConfig cfg = DbConfig.load();
        this.url = cfg.getUrl();
        this.user = cfg.getUser();
        this.pass = cfg.getPass();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // Driver não encontrado; a aplicação continuará mas operações SQL falharão
            System.out.println("Driver MySQL não encontrado no classpath: " + e.getMessage());
        }
        // Garante usuário admin padrão
        try {
            createAdminIfMissing();
            // Opcional: resetar senha do admin ao iniciar, quando habilitado
            if ("true".equalsIgnoreCase(System.getProperty("db.resetAdmin"))) {
                resetAdminPassword();
            }
        } catch (Exception ignored) {}
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }

    /**
     * Autentica um usuário por login e senha (texto exato). Se usar hash, alinhar aqui.
     */
    public Usuario autenticar(String login, String senha) {
        String sql = "SELECT login, senha, nome, is_admin FROM usuarios WHERE login = ? AND senha = ? LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, senha);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String l = rs.getString("login");
                    String s = rs.getString("senha");
                    String n = rs.getString("nome");
                    boolean admin = rs.getBoolean("is_admin");
                    return new Usuario(l, s, n, admin);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao autenticar usuário: " + e.getMessage() +
                    " | url=" + url + " user=" + user + " pass_set=" + (pass != null && !pass.isEmpty()));
        }
        return null;
    }

    /**
     * Cadastra um novo usuário (login único).
     */
    public void cadastrarUsuario(Usuario usuario) {
        if (isLoginExistente(usuario.getLogin())) return;
        String sql = "INSERT INTO usuarios (login, senha, nome, is_admin) VALUES (?,?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.getLogin());
            ps.setString(2, usuario.getSenha());
            ps.setString(3, usuario.getNome());
            ps.setBoolean(4, usuario.isAdmin());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar usuário: " + e.getMessage() +
                    " | url=" + url + " user=" + user + " pass_set=" + (pass != null && !pass.isEmpty()));
        }
    }

    /**
     * Verifica se o login já existe.
     */
    public boolean isLoginExistente(String login) {
        String sql = "SELECT 1 FROM usuarios WHERE login = ? LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Erro ao verificar login existente: " + e.getMessage() +
                    " | url=" + url + " user=" + user + " pass_set=" + (pass != null && !pass.isEmpty()));
        }
        return false;
    }

    /**
     * Retorna todos os funcionários (não administradores).
     */
    public List<Usuario> getTodosFuncionarios() {
        List<Usuario> funcionarios = new ArrayList<>();
        String sql = "SELECT login, senha, nome, is_admin FROM usuarios WHERE is_admin = 0";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String l = rs.getString("login");
                String s = rs.getString("senha");
                String n = rs.getString("nome");
                boolean admin = rs.getBoolean("is_admin");
                funcionarios.add(new Usuario(l, s, n, admin));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao obter funcionários: " + e.getMessage());
        }
        return funcionarios;
    }

    /**
     * Garante admin padrão no banco.
     */
    public void createAdminIfMissing() {
        if (!isLoginExistente("admin")) {
            cadastrarUsuario(new Usuario("admin", "admin", "Administrador", true));
        }
    }

    /**
     * Atualiza/força a senha do admin para 'admin' quando necessário.
     * Habilite com -Ddb.resetAdmin=true para rodar uma vez.
     */
    private void resetAdminPassword() {
        String sql = "UPDATE usuarios SET senha = ?, nome = ?, is_admin = 1 WHERE login = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "admin");
            ps.setString(2, "Administrador");
            ps.setString(3, "admin");
            ps.executeUpdate();
            System.out.println("Senha do usuário admin foi resetada para 'admin'.");
        } catch (SQLException e) {
            System.out.println("Falha ao resetar senha do admin: " + e.getMessage());
        }
    }
}