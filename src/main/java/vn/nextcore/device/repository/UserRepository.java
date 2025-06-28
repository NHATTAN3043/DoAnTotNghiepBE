package vn.nextcore.device.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.nextcore.device.entity.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    User findUserByIdAndDeletedAtIsNull(Long id);

    boolean existsByEmailAndDeletedAtIsNull(String email);

    List<User> findAllByDeletedAtIsNull(Sort sort);

    List<User> findAllByRoleIdAndDeletedAtIsNull(Long roleId);

    @Query(value = "SELECT * FROM \"NGUOIDUNG\" WHERE LOWER(unaccent(ten)) LIKE LOWER(unaccent(CONCAT('%', :keyword, '%'))) AND deleted_at is null ", nativeQuery = true)
    List<User> searchByTenIgnoreCaseAndAccent(@Param("keyword") String keyword);

    @Transactional
    @Modifying
    @Query("update User u set u.password = ?2 where u.email = ?1")
    void updatePassword(String email, String password);
}
