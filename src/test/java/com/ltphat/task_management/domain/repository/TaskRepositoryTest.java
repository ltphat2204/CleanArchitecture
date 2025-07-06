package com.ltphat.task_management.domain.repository;

import com.ltphat.task_management.domain.model.Category;
import com.ltphat.task_management.domain.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
    void testFindByNameContainingIgnoreCase_CaseInsensitive() {
        var pageable = PageRequest.of(0, 5);
        // Tìm kiếm bằng chữ "TASK" (in hoa)
        var tasks = taskRepository.findByNameContainingIgnoreCase("TASK", pageable);

        assertNotNull(tasks);
        assertFalse(tasks.isEmpty());
        assertEquals(1, tasks.getTotalElements());
        assertEquals("Task 1", tasks.getContent().get(0).getName());
    }

    @Test
    void testFindByNameContainingIgnoreCase_NotFound() {
        var pageable = PageRequest.of(0, 5);
        var tasks = taskRepository.findByNameContainingIgnoreCase("NonExistentTask", pageable);

        assertNotNull(tasks);
        assertTrue(tasks.isEmpty());
        assertEquals(0, tasks.getTotalElements());
    }

    @Test
    void testSave_CreateNewTask() {
        Category existingCategory = testEntityManager.find(Category.class, task.getCategory().getId());
        Task newTask = new Task(null, "Task 2", "A brand new task", "Todo", existingCategory);

        Task savedTask = taskRepository.save(newTask);

        assertNotNull(savedTask);
        assertNotNull(savedTask.getId());
        assertEquals("Task 2", savedTask.getName());

        Task foundTask = testEntityManager.find(Task.class, savedTask.getId());
        assertEquals("Task 2", foundTask.getName());
    }

    @Test
    void testSave_UpdateExistingTask() {
        Optional<Task> taskToUpdateOpt = taskRepository.findById(task.getId());
        assertTrue(taskToUpdateOpt.isPresent());

        Task taskToUpdate = taskToUpdateOpt.get();
        taskToUpdate.setStatus("Completed");
        taskRepository.save(taskToUpdate);

        Task updatedTask = testEntityManager.find(Task.class, task.getId());
        assertEquals("Completed", updatedTask.getStatus());
        assertEquals("Task 1", updatedTask.getName());
    }

    @Test
    void testDeleteById() {

        Long taskId = task.getId();

        assertTrue(taskRepository.findById(taskId).isPresent());

        taskRepository.deleteById(taskId);
        testEntityManager.flush();

        Optional<Task> deletedTaskOpt = taskRepository.findById(taskId);
        assertFalse(deletedTaskOpt.isPresent());
    }

    @Test
    void testFindById() {
        Optional<Task> foundTask = taskRepository.findById(task.getId());

        assertTrue(foundTask.isPresent());
        assertEquals("Task 1", foundTask.get().getName());
    }
}

