package lk.ijse.dep8.orm;

import lk.ijse.dep8.orm.annotation.Entity;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class SessionFactory {

    private List<Class<?>> entityClassList = new ArrayList<>();
    private Connection connection;

    public SessionFactory addAnnotatedClass(Class<?> entityClass) {

        if (entityClass.getDeclaredAnnotation(Entity.class) == null) {
            throw new RuntimeException("Invalid entity class");
        }

        entityClassList.add(entityClass);
        return this;
    }

    public SessionFactory setConnection(Connection connection) {
        this.connection = connection;
        return this;
    }



}
