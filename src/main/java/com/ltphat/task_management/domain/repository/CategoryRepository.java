package com.ltphat.task_management.domain.repository;

import com.ltphat.task_management.domain.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{
    Page<Category> findCategoryByName(String name, Pageable pageable);
}
