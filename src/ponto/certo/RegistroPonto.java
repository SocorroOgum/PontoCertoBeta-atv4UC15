package ponto.certo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RegistroPonto {
    private String usuarioLogin;
    private LocalDateTime dataHora;
    private String tipo; // "ENTRADA" ou "SAIDA"
    
    public RegistroPonto(String usuarioLogin, String tipo) {
        this.usuarioLogin = usuarioLogin;
        this.tipo = tipo;
        this.dataHora = LocalDateTime.now();
    }

    public RegistroPonto(String usuarioLogin, String tipo, LocalDateTime dataHora) {
        this.usuarioLogin = usuarioLogin;
        this.tipo = tipo;
        this.dataHora = dataHora;
    }
    
    public String getUsuarioLogin() {
        return usuarioLogin;
    }
    
    public LocalDateTime getDataHora() {
        return dataHora;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public String getDataHoraFormatada() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return dataHora.format(formatter);
    }
}