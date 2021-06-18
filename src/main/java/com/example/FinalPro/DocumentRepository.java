package com.example.FinalPro;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    // SELECT the name and id from the Document model and order by the upload time in descending order
    @Query("SELECT new Document(d.id, d.name) FROM Document d ORDER BY d.uploadTime DESC")
    List<Document> findAll(); // find all the files in the mysql database
}
