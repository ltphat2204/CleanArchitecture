package com.ltphat.task_management.domain.repository;

import com.ltphat.task_management.domain.model.Category;
import com.ltphat.task_management.domain.model.Task;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    private Task task;

    @BeforeEach
    void setUp() {
        Category category = new Category(null, "Work", "Work related tasks", "blue", Instant.now());
        Category savedCategory = testEntityManager.persist(category);
        Task temp = new Task(null, "Task 1", "Description of Task 1", "Pending", category);
        task = testEntityManager.persistAndFlush(temp);
    }

    @Test
    void testFindByNameContainingIgnoreCase() {
        var pageable = PageRequest.of(0, 5);
        var tasks = taskRepository.findByNameContainingIgnoreCase("task", pageable);

        assertNotNull(tasks);
        assertFalse(tasks.isEmpty());
        assertEquals("Task 1", tasks.getContent().get(0).getName());
    }

    @Test
    void testFindById() {
        Optional<Task> foundTask = taskRepository.findById(task.getId());

        assertTrue(foundTask.isPresent());
        assertEquals("Task 1", foundTask.get().getName());
    }
}

