package com.course.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.course.dto.post.PostResponse;
import com.course.entity.Post;
import com.course.entity.User;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    List<Post> findAllByOrderByCreatedAtDesc();
    
    // For user-specific posts
    List<Post> findByUserOrderByCreatedAtDesc(User user);
    
    // Find by user ID
    @Query("SELECT p FROM Post p WHERE p.user.id = :userId ORDER BY p.createdAt DESC")
    List<Post> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Query("""
        SELECT p FROM Post p
        JOIN FETCH p.user
        ORDER BY p.createdAt DESC
    """)
    List<Post> findTop5WithUser(Pageable pageable);

    @Query("""
        SELECT p FROM Post p
        JOIN FETCH p.user
        ORDER BY p.createdAt DESC
    """)
    List<Post> findAllWithUser();

    @Query("""
        SELECT new com.course.dto.post.PostResponse(
            p.id,
            p.title,
            p.content,
            COALESCE(p.imageUrl, ''),
            p.createdAt,
            p.updatedAt,
            u.fullName
        )
        FROM Post p
        JOIN p.user u
        ORDER BY p.createdAt DESC
    """)
    Page<PostResponse> findAllPostResponse(Pageable pageable);

}
