package com.monaum.Rapid_Global.module.expenses.supplier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepo extends JpaRepository <Supplier , Long > {

}
