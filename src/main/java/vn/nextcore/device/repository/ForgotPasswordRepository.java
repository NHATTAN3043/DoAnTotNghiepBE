package vn.nextcore.device.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.nextcore.device.entity.ForgotPassword;
import vn.nextcore.device.entity.User;

import java.util.Optional;

@Repository
public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, Long> {
    ForgotPassword findByUser(User user);

    Optional<ForgotPassword> findByOtpAndUser(Integer otp, User user);
}
