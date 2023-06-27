import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class conexion {
    private final String url="jdbc:postgresql://localhost:5432/hr";
    private final String user="poli01";
    private final String password="poli01";

    public Connection conectar(){

        Connection con = null;
        try {
            con = DriverManager.getConnection(url, user, password);

            if (con != null) {
                System.out.println("Connected to the PostgreSQL server successfully.");
            } else {
                System.out.println("Failed to make connection!");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return con;
    }

    public static void main(String[] args) {
        conexion app=new conexion();
        app.conectar();
    }

}
