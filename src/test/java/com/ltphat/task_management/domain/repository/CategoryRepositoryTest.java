package com.ltphat.task_management.domain.repository;

import com.ltphat.task_management.domain.model.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void findById_shouldReturnCategory_whenCategoryExists() {
        Category categoryToSave = new Category(null, "Work", "Work tasks", "blue", null);
        Category savedCategory = testEntityManager.persistAndFlush(categoryToSave);

        Optional<Category> foundCategory = categoryRepository.findById(savedCategory.getId());

        assertThat(foundCategory).isPresent();
        assertThat(foundCategory.get().getName()).isEqualTo("Work");
    }

    @Test
    void findCategoryByName_shouldReturnMatchingCategories() {
        testEntityManager.persist(new Category(null, "Personal", "Personal tasks", "green", null));
        testEntityManager.persist(new Category(null, "Work", "Project A", "blue", null));
        testEntityManager.persist(new Category(null, "Study", "Spring Boot study", "yellow", null));
        testEntityManager.flush();

        Page<Category> resultPage = categoryRepository.findCategoryByName("Work", PageRequest.of(0, 5));

        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getTotalElements()).isEqualTo(1);
        assertThat(resultPage.getContent()).hasSize(1);
        assertThat(resultPage.getContent().get(0).getName()).isEqualTo("Work");
    }

    @Test
    void findCategoryByName_shouldReturnEmptyPage_whenNoMatch() {
        testEntityManager.persist(new Category(null, "Personal", "Personal tasks", "green", null));
        testEntityManager.flush();

        Page<Category> resultPage = categoryRepository.findCategoryByName("Shopping", PageRequest.of(0, 5));

        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getTotalElements()).isEqualTo(0);
        assertThat(resultPage.getContent()).isEmpty();
    }
}
