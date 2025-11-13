package ponto.certo;

import ponto.certo.dao.RegistroPontoDAO;

import java.util.List;

public class PontoController {
    private static PontoController instance;
    private final RegistroPontoDAO registroPontoDAO;

    private PontoController() {
        registroPontoDAO = new RegistroPontoDAO();
    }

    public static PontoController getInstance() {
        if (instance == null) {
            instance = new PontoController();
        }
        return instance;
    }

    public void registrarEntrada(String usuarioLogin) {
        RegistroPonto registro = new RegistroPonto(usuarioLogin, "ENTRADA");
        registroPontoDAO.inserir(registro);
    }

    public void registrarSaida(String usuarioLogin) {
        RegistroPonto registro = new RegistroPonto(usuarioLogin, "SAIDA");
        registroPontoDAO.inserir(registro);
    }

    public List<RegistroPonto> getRegistrosUsuario(String usuarioLogin) {
        return registroPontoDAO.listarPorUsuario(usuarioLogin);
    }

    public List<RegistroPonto> getTodosRegistros() {
        return registroPontoDAO.listarTodos();
    }

    public RegistroPonto getUltimoRegistro(String usuarioLogin) {
        return registroPontoDAO.getUltimoRegistro(usuarioLogin);
    }

    /**
     * Retorna registros de um usuário específico em um mês/ano
     */
    public List<RegistroPonto> getRegistrosUsuarioPorMes(String usuarioLogin, int mes, int ano) {
        return registroPontoDAO.listarPorUsuarioMes(usuarioLogin, mes, ano);
    }

    /**
     * Retorna todos os registros de um mês/ano específico
     */
    public List<RegistroPonto> getRegistrosPorMes(int mes, int ano) {
        return registroPontoDAO.listarPorMes(mes, ano);
    }
}