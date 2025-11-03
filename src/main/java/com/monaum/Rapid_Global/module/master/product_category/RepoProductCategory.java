package com.monaum.Rapid_Global.module.master.product_category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepoProductCategory extends JpaRepository<ProductCategory , Long> {

}
