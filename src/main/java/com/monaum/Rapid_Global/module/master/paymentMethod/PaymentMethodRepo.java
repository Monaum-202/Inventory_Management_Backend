package com.monaum.Rapid_Global.module.master.paymentMethod;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 25-Oct-25 10:32 PM
 */

@Repository
public interface PaymentMethodRepo extends JpaRepository<PaymentMethod, Integer> {
}
