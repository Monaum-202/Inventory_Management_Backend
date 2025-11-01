package com.monaum.Rapid_Global.module.master.paymentMethod;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Monaum Hossain
 * monaum.202@gmail.com
 * @since 29-Oct-25 9:49 PM
 */

@Repository
public interface RepoPaymentMethod extends JpaRepository<PaymentMethod, Long> {

}
