package com.tablelog.tablelogback.sample.repository;

import java.util.List;

import com.tablelog.tablelogback.sample.entity.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {

    List<Test> findByName(String name);

    Slice<Test> findAllBy(Pageable pageable);
}
