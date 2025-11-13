package ponto.certo.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Carrega configurações de banco de dados de:
 * 1) Propriedades da JVM (-Ddb.url/db.user/db.pass)
 * 2) Variáveis de ambiente (DB_URL/DB_USER/DB_PASS)
 * 3) Arquivo db.properties no diretório do projeto
 *    (chaves: db.url, db.user, db.pass)
 */
public class DbConfig {
    private String url;
    private String user;
    private String pass;

    private DbConfig(String url, String user, String pass) {
        this.url = url;
        this.user = user;
        this.pass = pass;
    }

    public static DbConfig load() {
        // Defaults
        String url = "jdbc:mysql://localhost:3306/pontocerto?serverTimezone=UTC";
        String user = "root";
        String pass = "";

        // JVM props
        url = System.getProperty("db.url", url);
        user = System.getProperty("db.user", user);
        pass = System.getProperty("db.pass", pass);

        // Env vars se props não vierem
        if (url == null || url.isEmpty()) url = System.getenv("DB_URL");
        if (user == null || user.isEmpty()) user = System.getenv("DB_USER");
        if (pass == null) pass = System.getenv("DB_PASS");

        // Arquivo db.properties no projeto
        try {
            Path propsPath = Paths.get(System.getProperty("user.dir", "."), "db.properties");
            if (Files.exists(propsPath)) {
                Properties p = new Properties();
                try (FileInputStream fis = new FileInputStream(propsPath.toFile())) {
                    p.load(fis);
                    url = p.getProperty("db.url", url);
                    user = p.getProperty("db.user", user);
                    String filePass = p.getProperty("db.pass");
                    if (filePass != null) pass = filePass;
                }
            }
        } catch (IOException ignored) {}

        if (url == null || url.isEmpty()) {
            url = "jdbc:mysql://localhost:3306/pontocerto?serverTimezone=UTC";
        }
        if (user == null || user.isEmpty()) user = "root";
        if (pass == null) pass = "";

        return new DbConfig(url, user, pass);
    }

    public String getUrl() { return url; }
    public String getUser() { return user; }
    public String getPass() { return pass; }
}