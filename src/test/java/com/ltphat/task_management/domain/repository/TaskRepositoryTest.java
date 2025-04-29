package com.ltphat.task_management.domain.repository;

import com.ltphat.task_management.domain.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    private Task task;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        task = new Task(1L, "Task 1", "Description of Task 1", "Pending");
        taskRepository.save(task);
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
        var foundTask = taskRepository.findById(1L);

        assertTrue(foundTask.isPresent());
        assertEquals("Task 1", foundTask.get().getName());
    }
}

