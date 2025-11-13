package ponto.certo;

public class Usuario {
    private String login;
    private String senha;
    private String nome;
    private boolean isAdmin;
    
    public Usuario(String login, String senha, String nome, boolean isAdmin) {
        this.login = login;
        this.senha = senha;
        this.nome = nome;
        this.isAdmin = isAdmin;
    }
    
    public String getLogin() {
        return login;
    }
    
    public String getSenha() {
        return senha;
    }
    
    public String getNome() {
        return nome;
    }
    
    public boolean isAdmin() {
        return isAdmin;
    }
    
    public void setSenha(String senha) {
        this.senha = senha;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
}