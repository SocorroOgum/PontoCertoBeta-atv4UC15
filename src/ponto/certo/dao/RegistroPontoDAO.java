package ponto.certo.dao;

import ponto.certo.RegistroPonto;
import ponto.certo.db.DbConfig;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para registros de ponto, persistidos em MySQL (tabela registros_ponto).
 * Usa configurações de DbConfig (db.url, db.user, db.pass).
 */
public class RegistroPontoDAO {
    private final String url;
    private final String user;
    private final String pass;

    public RegistroPontoDAO() {
        DbConfig cfg = DbConfig.load();
        this.url = cfg.getUrl();
        this.user = cfg.getUser();
        this.pass = cfg.getPass();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver MySQL não encontrado: " + e.getMessage());
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }

    /** Inserir um registro de ponto. */
    public void inserir(RegistroPonto registro) {
        String sql = "INSERT INTO registros_ponto (usuario_login, data_hora, tipo) VALUES (?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, registro.getUsuarioLogin());
            ps.setTimestamp(2, Timestamp.valueOf(registro.getDataHora()));
            ps.setString(3, registro.getTipo());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao inserir registro de ponto: " + e.getMessage());
        }
    }

    /** Listar todos os registros. */
    public List<RegistroPonto> listarTodos() {
        String sql = "SELECT usuario_login, data_hora, tipo FROM registros_ponto ORDER BY data_hora";
        List<RegistroPonto> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String login = rs.getString("usuario_login");
                LocalDateTime dt = rs.getTimestamp("data_hora").toLocalDateTime();
                String tipo = rs.getString("tipo");
                lista.add(new RegistroPonto(login, tipo, dt));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar registros: " + e.getMessage());
        }
        return lista;
    }

    /** Listar registros por usuário. */
    public List<RegistroPonto> listarPorUsuario(String usuarioLogin) {
        String sql = "SELECT usuario_login, data_hora, tipo FROM registros_ponto WHERE usuario_login = ? ORDER BY data_hora";
        List<RegistroPonto> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuarioLogin);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String login = rs.getString("usuario_login");
                    LocalDateTime dt = rs.getTimestamp("data_hora").toLocalDateTime();
                    String tipo = rs.getString("tipo");
                    lista.add(new RegistroPonto(login, tipo, dt));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar registros por usuário: " + e.getMessage());
        }
        return lista;
    }

    /** Listar registros por mês/ano. */
    public List<RegistroPonto> listarPorMes(int mes, int ano) {
        String sql = "SELECT usuario_login, data_hora, tipo FROM registros_ponto WHERE MONTH(data_hora) = ? AND YEAR(data_hora) = ? ORDER BY usuario_login, data_hora";
        List<RegistroPonto> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mes);
            ps.setInt(2, ano);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String login = rs.getString("usuario_login");
                    LocalDateTime dt = rs.getTimestamp("data_hora").toLocalDateTime();
                    String tipo = rs.getString("tipo");
                    lista.add(new RegistroPonto(login, tipo, dt));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar registros por mês: " + e.getMessage());
        }
        return lista;
    }

    /** Listar registros por usuário e mês/ano. */
    public List<RegistroPonto> listarPorUsuarioMes(String usuarioLogin, int mes, int ano) {
        String sql = "SELECT usuario_login, data_hora, tipo FROM registros_ponto WHERE usuario_login = ? AND MONTH(data_hora) = ? AND YEAR(data_hora) = ? ORDER BY data_hora";
        List<RegistroPonto> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuarioLogin);
            ps.setInt(2, mes);
            ps.setInt(3, ano);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String login = rs.getString("usuario_login");
                    LocalDateTime dt = rs.getTimestamp("data_hora").toLocalDateTime();
                    String tipo = rs.getString("tipo");
                    lista.add(new RegistroPonto(login, tipo, dt));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar registros por usuário/mês: " + e.getMessage());
        }
        return lista;
    }

    /** Último registro de um usuário. */
    public RegistroPonto getUltimoRegistro(String usuarioLogin) {
        String sql = "SELECT usuario_login, data_hora, tipo FROM registros_ponto WHERE usuario_login = ? ORDER BY data_hora DESC LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuarioLogin);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String login = rs.getString("usuario_login");
                    LocalDateTime dt = rs.getTimestamp("data_hora").toLocalDateTime();
                    String tipo = rs.getString("tipo");
                    return new RegistroPonto(login, tipo, dt);
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao obter último registro: " + e.getMessage());
        }
        return null;
    }
}