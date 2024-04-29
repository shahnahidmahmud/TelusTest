package com.telus.todo.repository;

import com.telus.todo.model.CompletionStatus;
import com.telus.todo.model.Todo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Repository
public class TodoRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Todo save(Todo todo) {
        final String sql = "INSERT INTO todos (description, completion_status) VALUES (?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                        ps.setString(1, todo.getDescription());
                        ps.setString(2, todo.getCompletionStatus().name());
                        return ps;
                    }
                },
                keyHolder);

        // Assuming the ID column is named 'id' and is of type LONG
        todo.setId(keyHolder.getKey().longValue());

        return todo;
    }

    public List<Todo> findAll() {
        return jdbcTemplate.query(
                "SELECT id, description, completion_status FROM todos",
                (rs, rowNum) ->
                        new Todo(
                                rs.getLong("id"),
                                rs.getString("description"),
                                CompletionStatus.valueOf(rs.getString("completion_status"))
                        )
        );
    }

    public Todo findById(Long id) {
        List<Todo> todos = jdbcTemplate.query(
                "SELECT id, description, completion_status FROM todos WHERE id = ?",
                new Object[]{id},
                (rs, rowNum) ->
                        new Todo(
                                rs.getLong("id"),
                                rs.getString("description"),
                                CompletionStatus.valueOf(rs.getString("completion_status"))
                        )
        );
        return todos.isEmpty() ? null : todos.get(0);  // Return null or first item
    }

    public int save(Long id, Todo todo) {
        return jdbcTemplate.update(
                "UPDATE todos SET description = ?, completion_status = ? WHERE id = ?",
                todo.getDescription(), todo.getCompletionStatus().name(), id
        );
    }

    public int deleteById(Long id) {
        return jdbcTemplate.update("DELETE FROM todos WHERE id = ?", id);
    }
}
