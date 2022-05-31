package lk.ijse.dep8.orm;

import lk.ijse.dep8.orm.annotation.Entity;
import lk.ijse.dep8.orm.annotation.Id;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


/**
 * SessionFactory is the starting point of ORM
 *
 * @author Hashadhi-Jayasinghe
 * @since 1.0.0
 */

public class SessionFactory {

    private List<Class<?>> entityClassList = new ArrayList<>();
    private Connection connection;

    /**
     * Add classes that have been annotated with <code>@Entity</code> annotation
     * @param entityClass
     * @return SessionFactory
     * @throws RuntimeException If the class is not annotated with <code>@Entity</code> annotation
     */

    public SessionFactory addAnnotatedClass(Class<?> entityClass) {

        if (entityClass.getDeclaredAnnotation(Entity.class) == null) {
            throw new RuntimeException("Invalid entity class");
        }

        entityClassList.add(entityClass);
        return this;
    }

    /**
     *
     * Set the Connection
     *
     * @param connection Set an initialized JDBC Connectoin
     * @return SessionFactory
     */

    public SessionFactory setConnection(Connection connection) {
        this.connection = connection;
        return this;
    }

    /**
     * Validate whether everything is okay
     *
     * @return SessionFactory
     * @throws RuntimeException Throws when there is no connection
     */

    public SessionFactory build(){
        if (this.connection == null) {
            throw new RuntimeException("Failed to build without a connection");
        }

        return this;
    }

    /**
     * Bootstrap the ORM framework and create the tables
     * @throws SQLException
     */


    public void bootstrap() throws SQLException {
        for (Class<?> entity : entityClassList) {
            String tableName = entity.getDeclaredAnnotation(Entity.class).value();
            if(tableName.trim().isEmpty()) tableName = entity.getSimpleName();
            Field[] fields = entity.getDeclaredFields();
            List<String> columns = new ArrayList<>();

            String primaryKey=null;
            for (Field field : fields) {

                Id primaryKeyField = field.getDeclaredAnnotation(Id.class);
                if (primaryKeyField != null) {
                    primaryKey = field.getName();
                    continue;
                }
                columns.add(field.getName());

            }

            if(primaryKey == null) throw new RuntimeException("Entity without a primary key");

            StringBuilder sb = new StringBuilder();
            sb.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append("(");
            for (String column : columns) {
                sb.append(column + " VARCHAR(255), ");
            }
            sb.append(primaryKey).append(" VARCHAR(255) PRIMARY KEY);");
            Statement stm = connection.createStatement();
            stm.execute(sb.toString());
        }
    }
}
